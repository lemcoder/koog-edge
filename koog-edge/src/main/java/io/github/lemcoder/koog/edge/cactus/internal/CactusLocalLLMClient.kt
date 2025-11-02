package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import com.cactus.CactusCompletionParams
import io.github.lemcoder.koog.edge.cactus.CactusLLMParams
import io.github.lemcoder.koog.edge.cactus.getCactusLLMModelById
import io.github.lemcoder.koog.edge.cactus.internal.converter.cactusToKoogToolCallResponseConverter
import io.github.lemcoder.koog.edge.cactus.internal.converter.koogToCactusMessageConverter
import io.github.lemcoder.koog.edge.cactus.internal.converter.koogToCactusToolConverter
import io.github.lemcoder.koog.edge.log.AndroidLogger
import kotlinx.datetime.Clock

class CactusLocalLLMClient(
    private val modelLoader: CactusModelLoader
) : LLMClient {
    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<Message.Response> {
        AndroidLogger.w { "Executing prompt: $prompt with tools: $tools and model: $model" }
        require(model.capabilities.contains(LLMCapability.Completion)) {
            "Model ${model.id} does not support chat completions"
        }
        require(model.capabilities.contains(LLMCapability.Tools) || tools.isEmpty()) {
            "Model ${model.id} does not support tools"
        }

        val cactusModel = getCactusLLMModelById(model.id)
            ?: error("Model ${model.id} is not supported by Cactus compute")

        val modelRunner = modelLoader.loadModel(cactusModel)
            ?: error("Failed to load model ${model.id}")

        val history = prompt.messages.map(koogToCactusMessageConverter::convert)
        val params = prompt.params as? CactusLLMParams

        val cactusTools = tools.map(koogToCactusToolConverter::convert).also {
            it.forEach { tool ->
                AndroidLogger.w("Registering: $tool")
            }
        }

        val cactusResult = modelRunner.generateCompletion(
            messages = history,
            params = CactusCompletionParams(
                model = model.id,
                temperature = params?.temperature,
                topK = params?.topK,
                topP = params?.topP,
                maxTokens = params?.maxTokens ?: 1024, // TODO check default max tokens by model
                stopSequences = params?.stopSequences ?: emptyList(),
                cactusToken = params?.cactusToken,
                tools = cactusTools
            ),
            onToken = { token, tokenId ->
                // Used in streaming only
            }
        ) ?: error("Model ${model.id} returned no response")

        val toolCalls = cactusResult.toolCalls?.map(cactusToKoogToolCallResponseConverter::convert)
            ?: emptyList()
        val responseText = cactusResult.response ?: ""

        if (responseText.isEmpty()) {
            if (toolCalls.isNotEmpty()) {
                AndroidLogger.w("Model returned only tool calls, no assistant response.")
                return toolCalls
            }
            AndroidLogger.error("Model returned empty response. Frames: $toolCalls")
            throw IllegalStateException("Model returned empty response. Check input prompt and model configuration.")
        }

        val result = Message.Assistant(
            content = responseText,
            metaInfo = ResponseMetaInfo(timestamp = Clock.System.now()),
            attachments = listOf(),
        )

        return listOf(result) + toolCalls
    }

    override suspend fun moderate(
        prompt: Prompt,
        model: LLModel
    ): ModerationResult {
        TODO("Not yet implemented")
    }
}