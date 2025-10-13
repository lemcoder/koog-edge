package io.github.lemcoder.koog.leap.internal.util

internal fun interface Converter<in V, out T> {
    fun convert(value: V): T
}