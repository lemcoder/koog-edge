package io.github.lemcoder.koog.edge.leap

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.edge.LocalModel
import io.github.lemcoder.koog.edge.provider.LocalLLMProvider

sealed interface LeapModels : LocalModel {
    data object Chat : LocalModel {
        val LFM2_1_2B_Tool = LLModel(
            provider = LocalLLMProvider,
            id = "lfm2-1.2b-tool-20250912-8da4w",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 32_768,
        )
    }
}

internal fun getLeapLLModelById(modelId: String): LLModel? {
    return when (modelId) {
        LeapModels.Chat.LFM2_1_2B_Tool.id -> LeapModels.Chat.LFM2_1_2B_Tool
        else -> null
    }
}