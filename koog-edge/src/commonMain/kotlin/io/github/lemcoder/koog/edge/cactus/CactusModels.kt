package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.edge.LocalModel
import io.github.lemcoder.koog.edge.provider.LocalLLMProvider

/**
 * Model: Qwen 3 0.6B
 *    Slug: qwen3-0.6
 *    Size: 394 MB
 *    Tool calling: true
 *    Vision: false
 * Model: Qwen 3 1.7B
 *    Slug: qwen3-1.7
 *    Size: 1161 MB
 *    Tool calling: true
 *    Vision: false
 * Model: LFM 2 350M
 *    Slug: lfm2-350m
 *    Size: 233 MB
 *    Tool calling: true
 *    Vision: false
 * Model: LFM 2 700M
 *    Slug: lfm2-700m
 *    Size: 467 MB
 *    Tool calling: true
 *    Vision: false
 * Model: LFM 2 1.2B
 *    Slug: lfm2-1.2b
 *    Size: 722 MB
 *    Tool calling: true
 *    Vision: false
 * Model: LFM 2 1.2B RAG
 *    Slug: lfm2-1.2b-rag
 *    Size: 722 MB
 *    Tool calling: true
 *    Vision: false
 */
sealed interface CactusModels : LocalModel {
    data object Chat : LocalModel {
        val Qwen3_0_6B = LLModel(
            provider = LocalLLMProvider,
            id = "qwen3-0.6",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )

        val Qwen3_1_7B = LLModel(
            provider = LocalLLMProvider,
            id = "qwen3-1.7",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )

        val LFM2_350M = LLModel(
            provider = LocalLLMProvider,
            id = "lfm2-350m",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )

        val LFM2_700M = LLModel(
            provider = LocalLLMProvider,
            id = "lfm2-700m",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )

        val LFM2_1_2B = LLModel(
            provider = LocalLLMProvider,
            id = "lfm2-1.2b",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 16_384,
        )

        val LFM2_1_2B_RAG = LLModel(
            provider = LocalLLMProvider,
            id = "lfm2-1.2b-rag",
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

        CactusModels.Chat.LFM2_350M.id -> {
            CactusModels.Chat.LFM2_350M
        }

        CactusModels.Chat.LFM2_700M.id -> {
            CactusModels.Chat.LFM2_700M
        }

        CactusModels.Chat.LFM2_1_2B.id -> {
            CactusModels.Chat.LFM2_1_2B
        }

        CactusModels.Chat.LFM2_1_2B_RAG.id -> {
            CactusModels.Chat.LFM2_1_2B_RAG
        }

        else -> null
    }
}