package io.github.lemcoder.koogedge.ui.screen.chat

data class ChatState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList()
) {
    data class Message(
        val id: String,
        val content: String,
        val isUser: Boolean
    )
}