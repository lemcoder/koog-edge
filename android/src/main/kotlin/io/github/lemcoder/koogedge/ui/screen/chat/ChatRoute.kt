package io.github.lemcoder.koogedge.ui.screen.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatRoute() {
    val viewModel = viewModel { ChatViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}