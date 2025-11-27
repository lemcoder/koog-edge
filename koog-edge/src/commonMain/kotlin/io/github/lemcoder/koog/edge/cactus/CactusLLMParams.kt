package io.github.lemcoder.koog.edge.cactus

import ai.koog.prompt.params.LLMParams
import com.cactus.InferenceMode

/**
 * public constructor CactusCompletionParams(
 *     model: String? = COMPILED_CODE,
 *     temperature: Double? = COMPILED_CODE,
 *     topK: Int? = COMPILED_CODE,
 *     topP: Double? = COMPILED_CODE,
 *     maxTokens: Int = COMPILED_CODE,
 *     stopSequences: List<String> = COMPILED_CODE,
 *     tools: List<CactusTool> = COMPILED_CODE,
 *     mode: InferenceMode = COMPILED_CODE,
 *     cactusToken: String? = COMPILED_CODE
 * )
 */
class CactusLLMParams(
    temperature: Double? = null,
    maxTokens: Int? = null,
    val topK: Int? = null,
    val topP: Double? = null,
    val stopSequences: List<String> = emptyList(),
    val cactusToken: String? = null,
    val inferenceMode: InferenceMode = InferenceMode.LOCAL
) : LLMParams(
    temperature = temperature,
    maxTokens = maxTokens,
)