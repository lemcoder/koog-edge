package io.github.lemcoder.koog.leap.internal.util

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.liquid.leap.function.LeapFunction
import ai.liquid.leap.function.LeapFunctionParameter

internal val leapFunctionConverter = Converter<ToolDescriptor, LeapFunction> { tool ->
    LeapFunction(
        name = tool.name,
        description = tool.description,
        parameters = tool.requiredParameters.map { it.toLeapParameter(false) } +
                tool.optionalParameters.map { it.toLeapParameter(true) }
    )
}

private fun ToolParameterDescriptor.toLeapParameter(isOptional: Boolean): LeapFunctionParameter =
    LeapFunctionParameter(
        name = name,
        type = leapParameterTypeConverter.convert(type),
        description = description,
        optional = isOptional
    )