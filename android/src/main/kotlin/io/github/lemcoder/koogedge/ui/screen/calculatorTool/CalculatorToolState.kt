package io.github.lemcoder.koogedge.ui.screen.calculatorTool

data class CalculatorToolState(
    val toolCalls: List<String> = emptyList(),
    val answer: String? = null,
    val isCalculating: Boolean = false,
)
