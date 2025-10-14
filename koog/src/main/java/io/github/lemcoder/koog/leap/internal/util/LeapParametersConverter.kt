package io.github.lemcoder.koog.leap.internal.util

import ai.koog.prompt.params.LLMParams
import ai.liquid.leap.GenerationOptions
import io.github.lemcoder.koog.leap.LeapLLMParams

internal val koogToLeapParametersConverter =
    Converter<LLMParams, GenerationOptions> { params ->
        val leapParams = (params as? LeapLLMParams) ?: return@Converter GenerationOptions()
        GenerationOptions(
            temperature = leapParams.temperature?.toFloat(),
            topP = leapParams.topP,
            minP = leapParams.minP,
            repetitionPenalty = leapParams.repetitionPenalty,
            jsonSchemaConstraint = leapParams.jsonSchemaConstraint,
        )
    }