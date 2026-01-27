package io.github.lemcoder.koog.edge.util

internal fun interface Converter<in V, out T> {
    fun convert(value: V): T
}
