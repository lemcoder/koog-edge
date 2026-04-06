package io.github.lemcoder.koogedge.agents.calculator

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.environment.result
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.message.Message
import android.util.Log
import io.github.lemcoder.koog.edge.cactus.CactusLLMParams
import io.github.lemcoder.koog.edge.cactus.CactusModels
import io.github.lemcoder.koog.edge.cactus.getCactusLLMClient
import io.github.lemcoder.koog.edge.leap.getLeapLLMClient
import io.github.lemcoder.koogedge.App
import io.github.lemcoder.koogedge.agents.common.AgentProvider
import io.github.lemcoder.koogedge.agents.common.modelsPath

/** Factory for creating calculator agents (graphless strategy) */
internal class CalculatorAgentProvider : AgentProvider {
    override val title: String = "Calculator"
    override val description: String = "Hi, I'm a calculator agent, I can do math"

    override suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (String) -> String,
    ): AIAgent<String, String> {
        val leapExecutor = SingleLLMPromptExecutor(getLeapLLMClient(modelsPath))
        val cactusExecutor = SingleLLMPromptExecutor(getCactusLLMClient(App.context))

        val toolRegistry = ToolRegistry {
            tool(CalculatorTools.DivideTool)
            tool(CalculatorTools.PlusTool)
            tool(CalculatorTools.MinusTool)
            tool(CalculatorTools.MultiplyTool)
        }

        @Suppress("DuplicatedCode")
        val strategy =
            functionalStrategy<String, String>(title) { input ->
                llm.writeSession {
                    appendPrompt {
                        user {
                            +"/no_think"
                            +"pick the best tool to answer the question: $input and call it immediately."
                        }
                    }
                }

                var response = requestLLM(input)
                while (response is Message.Tool.Call) {
                    onToolCallEvent("Tool ${response.tool}")
                    val result = executeTool(response)
                    Log.w("CalculatorAgent", "Tool result: ${result.result}")
                    llm.writeSession {
                        appendPrompt { tool { result(result) } }

                        appendPrompt {
                            user { +"Based on the tool result, please provide only result number." }
                        }
                        response = requestLLM()
                    }
                }

                val assistantContent = response.asAssistantMessage().content
                onAssistantMessage(assistantContent)
            }

        // Create agent config with proper prompt
        val agentConfig =
            AIAgentConfig(
                prompt =
                    prompt("test", params = CactusLLMParams(maxTokens = 512)) {
                        system(calculatorSystemPrompt)
                    },
                model = CactusModels.Chat.Qwen3_0_6B,
                maxAgentIterations = 10,
            )

        return AIAgent(
            promptExecutor = cactusExecutor,
            strategy = strategy,
            agentConfig = agentConfig,
            toolRegistry = toolRegistry,
        )
    }
}
