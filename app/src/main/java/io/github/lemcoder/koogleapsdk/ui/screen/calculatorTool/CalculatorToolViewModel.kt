package io.github.lemcoder.koogleapsdk.ui.screen.calculatorTool

import androidx.lifecycle.viewModelScope
import io.github.lemcoder.koogleapsdk.ui.common.MviViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorToolViewModel : MviViewModel<CalculatorToolState, CalculatorToolEvent>() {
    private val _state = MutableStateFlow(CalculatorToolState())
    override val state: StateFlow<CalculatorToolState> = _state.asStateFlow()

    override fun onEvent(event: CalculatorToolEvent) {
        when (event) {
            is CalculatorToolEvent.Calculate -> {
                calculateUsingAgent(event.expression)
            }
        }
    }

    internal fun calculateUsingAgent(expression: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCalculating = true
                )
            }
            val prompt = "What is the result of: $expression"
            try {
                val result = CalculatorAgentRunner.runAgent(prompt)
                _state.update {
                    it.copy(
                        isCalculating = false,
                        answer = result
                    )
                }
            } catch (ex: Exception) {
                // TODO handle errors
            }
        }
    }
}