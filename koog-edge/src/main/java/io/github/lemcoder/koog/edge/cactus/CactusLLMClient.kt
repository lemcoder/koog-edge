package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.executor.clients.LLMClient
import io.github.lemcoder.koog.edge.cactus.internal.CactusLocalLLMClient
import io.github.lemcoder.koog.edge.cactus.internal.CactusModelLoader

fun getCactusLLMClient(): LLMClient = CactusLocalLLMClient(CactusModelLoader())
