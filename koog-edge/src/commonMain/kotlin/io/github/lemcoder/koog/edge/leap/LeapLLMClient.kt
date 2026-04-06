package io.github.lemcoder.koog.edge.leap

import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.LLMClientAPI
import io.github.lemcoder.koog.edge.leap.internal.LeapLocalLLMClient
import io.github.lemcoder.koog.edge.leap.internal.LeapModelLoader

fun getLeapLLMClient(modelPath: String): LLMClient =
    LeapLocalLLMClient(LeapModelLoader(modelPath))
