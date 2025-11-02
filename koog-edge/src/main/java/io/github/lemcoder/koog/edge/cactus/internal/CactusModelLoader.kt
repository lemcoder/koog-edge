package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.prompt.llm.LLModel
import android.content.Context
import com.cactus.CactusContextInitializer
import com.cactus.CactusInitParams
import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelLoader


class CactusModelLoader : LocalModelLoader<CactusLM?> {
    override suspend fun loadModel(model: LLModel): CactusLM? {
        val lm = CactusLM()
        lm.initializeModel(
            CactusInitParams(
                model = model.id
            )
        )

        return lm.takeIf { it.isLoaded() }
    }

    fun initialize(context: Context) {
        CactusContextInitializer.initialize(context)
    }
}