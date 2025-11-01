package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.prompt.llm.LLModel
import android.content.Context
import com.cactus.CactusContextInitializer
import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelLoader
import kotlinx.coroutines.flow.Flow


class CactusModelLoader : LocalModelLoader<Unit> {
    override val state: Flow<LocalModelLoader.State>
        get() = TODO("Not yet implemented")

    override suspend fun loadModel(model: LLModel): Unit {
        val lm = CactusLM()
        lm.downloadModel(model.id)
    }

    fun initialize(context: Context) {
        CactusContextInitializer.initialize(context)
    }
}