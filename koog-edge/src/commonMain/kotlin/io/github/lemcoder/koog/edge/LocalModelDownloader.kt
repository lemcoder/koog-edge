package io.github.lemcoder.koog.edge

import ai.koog.prompt.llm.LLModel
import kotlinx.coroutines.flow.Flow

interface LocalModelDownloader {
    /**
     * Downloads the specified local model and
     * @param model the model to download
     * @return a `Flow` emitting the download progress as a `Float` between 0.0 and 1.0.
     */
    suspend fun downloadModel(model: LLModel): Flow<Float>
}