package io.github.lemcoder.koogedge.ui.navigation

sealed interface Destination {
    data object CalculatorTool : Destination

    data object WeatherTool : Destination

    data object ToolsList : Destination

    data object Chat: Destination
}