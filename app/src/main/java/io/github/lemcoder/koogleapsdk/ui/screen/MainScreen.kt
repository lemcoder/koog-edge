package io.github.lemcoder.koogleapsdk.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lemcoder.koogleapsdk.ui.navigation.Destination
import io.github.lemcoder.koogleapsdk.ui.navigation.NavigationService
import io.github.lemcoder.koogleapsdk.ui.screen.calculatorTool.CalculatorToolRoute
import io.github.lemcoder.koogleapsdk.ui.screen.toolsList.ToolsListRoute

@Composable
fun MainScreen() {
    val navigationService = remember { NavigationService.Instance }
    val destination by navigationService.destinationFlow.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AnimatedContent(
            targetState = destination,
            modifier = Modifier.padding(innerPadding)
        ) { destination ->
            when (destination) {
                Destination.CalculatorTool -> CalculatorToolRoute()
                Destination.ToolsList -> ToolsListRoute()
            }
        }
    }

    BackHandler {
        navigationService.navigateBack()
    }
}