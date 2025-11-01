package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.edge.AndroidLocalModel
import io.github.lemcoder.koog.edge.provider.AndroidLocalLLMProvider

/**
 * Model: Qwen 3 0.6B
 *    Slug: qwen3-0.6
 *    Size: 394 MB
 *    Tool calling: true
 *    Vision: false
 *  Model: Qwen 3 1.7B
 *    Slug: qwen3-1.7
 *    Size: 1161 MB
 *    Tool calling: true
 *    Vision: false
 */
sealed interface CactusModels : AndroidLocalModel {
    data object Chat : AndroidLocalModel {
        val Qwen3_0_6B = LLModel(
            provider = AndroidLocalLLMProvider,
            id = "qwen3-0.6",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )

        val Qwen3_1_7B = LLModel(
            provider = AndroidLocalLLMProvider,
            id = "qwen3-1.7",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )
    }
}

internal fun getCactusLLMModelById(modelId: String): LLModel? {
    return when (modelId) {
        CactusModels.Chat.Qwen3_0_6B.id -> {
            CactusModels.Chat.Qwen3_0_6B
        }

        CactusModels.Chat.Qwen3_1_7B.id -> {
            CactusModels.Chat.Qwen3_1_7B
        }

        else -> null
    }
}