package io.github.lemcoder.koogedge.ui.screen.chat

sealed class ChatEvent {
    data class SendMessage(val message: String) : ChatEvent()
}
