package io.github.lemcoder.koogleapsdk.ui.screen.toolsList.util

import io.github.lemcoder.koogleapsdk.ui.screen.toolsList.ToolsListState
import io.github.lemcoder.koogleapsdk.ui.screen.toolsList.ToolsListState.ToolItem.Companion.TOOL_ID_CALCULATOR

val allTools by lazy {
    listOf(
        ToolsListState.ToolItem(
            id = TOOL_ID_CALCULATOR,
            name = "Calculator",
            description = "A simple calculator tool",
        )
    )
}
