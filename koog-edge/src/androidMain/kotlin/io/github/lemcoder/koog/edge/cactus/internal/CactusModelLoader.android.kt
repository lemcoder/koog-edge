package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.prompt.llm.LLModel
import android.content.Context
import com.cactus.CactusContextInitializer
import com.cactus.CactusInitParams
import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelLoader
import io.github.lemcoder.koog.edge.log.KoogEdgeLog

actual fun cactusModelLoader(context: Any?): LocalModelLoader<CactusLM?> = CactusModelLoader.apply {
    if (context is Context) {
        initializeIfNecessary(context)
    } else {
        KoogEdgeLog.error("CactusModelLoader initialization failed: context is not of type android.content.Context")
        throw IllegalArgumentException("Expected Android Context for CactusModelLoader initialization")
    }
}

internal object CactusModelLoader :
    LocalModelLoader<CactusLM?> {
    private var isInitialized = false
    private var lastLoadedModel: LLModel? = null
    private var lastCreatedExecutor: CactusLM? = null

    fun initializeIfNecessary(context: Context) {
        if (isInitialized) return
        isInitialized = true
        CactusContextInitializer.initialize(context)
    }

    override suspend fun loadModel(model: LLModel): CactusLM? {
        KoogEdgeLog.info("Loading Cactus model: ${model.id}")

        if (lastLoadedModel?.id == model.id) {
            val cached = lastCreatedExecutor
            if (cached != null && cached.isLoaded()) {
                KoogEdgeLog.warning("Using cached Cactus model executor for model: ${model.id}")
                return cached
            }
            KoogEdgeLog.warning("Cached Cactus model executor is not loaded, creating a new one for model: ${model.id}")
        }

        val lm = CactusLM(
            enableToolFiltering = false
        )
        lm.initializeModel(
            CactusInitParams(
                model = model.id
            )
        )

        lastCreatedExecutor = lm
        lastLoadedModel = model
        return lm.takeIf { it.isLoaded() }
    }
}
