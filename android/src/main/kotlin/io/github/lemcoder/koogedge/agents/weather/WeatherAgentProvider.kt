package io.github.lemcoder.koogedge.agents.weather

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.asAssistantMessage
import ai.koog.agents.core.dsl.extension.executeTool
import ai.koog.agents.core.dsl.extension.requestLLM
import ai.koog.agents.core.dsl.extension.sendToolResult
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.message.Message
import io.github.lemcoder.koog.edge.cactus.CactusModels
import io.github.lemcoder.koog.edge.cactus.getCactusLLMClient
import io.github.lemcoder.koog.edge.leap.getLeapLLMClient
import io.github.lemcoder.koogedge.App
import io.github.lemcoder.koogedge.agents.common.AgentProvider
import io.github.lemcoder.koogedge.agents.common.modelsPath

/** Factory for creating weather forecast agents */
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
        val toolRegistry = ToolRegistry { tool(WeatherTools.WeatherForecastTool) }

        @Suppress("DuplicatedCode")
        val strategy =
            functionalStrategy<String, String>(title) { input ->
                var response = requestLLM(input)

                while (response is Message.Tool.Call) {
                    val tool = response

                    val result = executeTool(tool)
                    response = sendToolResult(result)
                }

                val assistantContent = response.asAssistantMessage().content
                onAssistantMessage(assistantContent)
            }

        // Create agent config with proper prompt
        val agentConfig =
            AIAgentConfig(
                prompt =
                    prompt("test") {
                        system(
                            """
                            You are a helpful weather assistant. Use the tools available to provide accurate weather forecasts.
                            """
                                .trimIndent()
                        )
                    },
                model = CactusModels.Chat.Qwen3_0_6B,
                maxAgentIterations = 50,
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
