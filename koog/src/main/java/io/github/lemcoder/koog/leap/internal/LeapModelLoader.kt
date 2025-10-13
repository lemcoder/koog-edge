package io.github.lemcoder.koog.leap.internal

import ai.liquid.leap.LeapClient
import ai.liquid.leap.LeapModelLoadingException
import ai.liquid.leap.ModelLoadingOptions
import ai.liquid.leap.ModelRunner
import io.github.lemcoder.koog.AndroidLocalModel
import io.github.lemcoder.koog.LocalModelLoader
import io.github.lemcoder.koog.leap.LeapModel
import io.github.lemcoder.koog.log.AndroidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class LeapModelLoader(
    private val modelsPath: String,
    private val options: ModelLoadingOptions = ModelLoadingOptions(),
) : LocalModelLoader<ModelRunner?> {
    private val mutex = Mutex()
    private var loadingJob: Job? = null
    private var currentRunner: ModelRunner? = null
    private val loadingState = MutableSharedFlow<LocalModelLoader.State>(replay = 1)

    override val state: Flow<LocalModelLoader.State> = loadingState

    override suspend fun loadModel(
        model: AndroidLocalModel,
    ): ModelRunner? = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (loadingJob?.isActive == true) {
                throw IllegalStateException("A model is already loading")
            }
            val leapModel = model as LeapModel

            val lastState = loadingState.replayCache.lastOrNull()
            if (lastState is LocalModelLoader.State.Success) {
                val model = lastState.model as LeapModel
                if (leapModel.llmModel.id == model.llmModel.id) {
                    AndroidLogger.w("Model is already loaded")
                    return@withContext currentRunner!!
                }
            }

            loadingJob = launch {
                loadingState.emit(LocalModelLoader.State.Loading)
                try {
                    val modelFile = leapModel.resolveModelFile(modelsPath)
                    currentRunner = LeapClient.loadModel(
                        bundlePath = modelFile.path,
                        options = options
                    )
                    loadingState.emit(LocalModelLoader.State.Success(leapModel))
                } catch (e: LeapModelLoadingException) {
                    AndroidLogger.error("Error loading model: ${e.message}", e)
                    loadingState.emit(LocalModelLoader.State.Error(e))
                }
            }
            loadingJob?.join()
            currentRunner
        }
    }
}

fun LeapModel.resolveModelFile(
    modelsPath: String,
): File {
    return when (this) {
        is LeapModel.LFM2_1_2B_Tool -> File(
            modelsPath,
            "${this.llmModel.id}.bundle"
        )
    }
}
