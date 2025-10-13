package io.github.lemcoder.koog.leap

import ai.koog.prompt.executor.clients.LLMClient
import io.github.lemcoder.koog.leap.internal.LeapLocalLLMClient
import io.github.lemcoder.koog.leap.internal.LeapModelLoader

fun getLeapLLMClient(
    modelPath: String,
): LLMClient = LeapLocalLLMClient(LeapModelLoader(modelPath))
