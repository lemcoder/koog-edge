package io.github.lemcoder.koog.leap.internal

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import ai.koog.prompt.streaming.StreamFrame
import io.github.lemcoder.koog.leap.getLeapLLModelById
import io.github.lemcoder.koog.leap.internal.util.koogToLeapParametersConverter
import io.github.lemcoder.koog.leap.internal.util.leapFunctionConverter
import io.github.lemcoder.koog.leap.internal.util.leapToKoogMessageConverter
import io.github.lemcoder.koog.leap.internal.util.messageResponseToStreamFrameMapper
import io.github.lemcoder.koog.log.AndroidLogger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

internal open class LeapLocalLLMClient(
    private val modelLoader: LeapModelLoader
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

        val leapLLModel = getLeapLLModelById(model.id)
            ?: error("Model ${model.id} is not a valid Leap model")

        val modelRunner = modelLoader.loadModel(leapLLModel)
            ?: error("Failed to load model ${model.id}")

        val conversation = modelRunner.createConversation()

        // Add message history
        for (i in 0 until prompt.messages.size - 1) {
            val message = leapToKoogMessageConverter.convert(prompt.messages[i])
            conversation.appendToHistory(message)
        }

        tools.map(leapFunctionConverter::convert).forEach { function ->
            AndroidLogger.w("Registering: $function")
            conversation.registerFunction(function)
        }

        val latestMessage = leapToKoogMessageConverter.convert(prompt.messages.last())

        val responseBuffer = mutableListOf<Message.Response>()

        coroutineScope {
            conversation.generateResponse(
                latestMessage,
                koogToLeapParametersConverter.convert(prompt.params)
            ).collect { messageResponse ->
                val frames = messageResponseToStreamFrameMapper.convert(messageResponse)
                frames.forEach { frame ->
                    AndroidLogger.w { frame.toMessageResponse().content }

                    runCatching {
                        responseBuffer.add(frame.toMessageResponse())
                    }
                }
            }
        }

        return responseBuffer
    }


    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): Flow<StreamFrame> = flow {
        AndroidLogger.w { "Executing prompt: $prompt with tools: $tools and model: $model" }
        require(model.capabilities.contains(LLMCapability.Completion)) {
            "Model ${model.id} does not support chat completions"
        }
        require(model.capabilities.contains(LLMCapability.Tools) || tools.isEmpty()) {
            "Model ${model.id} does not support tools"
        }
        val leapLLModel = getLeapLLModelById(model.id)
        require(leapLLModel != null) {
            "Model ${model.id} is not a valid Leap model"
        }
        val modelRunner = modelLoader.loadModel(leapLLModel) ?: run {
            throw IllegalStateException("Failed to load model ${model.id}")
        }
        val conversation = modelRunner.createConversation()

        val latestMessage =
            leapToKoogMessageConverter.convert(prompt.messages[prompt.messages.lastIndex])
        for (i in 0 until prompt.messages.size - 1) {
            val message = leapToKoogMessageConverter.convert(prompt.messages[i])
            conversation.appendToHistory(message)
        }

        tools.map(leapFunctionConverter::convert).forEach { function ->
            AndroidLogger.w("Registering: $function")
            conversation.registerFunction(function)
        }

        // TODO Support params
        conversation.generateResponse(
            latestMessage,
            koogToLeapParametersConverter.convert(prompt.params)
        ).collect { messageResponse ->
            val frames = messageResponseToStreamFrameMapper.convert(messageResponse)
            frames.forEach { frame -> emit(frame) }
        }
    }

    override suspend fun moderate(
        prompt: Prompt,
        model: LLModel
    ): ModerationResult {
        TODO()
    }
}

private fun StreamFrame.toMessageResponse(): Message.Response {
    val metaInfo = ResponseMetaInfo(timestamp = Clock.System.now())
    return when (this) {
        is StreamFrame.Append -> Message.Assistant(
            content = this.text,
            metaInfo = metaInfo,
            attachments = listOf(),
            finishReason = "",
        )

        is StreamFrame.End -> Message.Assistant(
            content = "",
            metaInfo = metaInfo,
            attachments = listOf(),
            finishReason = this.finishReason,
        )

        is StreamFrame.ToolCall -> Message.Tool.Call(
            id = this.id,
            tool = this.name,
            content = this.content,
            metaInfo = metaInfo
        )
    }
}