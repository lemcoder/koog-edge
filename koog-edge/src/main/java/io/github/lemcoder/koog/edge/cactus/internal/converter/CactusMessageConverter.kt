package io.github.lemcoder.koog.edge.cactus.internal.converter

import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import com.cactus.ChatMessage
import com.cactus.ToolCall
import io.github.lemcoder.koog.edge.util.Converter
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal val cactusToKoogToolCallResponseConverter =
    Converter<ToolCall, Message.Response> { toolCall ->
        Message.Tool.Call(
            id = null,
            tool = toolCall.name,
            content = Json.encodeToString(
                buildJsonObject {
                    toolCall.arguments.forEach { (k, v) ->
                        put(k, v)
                    }
                }
            ),
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