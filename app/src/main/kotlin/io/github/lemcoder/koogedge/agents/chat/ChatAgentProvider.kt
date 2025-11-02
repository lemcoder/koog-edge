package io.github.lemcoder.koogedge.agents.chat

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.entity.AIAgentGraphStrategy
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.agent.requestLLM
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.ext.agent.chatAgentStrategy
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import io.github.lemcoder.koog.edge.cactus.CactusLLMParams
import io.github.lemcoder.koog.edge.cactus.CactusModels
import io.github.lemcoder.koog.edge.cactus.getCactusLLMClient
import io.github.lemcoder.koog.edge.leap.getLeapLLMClient
import io.github.lemcoder.koogedge.App
import io.github.lemcoder.koogedge.agents.common.AgentProvider
import io.github.lemcoder.koogedge.agents.common.modelsPath

internal class ChatAgentProvider : AgentProvider {
    override val title: String = "Chat"
    override val description: String = "Hi, I'm a helpful assistant"

    override suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (String) -> String,
    ): AIAgent<String, String> {
        val leapExecutor = SingleLLMPromptExecutor(getLeapLLMClient(modelsPath))
        val cactusExecutor = SingleLLMPromptExecutor(getCactusLLMClient(App.context))

        @Suppress("DuplicatedCode")
        val strategy = plainChatAgentStrategy()

        // Create agent config with proper prompt
        val agentConfig = AIAgentConfig(
            prompt = prompt(
                "test",
                params = CactusLLMParams()
            ) {
                system("/nothink")
                system("You are a helpful assistant.")
            },
            model = CactusModels.Chat.Qwen3_0_6B,
            maxAgentIterations = 10,
        )

        return AIAgent(
            promptExecutor = cactusExecutor,
            strategy = strategy,
            agentConfig = agentConfig,
        )
    }
}

private fun plainChatAgentStrategy() = functionalStrategy<String, String>("Chat Agent") { input ->
    val response = requestLLM(input, allowToolCalls = false)
    return@functionalStrategy response.content
}