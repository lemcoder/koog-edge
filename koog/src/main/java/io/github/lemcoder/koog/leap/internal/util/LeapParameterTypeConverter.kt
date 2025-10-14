package io.github.lemcoder.koog.leap.internal.util

import ai.koog.agents.core.tools.ToolParameterType
import ai.liquid.leap.function.LeapFunctionParameterType

internal val leapParameterTypeConverter =
    Converter<ToolParameterType, LeapFunctionParameterType> { parameter ->
        parameter.toLeapFunctionParameterType()
    }

private fun ToolParameterType.toLeapFunctionParameterType(): LeapFunctionParameterType =
    when (this) {
        ToolParameterType.Boolean -> LeapFunctionParameterType.Boolean()
        is ToolParameterType.Enum -> LeapFunctionParameterType.Object(
            properties = mapOf(name to LeapFunctionParameterType.String()),
            required = listOf(name),
        )

        ToolParameterType.Float -> LeapFunctionParameterType.Number()
        ToolParameterType.Integer -> LeapFunctionParameterType.Integer()
        is ToolParameterType.List -> LeapFunctionParameterType.Array(
            itemType = itemsType.toLeapFunctionParameterType(),
        )

        is ToolParameterType.Object -> LeapFunctionParameterType.Object(
            properties = properties.associate { it.name to it.type.toLeapFunctionParameterType() },
            required = requiredProperties,
        )

        ToolParameterType.String -> LeapFunctionParameterType.String()
    }