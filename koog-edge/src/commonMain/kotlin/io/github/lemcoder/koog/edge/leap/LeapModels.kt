package io.github.lemcoder.koog.edge.leap

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.edge.LocalModel
import io.github.lemcoder.koog.edge.provider.LocalLLMProvider

sealed interface LeapModels : LocalModel {
    data object Chat : LocalModel {
        val LFM2_1_2B_Tool = LLModel(
            provider = LocalLLMProvider,
            id = "LFM2-1.2B-Tool",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 32_768,
        )

        val LFM2_1_2B_Instruct = LLModel(
            provider = LocalLLMProvider,
            id = "LFM2.5-1.2B-Instruct",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 32_768,
        )

        val LFM2_1_2B_Thinking = LLModel(
            provider = LocalLLMProvider,
            id = "LFM2.5-1.2B-Thinking",
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
        LeapModels.Chat.LFM2_1_2B_Instruct.id -> LeapModels.Chat.LFM2_1_2B_Instruct
        LeapModels.Chat.LFM2_1_2B_Thinking.id -> LeapModels.Chat.LFM2_1_2B_Thinking
        else -> null
    }
}