package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.executor.clients.LLMClient
import io.github.lemcoder.koog.edge.cactus.internal.CactusLocalLLMClient
import io.github.lemcoder.koog.edge.cactus.internal.CactusModelLoader
import io.github.lemcoder.koog.edge.leap.internal.LeapLocalLLMClient
import io.github.lemcoder.koog.edge.leap.internal.LeapModelLoader

fun getLeapLLMClient(
    modelPath: String,
): LLMClient = LeapLocalLLMClient(LeapModelLoader(modelPath))

fun getCactusLLMClient(): LLMClient = CactusLocalLLMClient(CactusModelLoader())
