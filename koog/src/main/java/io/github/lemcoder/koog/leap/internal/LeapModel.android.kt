package io.github.lemcoder.koog.leap.internal

import ai.koog.prompt.executor.clients.LLMClient

fun getLeapLLMClient(
    modelPath: String,
): LLMClient = LeapLocalLLMClient(LeapModelLoader(modelPath))
