package io.github.lemcoder.koog.edge.leap.internal

import ai.koog.prompt.llm.LLModel
import ai.liquid.leap.LeapClient
import ai.liquid.leap.LeapModelLoadingException
import ai.liquid.leap.ModelLoadingOptions
import ai.liquid.leap.ModelRunner
import io.github.lemcoder.koog.edge.LocalModelLoader
import io.github.lemcoder.koog.edge.log.AndroidEdgeLogger
import io.github.lemcoder.koog.edge.log.KoogEdgeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

internal class LeapModelLoader(
    private val modelsPath: String,
    private val options: ModelLoadingOptions = ModelLoadingOptions.build {
        this.cpuThreads = 2
    },
) : LocalModelLoader<ModelRunner?> {
    private val mutex = Mutex()
    private var loadingJob: Job? = null
    private var currentRunner: ModelRunner? = null

    override suspend fun loadModel(
        model: LLModel,
    ): ModelRunner? = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (loadingJob?.isActive == true) {
                throw IllegalStateException("A model is already loading")
            }

            loadingJob = launch {
                try {
                    val modelFile = File(
                        modelsPath,
                        "${model.id}.bundle"
                    )

                    currentRunner = LeapClient.loadModel(
                        bundlePath = modelFile.path,
                        options = options
                    )
                } catch (e: LeapModelLoadingException) {
                    KoogEdgeLog.error("Error loading model: ${e.message}", e)
                }
            }
            loadingJob?.join()
            currentRunner
        }
    }
}