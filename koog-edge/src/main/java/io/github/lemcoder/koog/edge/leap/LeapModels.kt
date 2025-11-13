package io.github.lemcoder.koog.edge.leap

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.edge.AndroidLocalModel
import io.github.lemcoder.koog.edge.provider.AndroidLocalLLMProvider

sealed interface LeapModels : AndroidLocalModel {
    data object Chat : AndroidLocalModel {
        val LFM2_1_2B_Tool = LLModel(
            provider = AndroidLocalLLMProvider,
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