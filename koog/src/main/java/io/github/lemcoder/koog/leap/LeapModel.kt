package io.github.lemcoder.koog.leap

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import io.github.lemcoder.koog.AndroidLocalModel
import io.github.lemcoder.koog.provider.AndroidLocalLLMProvider

sealed interface LeapModel : AndroidLocalModel {
    val llmModel: LLModel

    data object LFM2_1_2B_Tool : LeapModel {
        override val llmModel = LLModel(
            provider = AndroidLocalLLMProvider,
            id = "lfm2-1.2b-tool",
            capabilities = listOf(
                LLMCapability.Tools,
                LLMCapability.Completion,
            ),
            contextLength = 32_768,
        )

        val MODEL_ID = "lfm2-1.2b-tool"
    }
}

fun getLeapLLModelById(modelId: String): LeapModel? {
    return when (modelId) {
        LeapModel.LFM2_1_2B_Tool.MODEL_ID -> LeapModel.LFM2_1_2B_Tool
        else -> null
    }
}