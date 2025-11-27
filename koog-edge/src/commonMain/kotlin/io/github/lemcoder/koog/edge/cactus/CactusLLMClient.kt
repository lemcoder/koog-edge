package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.executor.clients.LLMClient
import android.content.Context
import io.github.lemcoder.koog.edge.cactus.internal.CactusLocalLLMClient
import io.github.lemcoder.koog.edge.cactus.internal.CactusModelLoader

fun getCactusLLMClient(context: Context): LLMClient {
    CactusModelLoader.initializeIfNecessary(context)
    return CactusLocalLLMClient(CactusModelLoader)
}
