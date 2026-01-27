package io.github.lemcoder.koog.edge.leap.internal.converter

import ai.koog.agents.core.tools.ToolParameterType
import ai.liquid.leap.function.LeapFunctionParameterType
import ai.liquid.leap.function.LeapFunctionParameterType.*
import ai.liquid.leap.function.LeapNull
import io.github.lemcoder.koog.edge.util.Converter

internal val leapParameterTypeConverter =
    Converter<ToolParameterType, LeapFunctionParameterType> { parameter ->
        parameter.toLeapFunctionParameterType()
    }

private fun ToolParameterType.toLeapFunctionParameterType(): LeapFunctionParameterType =
    when (this) {
        ToolParameterType.Boolean -> LeapBool()
        is ToolParameterType.Enum -> LeapStr(enumValues = entries.toList())
        ToolParameterType.Float -> LeapNum()
        ToolParameterType.Integer -> LeapNum()
        is ToolParameterType.List -> LeapArr(itemType = itemsType.toLeapFunctionParameterType())

        is ToolParameterType.Object ->
            LeapObj(
                properties =
                    properties.associate { it.name to it.type.toLeapFunctionParameterType() },
                required = requiredProperties,
            )

        ToolParameterType.String -> LeapStr()
        ToolParameterType.Null -> LeapNull
        is ToolParameterType.AnyOf ->
            throw UnsupportedOperationException("Leap does not support ToolParameterType.AnyOf")
    }
        as LeapFunctionParameterType
