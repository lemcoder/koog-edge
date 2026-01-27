package io.github.lemcoder.koog.edge.leap

import ai.koog.prompt.params.LLMParams

class LeapLLMParams(
    temperature: Float? = null,
    val topP: Float? = null,
    val minP: Float? = null,
    val repetitionPenalty: Float? = null,
    val jsonSchemaConstraint: String? = null,
) : LLMParams(temperature = temperature?.toDouble())
