package io.github.lemcoder.koog.edge.cactus.internal.converter

import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import com.cactus.ChatMessage
import com.cactus.ToolCall
import io.github.lemcoder.koog.edge.util.Converter
import kotlinx.datetime.Clock

internal val cactusToKoogToolCallResponseConverter =
    Converter<ToolCall, Message.Response> { toolCall ->
        Message.Tool.Call(
            id = null,
            tool = toolCall.name,
            content = toolCall.arguments
                .map { "${it.key}: ${it.value}" }
                .joinToString(", "),
            metaInfo = ResponseMetaInfo(timestamp = Clock.System.now())
        )
    }

internal val koogToCactusMessageConverter = Converter<Message, ChatMessage> { message ->
    when (message) {
        is Message.User -> ChatMessage(
            role = "USER",
            content = message.content
        )

        is Message.Assistant -> ChatMessage(
            role = "ASSISTANT",
            content = message.content
        )

        is Message.System -> ChatMessage(
            role = "SYSTEM",
            content = message.content
        )

        is Message.Tool -> ChatMessage(
            role = "TOOL",
            content = message.content
        )
    }
}