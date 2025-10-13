package io.github.lemcoder.koogleapsdk.ui.screen.weatherTool

import io.github.lemcoder.koogleapsdk.ui.common.MviViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherToolViewModel : MviViewModel<WeatherToolState, WeatherToolEvent>() {
    private val _state = MutableStateFlow(WeatherToolState())
    override val state: StateFlow<WeatherToolState> = _state.asStateFlow()

    override fun onEvent(event: WeatherToolEvent) {
        TODO("Not yet implemented")
    }
}