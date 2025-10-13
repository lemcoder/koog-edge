package io.github.lemcoder.koogleapsdk.agents.calculator

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import io.github.lemcoder.koog.leap.LeapLLMParams
import io.github.lemcoder.koog.leap.LeapModel
import io.github.lemcoder.koog.leap.internal.getLeapLLMClient
import io.github.lemcoder.koogleapsdk.agents.common.modelsPath

object CalculatorAgentRunner {
    val toolRegistry = ToolRegistry {
        tools(CalculatorTools())
    }
    private val leapExecutor = SingleLLMPromptExecutor(getLeapLLMClient(modelsPath))
    val agentConfig = AIAgentConfig(
        prompt = prompt(
            "calculator-agent-prompt",
            params = LeapLLMParams()
        ) {
            system(
                """
                You are a helpful calculator assistant.
                Your goal is to help the user perform basic arithmetic operations using the provided tools.
                Use the provided tools to perform calculations as needed.
                """.trimIndent()
            )
        },
        model = LeapModel.LFM2_1_2B_Tool.llmModel,
        maxAgentIterations = 200
    )

    // Create the runner
    val agent
        get() = AIAgent(
            promptExecutor = leapExecutor,
            strategy = CalculatorStrategy.strategy,
            agentConfig = agentConfig,
            toolRegistry = toolRegistry,
        ) {
            handleEvents {
                onToolCallStarting { eventContext ->
                    // Log.w("Tool called: tool ${eventContext.tool.name}, args ${eventContext.toolArgs}")
                }

                onAgentExecutionFailed { eventContext ->
//                    Log.e(
//                        "An error occurred: ${eventContext.throwable.message}\n${eventContext.throwable.stackTraceToString()}"
//                    )
                }

                onAgentCompleted { eventContext ->
                    // Log.e("Result: ${eventContext.result}")
                }
            }
        }

    suspend fun runAgent(
        input: String,
    ): String {
        return agent.run(input)
    }
}