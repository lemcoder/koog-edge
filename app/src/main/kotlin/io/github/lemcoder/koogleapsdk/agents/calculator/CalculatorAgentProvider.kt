package io.github.lemcoder.koogleapsdk.agents.calculator

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.asAssistantMessage
import ai.koog.agents.core.agent.compressHistory
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.containsToolCalls
import ai.koog.agents.core.agent.executeMultipleTools
import ai.koog.agents.core.agent.extractToolCalls
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.agent.latestTokenUsage
import ai.koog.agents.core.agent.requestLLMMultiple
import ai.koog.agents.core.agent.sendMultipleToolResults
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import io.github.lemcoder.koog.edge.cactus.CactusModels
import io.github.lemcoder.koog.edge.cactus.getCactusLLMClient
import io.github.lemcoder.koog.edge.leap.LeapLLMParams
import io.github.lemcoder.koog.edge.leap.LeapModels
import io.github.lemcoder.koog.edge.leap.getLeapLLMClient
import io.github.lemcoder.koogleapsdk.App
import io.github.lemcoder.koogleapsdk.agents.common.AgentProvider
import io.github.lemcoder.koogleapsdk.agents.common.ExitTool
import io.github.lemcoder.koogleapsdk.agents.common.modelsPath

/**
 * Factory for creating calculator agents (graphless strategy)
 */
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
            prompt = prompt(
                "test",
                params = LeapLLMParams(
                    temperature = .5f
                )
            ) {
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