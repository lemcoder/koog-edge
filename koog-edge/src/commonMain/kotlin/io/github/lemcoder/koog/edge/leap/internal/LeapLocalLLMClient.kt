package io.github.lemcoder.koog.edge.leap.internal

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.ContentPart
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import ai.koog.prompt.streaming.StreamFrame
import io.github.lemcoder.koog.edge.leap.getLeapLLModelById
import io.github.lemcoder.koog.edge.leap.internal.converter.koogToLeapMessageConverter
import io.github.lemcoder.koog.edge.leap.internal.converter.koogToLeapParametersConverter
import io.github.lemcoder.koog.edge.leap.internal.converter.leapFunctionConverter
import io.github.lemcoder.koog.edge.leap.internal.converter.messageResponseToStreamFrameConverter
import io.github.lemcoder.koog.edge.log.KoogEdgeLog
import io.github.lemcoder.koog.edge.provider.LocalLLMProvider
import kotlin.time.Clock
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

internal class LeapLocalLLMClient(private val modelLoader: LeapModelLoader) : LLMClient() {
    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): List<Message.Response> {
        KoogEdgeLog.w { "Executing prompt: $prompt with tools: $tools and model: $model" }
        require(model.capabilities?.contains(LLMCapability.Completion) == true) {
            "Model ${model.id} does not support chat completions"
        }
        require(model.capabilities?.contains(LLMCapability.Tools) == true || tools.isEmpty()) {
            "Model ${model.id} does not support tools"
        }

        val leapLLModel =
            getLeapLLModelById(model.id) ?: error("Model ${model.id} is not a valid Leap model")

        val modelRunner =
            modelLoader.loadModel(leapLLModel) ?: error("Failed to load model ${model.id}")

        val conversation = modelRunner.createConversation()

        // Add message history
        for (i in 0 until prompt.messages.size - 1) {
            val message = koogToLeapMessageConverter.convert(prompt.messages[i])
            conversation.appendToHistory(message)
        }

        tools.map(leapFunctionConverter::convert).forEach { function ->
            KoogEdgeLog.w { "Registering: $function" }
            conversation.registerFunction(function)
        }

        val latestMessage = koogToLeapMessageConverter.convert(prompt.messages.last())

        val responseText = StringBuilder()
        val toolCalls = mutableListOf<Message.Tool.Call>()
        var finishReason: String? = null

        coroutineScope {
            conversation
                .generateResponse(
                    latestMessage,
                    koogToLeapParametersConverter.convert(prompt.params),
                )
                .catch { KoogEdgeLog.error("Error during response generation", it) }
                .collect { messageResponse ->
                    val frames = messageResponseToStreamFrameConverter.convert(messageResponse)
                    frames.forEach { frame ->
                        KoogEdgeLog.warning("Received frame: $frame")
                        when (frame) {
                            is StreamFrame.End -> finishReason = frame.finishReason
                            is StreamFrame.TextComplete -> responseText.append(frame.text)
                            is StreamFrame.TextDelta -> responseText.append(frame.text)
                            is StreamFrame.ToolCallComplete -> {
                                toolCalls +=
                                    Message.Tool.Call(
                                        id = frame.id,
                                        tool = frame.name,
                                        content = frame.content,
                                        metaInfo = ResponseMetaInfo.create(Clock.System),
                                    )
                            }
                            is StreamFrame.ReasoningComplete,
                            is StreamFrame.ReasoningDelta,
                            is StreamFrame.ToolCallDelta -> {
                                // Ignored for now in non-streaming execute() aggregation.
                            }
                        }
                    }
                }
        }

        if (responseText.isEmpty()) {
            if (toolCalls.isNotEmpty()) {
                KoogEdgeLog.warning("Model returned only tool calls, no assistant response.")
                return toolCalls
            }
            KoogEdgeLog.error(
                "Model returned empty response. Frames: $toolCalls, finishReason: $finishReason"
            )
            throw IllegalStateException(
                "Model returned empty response. Check input prompt and model configuration."
            )
        }

        val metaInfo = ResponseMetaInfo(timestamp = Clock.System.now())
        val result =
            Message.Assistant(
                content = responseText.toString(),
                metaInfo = metaInfo,
                finishReason = finishReason.orEmpty(),
            )

        return listOf(result) + toolCalls
    }

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): Flow<StreamFrame> = flow {
        KoogEdgeLog.w { "Executing prompt: $prompt with tools: $tools and model: $model" }
        require(model.capabilities?.contains(LLMCapability.Completion) == true) {
            "Model ${model.id} does not support chat completions"
        }
        require(model.capabilities?.contains(LLMCapability.Tools) == true || tools.isEmpty()) {
            "Model ${model.id} does not support tools"
        }
        val leapLLModel = getLeapLLModelById(model.id)
        require(leapLLModel != null) { "Model ${model.id} is not a valid Leap model" }
        val modelRunner =
            modelLoader.loadModel(leapLLModel)
                ?: run { throw IllegalStateException("Failed to load model ${model.id}") }
        val conversation = modelRunner.createConversation()

        val latestMessage =
            koogToLeapMessageConverter.convert(prompt.messages[prompt.messages.lastIndex])
        for (i in 0 until prompt.messages.size - 1) {
            val message = koogToLeapMessageConverter.convert(prompt.messages[i])
            conversation.appendToHistory(message)
        }

        tools.map(leapFunctionConverter::convert).forEach { function ->
            KoogEdgeLog.warning("Registering: $function")
            conversation.registerFunction(function)
        }

        // TODO Support params
        conversation
            .generateResponse(latestMessage, koogToLeapParametersConverter.convert(prompt.params))
            .collect { messageResponse ->
                val frames = messageResponseToStreamFrameConverter.convert(messageResponse)
                frames.forEach { frame -> emit(frame) }
            }
    }

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult {
        TODO()
    }

    override fun llmProvider(): LLMProvider = LocalLLMProvider

    override fun close() {
        KoogEdgeLog.info("Closing LeapLocalLLMClient")
    }
}

private fun StreamFrame.toMessageResponse(): Message.Response {
    val metaInfo = ResponseMetaInfo(timestamp = Clock.System.now())
    return when (this) {
        is StreamFrame.ReasoningComplete ->
            Message.Reasoning(
                encrypted = encrypted,
                parts = text.map { ContentPart.Text(it) },
                summary = summary?.map { ContentPart.Text(it) },
                metaInfo = metaInfo,
            )

        is StreamFrame.TextComplete -> Message.Assistant(content = text, metaInfo = metaInfo)

        is StreamFrame.ToolCallComplete ->
            Message.Tool.Call(id = id, tool = name, content = content, metaInfo = metaInfo)

        is StreamFrame.ReasoningDelta ->
            Message.Reasoning(
                parts = listOf(ContentPart.Text(text.orEmpty())),
                summary = summary?.let { listOf(ContentPart.Text(it)) },
                metaInfo = metaInfo,
            )

        is StreamFrame.TextDelta -> Message.Assistant(content = text, metaInfo = metaInfo)

        is StreamFrame.ToolCallDelta ->
            Message.Tool.Call(
                id = id,
                tool = name.orEmpty(),
                content = content.orEmpty(),
                metaInfo = metaInfo,
            )

        is StreamFrame.End ->
            Message.Assistant(content = "", metaInfo = metaInfo, finishReason = finishReason)
    }
}
