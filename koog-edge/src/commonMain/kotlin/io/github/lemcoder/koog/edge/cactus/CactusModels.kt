package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.edge.LocalModel
import io.github.lemcoder.koog.edge.provider.LocalLLMProvider

/**
 * CactusModel(created_at=2025-12-30T16:44:34.979918+00:00, slug=functiongemma-270m,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/functiongemma-270m-it.zip,
 * size_mb=182, supports_tool_calling=true, supports_vision=false, name=FunctionGemma 3 270M,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-12-30T16:44:34.979918+00:00,
 * slug=lfm2-1.2b-tool,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/lfm2-1.2b-tool.zip,
 * size_mb=729, supports_tool_calling=true, supports_vision=false, name=LFM 2 1.2B Tool,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-08-24T00:04:55.975939+00:00,
 * slug=qwen3-0.6,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/qwen3-0.6.zip,
 * size_mb=394, supports_tool_calling=true, supports_vision=false, name=Qwen 3 0.6B,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-09-23T01:01:19.968534+00:00,
 * slug=qwen3-1.7,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/qwen3-1.7.zip,
 * size_mb=1161, supports_tool_calling=true, supports_vision=false, name=Qwen 3 1.7B,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-10-24T18:13:59.778701+00:00,
 * slug=lfm2-1.2b,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/lfm2-1.2b.zip,
 * size_mb=722, supports_tool_calling=true, supports_vision=false, name=LFM 2 1.2B,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-11-22T23:52:30.351919+00:00,
 * slug=lfm2-1.2b-rag,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/lfm2-1.2b-rag.zip,
 * size_mb=722, supports_tool_calling=true, supports_vision=false, name=LFM 2 1.2B RAG,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-10-24T18:12:15.056461+00:00,
 * slug=lfm2-350m,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/lfm2-350m.zip,
 * size_mb=233, supports_tool_calling=true, supports_vision=false, name=LFM 2 350M,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-10-24T18:13:27.642922+00:00,
 * slug=lfm2-700m,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/lfm2-700m.zip,
 * size_mb=467, supports_tool_calling=true, supports_vision=false, name=LFM 2 700M,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-12-30T01:41:06.394756+00:00,
 * slug=qwen3-0.6-pro,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/v1.3/qwen3-0.6b-pro.zip,
 * size_mb=872, supports_tool_calling=true, supports_vision=false, name=Qwen 3 0.6B Pro,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-12-30T01:41:06.394756+00:00,
 * slug=qwen3-1.7-pro,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/v1.3/qwen3-1.7b-pro.zip,
 * size_mb=2529, supports_tool_calling=true, supports_vision=false, name=Qwen 3 1.7B Pro,
 * isDownloaded=false, quantization=8) CactusModel(created_at=2025-12-30T01:41:06.394756+00:00,
 * slug=functiongemma-270m-pro,
 * download_url=https://vlqqczxwyaodtcdmdmlw.supabase.co/storage/v1/object/public/cactus-models/v1.3/functiongemma-270m-it-pro.zip,
 * size_mb=279, supports_tool_calling=true, supports_vision=false, name=FunctionGemma 3 270M Pro,
 * isDownloaded=false, quantization=8)
 */
sealed interface CactusModels : LocalModel {
    data object Chat : LocalModel {
        val FunctionGemma_270M =
            LLModel(
                provider = LocalLLMProvider,
                id = "functiongemma-270m",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val LFM2_1_2B_Tool =
            LLModel(
                provider = LocalLLMProvider,
                id = "lfm2-1.2b-tool",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val Qwen3_0_6B =
            LLModel(
                provider = LocalLLMProvider,
                id = "qwen3-0.6",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val Qwen3_1_7B =
            LLModel(
                provider = LocalLLMProvider,
                id = "qwen3-1.7",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val LFM2_1_2B =
            LLModel(
                provider = LocalLLMProvider,
                id = "lfm2-1.2b",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val LFM2_1_2B_RAG =
            LLModel(
                provider = LocalLLMProvider,
                id = "lfm2-1.2b-rag",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val LFM2_350M =
            LLModel(
                provider = LocalLLMProvider,
                id = "lfm2-350m",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val LFM2_700M =
            LLModel(
                provider = LocalLLMProvider,
                id = "lfm2-700m",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val Qwen3_0_6B_Pro =
            LLModel(
                provider = LocalLLMProvider,
                id = "qwen3-0.6-pro",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val Qwen3_1_7B_Pro =
            LLModel(
                provider = LocalLLMProvider,
                id = "qwen3-1.7-pro",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )

        val FunctionGemma_270M_Pro =
            LLModel(
                provider = LocalLLMProvider,
                id = "functiongemma-270m-pro",
                capabilities = listOf(LLMCapability.Tools, LLMCapability.Completion),
                contextLength = 16_384,
            )
    }
}

internal fun getCactusLLMModelById(modelId: String): LLModel? {
    return when (modelId) {
        CactusModels.Chat.FunctionGemma_270M.id -> {
            CactusModels.Chat.FunctionGemma_270M
        }

        CactusModels.Chat.LFM2_1_2B_Tool.id -> {
            CactusModels.Chat.LFM2_1_2B_Tool
        }

        CactusModels.Chat.Qwen3_0_6B.id -> {
            CactusModels.Chat.Qwen3_0_6B
        }

        CactusModels.Chat.Qwen3_1_7B.id -> {
            CactusModels.Chat.Qwen3_1_7B
        }

        CactusModels.Chat.LFM2_1_2B.id -> {
            CactusModels.Chat.LFM2_1_2B
        }

        CactusModels.Chat.LFM2_1_2B_RAG.id -> {
            CactusModels.Chat.LFM2_1_2B_RAG
        }

        CactusModels.Chat.LFM2_350M.id -> {
            CactusModels.Chat.LFM2_350M
        }

        CactusModels.Chat.LFM2_700M.id -> {
            CactusModels.Chat.LFM2_700M
        }

        CactusModels.Chat.Qwen3_0_6B_Pro.id -> {
            CactusModels.Chat.Qwen3_0_6B_Pro
        }

        CactusModels.Chat.Qwen3_1_7B_Pro.id -> {
            CactusModels.Chat.Qwen3_1_7B_Pro
        }

        CactusModels.Chat.FunctionGemma_270M_Pro.id -> {
            CactusModels.Chat.FunctionGemma_270M_Pro
        }

        else -> null
    }
}
