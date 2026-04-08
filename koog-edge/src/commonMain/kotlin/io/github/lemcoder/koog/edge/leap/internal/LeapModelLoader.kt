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
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

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
                val modelPath = "${modelsPath}/${model.id}"
                KoogEdgeLog.w { "Loading model ${model.id} from path: $model at $modelPath" }
                SystemFileSystem.delete(Path(modelPath), false)

                if (loadingJob?.isActive == true) {
                    throw IllegalStateException("A model is already loading")
                }

                loadingJob = launch {
                    try {
                        currentRunner =
                            downloader.loadModel(
                                modelName = model.id,
                                quantizationSlug =
                                    "Q4_K_M", // Load 4-bit quantized models by default
                                modelLoadingOptions = options,
                                forceDownload = true,
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
