package io.github.lemcoder.koogedge.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.lemcoder.koogedge.R

@Composable
fun ChatScreen(
    state: ChatState,
    onEvent: (ChatEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.chat),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            items(state.messages, key = { it.id }) {
                val color =
                    if (it.isUser) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.surfaceVariant
                Text(
                    text = it.content,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            if (state.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            var message by remember { mutableStateOf("") }
            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                placeholder = {
                    Text(stringResource(R.string.enter_message))
                },
                modifier = Modifier.weight(1f)
            )

            val keyboardController = LocalSoftwareKeyboardController.current
            Button(
                onClick = {
                    onEvent(ChatEvent.SendMessage(message))
                    message = ""
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .padding(4.dp),
                enabled = !state.isLoading && message.isNotBlank(),
            ) {
                Text(text = "Send")
            }
        }
    }
}