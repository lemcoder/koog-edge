package io.github.lemcoder.koog.edge.cactus.internal

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import com.cactus.CactusCompletionParams
import com.cactus.CactusCompletionResult
import com.cactus.CactusLM
import com.cactus.ChatMessage
import com.cactus.InferenceMode
import com.cactus.models.CactusTool
import io.github.lemcoder.koog.edge.LocalModelLoader
import io.github.lemcoder.koog.edge.cactus.CactusLLMParams
import io.github.lemcoder.koog.edge.cactus.getCactusLLMModelById
import io.github.lemcoder.koog.edge.cactus.internal.converter.cactusToKoogToolCallResponseConverter
import io.github.lemcoder.koog.edge.cactus.internal.converter.koogToCactusMessageConverter
import io.github.lemcoder.koog.edge.cactus.internal.converter.koogToCactusToolConverter
import io.github.lemcoder.koog.edge.log.KoogEdgeLog
import io.github.lemcoder.koog.edge.provider.LocalLLMProvider
import kotlinx.datetime.Clock

class CactusLocalLLMClient(private val modelLoader: LocalModelLoader<CactusLM?>) : LLMClient {
    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): List<Message.Response> {
        KoogEdgeLog.w { "Executing prompt: $prompt with tools: $tools and model: $model" }
        require(model.capabilities.contains(LLMCapability.Completion)) {
            "Model ${model.id} does not support chat completions"
        }
        require(model.capabilities.contains(LLMCapability.Tools) || tools.isEmpty()) {
            "Model ${model.id} does not support tools"
        }

        val cactusModel =
            getCactusLLMModelById(model.id)
                ?: error("Model ${model.id} is not supported by Cactus compute")

        val modelRunner =
            modelLoader.loadModel(cactusModel) ?: error("Failed to load model ${model.id}")

        val history = prompt.messages.map(koogToCactusMessageConverter::convert)
        val params = prompt.params as? CactusLLMParams

        val cactusTools =
            tools.map(koogToCactusToolConverter::convert).also {
                it.forEach { tool -> KoogEdgeLog.w { "Registering: $tool" } }
            }

        var cactusResult = runCactusInference(model, modelRunner, history, params, cactusTools)
        while (cactusResult == null || !cactusResult.success) {
            KoogEdgeLog.w { "Cactus inference failed, retrying" }
            cactusResult = runCactusInference(model, modelRunner, history, params, cactusTools)
        }

        val toolCalls =
            cactusResult.toolCalls?.map(cactusToKoogToolCallResponseConverter::convert)
                ?: emptyList()
        val responseText = cactusResult.response ?: ""

        if (responseText.isEmpty()) {
            if (toolCalls.isNotEmpty()) {
                KoogEdgeLog.w { "Model returned only tool calls, no assistant response." }
                KoogEdgeLog.w { "tools called at ${Clock.System.now()}: $toolCalls" }
                return toolCalls
            }
            KoogEdgeLog.error("Model returned empty response. Frames: $toolCalls")
            throw IllegalStateException(
                "Model returned empty response. Check input prompt and model configuration."
            )
        }

        val result = Message.Assistant(content = responseText, metaInfo = ResponseMetaInfo.Empty)

        return listOf(result) + toolCalls
    }

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult {
        TODO("Not yet implemented")
    }

    override fun llmProvider(): LLMProvider = LocalLLMProvider

    private suspend fun runCactusInference(
        model: LLModel,
        modelRunner: CactusLM,
        history: List<ChatMessage>,
        params: CactusLLMParams?,
        cactusTools: List<CactusTool>,
    ): CactusCompletionResult? {
        val defaultParams = CactusCompletionParams()
        return modelRunner.generateCompletion(
            messages = history,
            params =
                CactusCompletionParams(
                    model = model.id,
                    temperature = params?.temperature ?: defaultParams.temperature,
                    topK = params?.topK ?: defaultParams.topK,
                    topP = params?.topP ?: defaultParams.topP,
                    maxTokens = params?.maxTokens ?: defaultParams.maxTokens,
                    stopSequences = params?.stopSequences ?: defaultParams.stopSequences,
                    cactusToken = params?.cactusToken ?: defaultParams.cactusToken,
                    tools = cactusTools,
                    mode = params?.inferenceMode ?: InferenceMode.LOCAL_FIRST,
                ),
            onToken = { token, tokenId ->
                // Used in streaming only
            },
        )
    }

    override fun close() {
        KoogEdgeLog.w { "CactusLocalLLMClient closed." }
    }
}
