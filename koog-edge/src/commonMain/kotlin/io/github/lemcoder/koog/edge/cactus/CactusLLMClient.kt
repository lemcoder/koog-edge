package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.executor.clients.LLMClient
import io.github.lemcoder.koog.edge.cactus.internal.CactusLocalLLMClient
import io.github.lemcoder.koog.edge.cactus.internal.cactusModelLoader

/**
 * A context type for Koog Edge on different platforms. on Android, this could be
 * android.content.Context on iOS, this can be null
 */
typealias KoogEdgeContext = Any?

fun getCactusLLMClient(context: KoogEdgeContext): LLMClient {
    return CactusLocalLLMClient(cactusModelLoader(context))
}
