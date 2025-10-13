package io.github.lemcoder.koog.leap.internal.util

import ai.koog.prompt.message.Message
import ai.koog.prompt.streaming.StreamFrame
import ai.liquid.leap.message.ChatMessage
import ai.liquid.leap.message.ChatMessageContent
import ai.liquid.leap.message.MessageResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

internal val leapToKoogMessageConverter = Converter<Message, ChatMessage> { message ->
    when (message) {
        is Message.User -> ChatMessage(
            role = ChatMessage.Role.USER,
            content = message.content()
        )

        is Message.Assistant -> ChatMessage(
            role = ChatMessage.Role.ASSISTANT,
            content = message.content()
        )

        is Message.System -> ChatMessage(
            role = ChatMessage.Role.SYSTEM,
            content = message.content()
        )

        is Message.Tool -> ChatMessage(
            role = ChatMessage.Role.TOOL,
            content = message.content()
        )
    }
}

internal val messageResponseToStreamFrameMapper =
    Converter<MessageResponse, List<StreamFrame>> { response ->
        when (response) {
            is MessageResponse.Chunk -> listOf(
                StreamFrame.Append(
                    text = response.text
                )
            )

            is MessageResponse.Complete -> listOf(StreamFrame.End())

            is MessageResponse.FunctionCalls -> response.functionCalls.map { firstCall ->
                StreamFrame.ToolCall(
                    id = null,
                    name = firstCall.name,
                    content = firstCall.arguments.toJsonObjectString()
                )
            }

            is MessageResponse.ReasoningChunk -> emptyList() // TODO Ignore reasoning chunks for now
        }
    }

// TODO support more content types
private fun Message.content(): List<ChatMessageContent> {
    return listOf(ChatMessageContent.Text(this.content))
}

private fun Map<String, Any?>.toJsonObjectString(): String {
    fun Any?.toJsonElement(): JsonElement = when (this) {
        null -> JsonNull
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is List<*> -> JsonArray(this.map { it.toJsonElement() })
        else -> JsonPrimitive(this.toString()) // fallback for unexpected types
    }

    fun Map<String, Any?>.toJsonObject(): JsonObject = buildJsonObject {
        for ((key, value) in this@toJsonObject) {
            put(key, value.toJsonElement())
        }
    }

    return Json.encodeToString(JsonObject.serializer(), this.toJsonObject())
}