package io.github.lemcoder.koog.edge.leap.internal.converter

import ai.koog.prompt.params.LLMParams
import ai.liquid.leap.GenerationOptions
import io.github.lemcoder.koog.edge.leap.LeapLLMParams
import io.github.lemcoder.koog.edge.util.Converter

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