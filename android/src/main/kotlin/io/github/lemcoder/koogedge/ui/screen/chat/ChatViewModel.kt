package io.github.lemcoder.koogedge.ui.screen.chat

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.lemcoder.koogedge.agents.chat.ChatAgentProvider
import io.github.lemcoder.koogedge.ui.common.MviViewModel
import io.github.lemcoder.koogedge.ui.util.SnackbarUtil
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : MviViewModel<ChatState, ChatEvent>() {
    private val _state = MutableStateFlow(ChatState())
    override val state: StateFlow<ChatState> = _state.asStateFlow()

    override fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage -> {
                viewModelScope.launch { sendMessageToAssistant(event.message) }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun sendMessageToAssistant(message: String) {
        val history = state.value.messages
        val userMessage =
            ChatState.Message(id = Uuid.random().toString(), content = message, isUser = true)

        _state.update { state -> state.copy(isLoading = true, messages = history + userMessage) }

        val agent =
            ChatAgentProvider()
                .provideAgent(
                    onToolCallEvent = {
                        // Should not happen
                    },
                    onErrorEvent = { SnackbarUtil.showSnackbar("Error occurred: $it") },
                    onAssistantMessage = { assistantMessage ->
                        Log.d("ChatViewModel", "Assistant message: $assistantMessage")
                        assistantMessage
                    },
                )

        val noThinkToken = "/no_think"
        val result = agent.run(noThinkToken + message)
        val newMessage =
            ChatState.Message(id = Uuid.random().toString(), content = result, isUser = false)
        val newHistory = state.value.messages
        _state.update { state -> state.copy(isLoading = false, messages = newHistory + newMessage) }
    }
}
