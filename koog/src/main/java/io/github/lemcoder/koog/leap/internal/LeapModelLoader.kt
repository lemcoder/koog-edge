package io.github.lemcoder.koog.leap.internal

import ai.koog.prompt.llm.LLModel
import ai.liquid.leap.LeapClient
import ai.liquid.leap.LeapModelLoadingException
import ai.liquid.leap.ModelLoadingOptions
import ai.liquid.leap.ModelRunner
import io.github.lemcoder.koog.LocalModelLoader
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

internal class LeapModelLoader(
    private val modelsPath: String,
    private val options: ModelLoadingOptions = ModelLoadingOptions(),
) : LocalModelLoader<ModelRunner?> {
    private val mutex = Mutex()
    private var loadingJob: Job? = null
    private var currentRunner: ModelRunner? = null
    private val loadingState = MutableSharedFlow<LocalModelLoader.State>(replay = 1)

    override val state: Flow<LocalModelLoader.State> = loadingState

    override suspend fun loadModel(
        model: LLModel,
    ): ModelRunner? = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (loadingJob?.isActive == true) {
                throw IllegalStateException("A model is already loading")
            }
            val leapModel = model

            val lastState = loadingState.replayCache.lastOrNull()
            if (lastState is LocalModelLoader.State.Success) {
                if (leapModel.id == lastState.modelId) {
                    AndroidLogger.w("Model is already loaded")
                    return@withContext currentRunner!!
                }
            }

            loadingJob = launch {
                loadingState.emit(LocalModelLoader.State.Loading)
                try {
                    val modelFile = File(
                        modelsPath,
                        "${model.id}.bundle"
                    )

                    currentRunner = LeapClient.loadModel(
                        bundlePath = modelFile.path,
                        options = options
                    )
                    loadingState.emit(LocalModelLoader.State.Success(leapModel.id))
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