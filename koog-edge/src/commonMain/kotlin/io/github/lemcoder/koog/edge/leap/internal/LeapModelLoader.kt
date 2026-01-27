package io.github.lemcoder.koog.edge.leap.internal

import ai.koog.prompt.llm.LLModel
import ai.liquid.leap.LeapModelLoadingException
import ai.liquid.leap.ModelLoadingOptions
import ai.liquid.leap.ModelRunner
import ai.liquid.leap.manifest.LeapDownloader
import ai.liquid.leap.manifest.LeapDownloaderConfig
import io.github.lemcoder.koog.edge.LocalModelLoader
import io.github.lemcoder.koog.edge.log.KoogEdgeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class LeapModelLoader(
    private val modelsPath: String,
    private val options: ModelLoadingOptions = ModelLoadingOptions(cpuThreads = 2),
) : LocalModelLoader<ModelRunner?> {
    private val mutex = Mutex()
    private var loadingJob: Job? = null
    private var currentRunner: ModelRunner? = null

    private val downloader = LeapDownloader(config = LeapDownloaderConfig(saveDir = modelsPath))

    override suspend fun loadModel(model: LLModel): ModelRunner? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (loadingJob?.isActive == true) {
                    throw IllegalStateException("A model is already loading")
                }

                loadingJob = launch {
                    try {
                        currentRunner =
                            downloader.loadModel(
                                modelSlug = model.id,
                                quantizationSlug =
                                    "Q_4_0", // Load 4-bit quantized models by default
                                modelLoadingOptions = options,
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
