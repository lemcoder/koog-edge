package io.github.lemcoder.koogedge.agents.weather

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.extension.asAssistantMessage
import ai.koog.agents.core.dsl.extension.compressHistory
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.extension.containsToolCalls
import ai.koog.agents.core.dsl.extension.executeMultipleTools
import ai.koog.agents.core.dsl.extension.extractToolCalls
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.latestTokenUsage
import ai.koog.agents.core.dsl.extension.requestLLMMultiple
import ai.koog.agents.core.dsl.extension.sendMultipleToolResults
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import io.github.lemcoder.koog.edge.cactus.CactusModels
import io.github.lemcoder.koog.edge.cactus.getCactusLLMClient
import io.github.lemcoder.koog.edge.leap.getLeapLLMClient
import io.github.lemcoder.koogedge.App
import io.github.lemcoder.koogedge.agents.common.AgentProvider
import io.github.lemcoder.koogedge.agents.common.ExitTool
import io.github.lemcoder.koogedge.agents.common.modelsPath
import kotlinx.datetime.Clock

/**
 * Factory for creating weather forecast agents
 */
internal class WeatherAgentProvider : AgentProvider {
    override val title: String = "Weather Forecast"
    override val description: String =
        "Hi, I'm a weather agent. I can provide weather forecasts for any location."

    override suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (String) -> String,
    ): AIAgent<String, String> {
        val leapExecutor = SingleLLMPromptExecutor(getLeapLLMClient(modelsPath))
        val cactusExecutor = SingleLLMPromptExecutor(getCactusLLMClient(App.context))

        // Create tool registry with weather tools
        val toolRegistry = ToolRegistry {
            tool(WeatherTools.CurrentDatetimeTool)
            tool(WeatherTools.AddDatetimeTool)
            tool(WeatherTools.WeatherForecastTool)
            tool(ExitTool)
        }

        @Suppress("DuplicatedCode")
        val strategy = functionalStrategy<String, String>(title) { input ->
            var responses = requestLLMMultiple(input)

            while (responses.containsToolCalls()) {
                val tools = extractToolCalls(responses)

                tools.forEach { toolCall ->
                    onToolCallEvent("Tool ${toolCall.tool}")
                }

                if (latestTokenUsage() > 100500) {
                    compressHistory()
                }

                val results = executeMultipleTools(tools)
                responses = sendMultipleToolResults(results)
            }

            val assistantContent = responses.single().asAssistantMessage().content
            onAssistantMessage(assistantContent)
        }

        // Create agent config with proper prompt
        val agentConfig = AIAgentConfig(
            prompt = prompt("test") {
                system(
                    """
                    You are a helpful weather assistant.
                    You can provide weather forecasts for any location in the world and help the user plan their activities.
                    ALWAYS use the available tools to get weather data. NEVER say you do not have access to weather data.
                    ALWAYS use date and time tools to handle dates and times.
                    Today's date and time is ${Clock.System.now()}.
                    When you receive a tool result, always explain it to the user in natural language.
                    Use the tools at your disposal to:
                    1. Get the current date and time
                    2. Add days, hours, or minutes to a date
                    3. Get weather forecasts for specific locations and dates
                    Do not say you lack access to data; always use the tools.
                    """.trimIndent()
                )
            },
            model = CactusModels.Chat.Qwen3_0_6B,
            maxAgentIterations = 50
        )

        // Return the agent
        return AIAgent(
            promptExecutor = cactusExecutor,
            strategy = strategy,
            agentConfig = agentConfig,
            toolRegistry = toolRegistry,
        )
    }
}