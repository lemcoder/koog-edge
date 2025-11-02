package io.github.lemcoder.koogedge.ui.screen.weatherTool

sealed class WeatherToolEvent {
    data class OnSearchClick(val cityName: String) : WeatherToolEvent()
}