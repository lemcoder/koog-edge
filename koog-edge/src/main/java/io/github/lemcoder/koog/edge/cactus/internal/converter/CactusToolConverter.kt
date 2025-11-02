package io.github.lemcoder.koog.edge.cactus.internal.converter

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import com.cactus.models.CactusTool
import com.cactus.models.ToolParameter
import com.cactus.models.createTool
import io.github.lemcoder.koog.edge.util.Converter

internal val koogToCactusToolConverter = Converter<ToolDescriptor, CactusTool> { tool ->
    val requiredParameters = tool.requiredParameters.map(
        koogToCactusRequiredParameterConverter::convert
    )
    val optionalParameters = tool.optionalParameters.map(
        koogToCactusOptionalParameterConverter::convert
    )

    createTool(
        name = tool.name,
        description = tool.description,
        parameters = (requiredParameters + optionalParameters).toMap()
    )
}

private val koogToCactusRequiredParameterConverter =
    Converter<ToolParameterDescriptor, Pair<String, ToolParameter>> { descriptor ->
        val name = descriptor.name
        val parameter = ToolParameter(
            required = true,
            description = descriptor.description,
            type = koogToCactusParameterTypeConverter.convert(descriptor.type)
        )

        name to parameter
    }

private val koogToCactusOptionalParameterConverter =
    Converter<ToolParameterDescriptor, Pair<String, ToolParameter>> { descriptor ->
        val name = descriptor.name
        val parameter = ToolParameter(
            required = false,
            description = descriptor.description,
            type = koogToCactusParameterTypeConverter.convert(descriptor.type)
        )

        name to parameter
    }

private val koogToCactusParameterTypeConverter = Converter<ToolParameterType, String> { type ->
    type.name.lowercase()
}

