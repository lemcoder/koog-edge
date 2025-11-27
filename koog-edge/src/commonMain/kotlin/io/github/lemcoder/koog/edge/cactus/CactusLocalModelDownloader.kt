package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.llm.LLModel
import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelDownloader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CactusLocalModelDownloader : LocalModelDownloader {
    override suspend fun downloadModel(model: LLModel): Flow<Float> = flow {
        val lm = CactusLM(
            enableToolFiltering = false
        )
        // TODO : implement progress tracking for Cactus SDK
        emit(0f)
        lm.downloadModel(
            model = model.id
        )
        emit(1f)
    }
}