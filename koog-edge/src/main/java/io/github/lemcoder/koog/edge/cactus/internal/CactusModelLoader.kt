package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.prompt.llm.LLModel
import android.content.Context
import com.cactus.CactusContextInitializer
import com.cactus.CactusInitParams
import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelLoader


object CactusModelLoader : LocalModelLoader<CactusLM?> {
    private var isInitialized = false

    fun initializeIfNecessary(context: Context) {
        if (isInitialized) return
        isInitialized = true
        CactusContextInitializer.initialize(context)
    }

    override suspend fun loadModel(model: LLModel): CactusLM? {
        val lm = CactusLM(
            enableToolFiltering = false
        )
        if (lm.isLoaded()) return lm
        lm.initializeModel(
            CactusInitParams(
                model = model.id
            )
        )

        return lm.takeIf { it.isLoaded() }
    }
}