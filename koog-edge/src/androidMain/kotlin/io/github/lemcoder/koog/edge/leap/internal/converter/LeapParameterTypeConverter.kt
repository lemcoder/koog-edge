package io.github.lemcoder.koog.edge.leap.internal.converter

import ai.koog.agents.core.tools.ToolParameterType
import ai.liquid.leap.function.LeapFunctionParameterType
import ai.liquid.leap.function.LeapFunctionParameterType.*
import io.github.lemcoder.koog.edge.util.Converter

internal val leapParameterTypeConverter =
    Converter<ToolParameterType, LeapFunctionParameterType> { parameter ->
        parameter.toLeapFunctionParameterType()
    }

private fun ToolParameterType.toLeapFunctionParameterType(): LeapFunctionParameterType =
    when (this) {
        ToolParameterType.Boolean -> Boolean()
        is ToolParameterType.Enum -> String(
            enumValues = entries.toList()
        )
        ToolParameterType.Float -> LeapFunctionParameterType.Number()
        ToolParameterType.Integer -> Integer()
        is ToolParameterType.List -> Array(
            itemType = itemsType.toLeapFunctionParameterType(),
        )

        is ToolParameterType.Object -> Object(
            properties = properties.associate { it.name to it.type.toLeapFunctionParameterType() },
            required = requiredProperties,
        )

        ToolParameterType.String -> LeapFunctionParameterType.String()
        ToolParameterType.Null -> Null()
        is ToolParameterType.AnyOf -> throw UnsupportedOperationException("Leap does not support ToolParameterType.AnyOf")
    }