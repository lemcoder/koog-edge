package io.github.lemcoder.koogedge.ui.screen.calculatorTool

sealed class CalculatorToolEvent {
    data class Calculate(val expression: String) : CalculatorToolEvent()
}