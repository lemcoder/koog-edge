package io.github.lemcoder.koogedge.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lemcoder.koogedge.ui.navigation.Destination
import io.github.lemcoder.koogedge.ui.navigation.NavigationService
import io.github.lemcoder.koogedge.ui.screen.calculatorTool.CalculatorToolRoute
import io.github.lemcoder.koogedge.ui.screen.chat.ChatRoute
import io.github.lemcoder.koogedge.ui.screen.toolsList.ToolsListRoute
import io.github.lemcoder.koogedge.ui.screen.weatherTool.WeatherToolRoute
import io.github.lemcoder.koogedge.ui.util.SnackbarUtil

@Composable
fun MainScreen() {
    val navigationService = remember { NavigationService.Instance }
    val destination by navigationService.destinationFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarHostState) {
        SnackbarUtil.snackbarHostState = snackbarHostState
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = destination,
            modifier = Modifier.padding(innerPadding)
        ) { destination ->
            when (destination) {
                Destination.CalculatorTool -> CalculatorToolRoute()
                Destination.ToolsList -> ToolsListRoute()
                Destination.WeatherTool -> WeatherToolRoute()
                Destination.Chat -> ChatRoute()
            }
        }
    }

    BackHandler {
        navigationService.navigateBack()
    }
}