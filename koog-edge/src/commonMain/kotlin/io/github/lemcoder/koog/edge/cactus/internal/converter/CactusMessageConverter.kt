package io.github.lemcoder.koog.edge.cactus.internal.converter

import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import com.cactus.ChatMessage
import com.cactus.ToolCall
import io.github.lemcoder.koog.edge.util.Converter
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalUuidApi::class)
internal val cactusToKoogToolCallResponseConverter =
    Converter<ToolCall, Message.Response> { toolCall ->
        Message.Tool.Call(
            id = Uuid.random().toString(),
            tool = toolCall.name,
            content =
                Json.encodeToString(
                    buildJsonObject { toolCall.arguments.forEach { (k, v) -> put(k, v) } }
                ),
            metaInfo = ResponseMetaInfo.create(Clock.System),
        )
    }

internal val koogToCactusMessageConverter =
    Converter<Message, ChatMessage> { message ->
        when (message) {
            is Message.User -> ChatMessage(role = "user", content = message.content)

            is Message.Assistant -> ChatMessage(role = "assistant", content = message.content)

            is Message.System -> ChatMessage(role = "system", content = message.content)

            is Message.Tool -> ChatMessage(role = "tool", content = message.content)

            is Message.Reasoning -> ChatMessage(role = "assistant", content = message.content)
        }
    }
