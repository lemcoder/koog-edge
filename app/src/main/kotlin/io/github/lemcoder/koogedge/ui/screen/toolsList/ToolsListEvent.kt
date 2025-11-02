package io.github.lemcoder.koogedge.ui.screen.toolsList

sealed class ToolsListEvent {
    data class OnToolClick(val toolId: String) : ToolsListEvent()
}