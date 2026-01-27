package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.prompt.llm.LLModel
import com.cactus.CactusInitParams
import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelLoader
import io.github.lemcoder.koog.edge.log.KoogEdgeLog

actual fun cactusModelLoader(context: Any?): LocalModelLoader<CactusLM?> {
    return CactusModelLoader
}

internal object CactusModelLoader : LocalModelLoader<CactusLM?> {
    private var lastLoadedModel: LLModel? = null
    private var lastCreatedExecutor: CactusLM? = null

    override suspend fun loadModel(model: LLModel): CactusLM? {
        KoogEdgeLog.info("Loading Cactus model: ${model.id}")

        if (lastLoadedModel?.id == model.id) {
            val cached = lastCreatedExecutor
            if (cached != null && cached.isLoaded()) {
                KoogEdgeLog.warning("Using cached Cactus model executor for model: ${model.id}")
                return cached
            }
            KoogEdgeLog.warning(
                "Cached Cactus model executor is not loaded, creating a new one for model: ${model.id}"
            )
        }

        val lm = CactusLM(enableToolFiltering = false)
        lm.initializeModel(CactusInitParams(model = model.id))

        lastCreatedExecutor = lm
        lastLoadedModel = model
        return lm.takeIf { it.isLoaded() }
    }
}
