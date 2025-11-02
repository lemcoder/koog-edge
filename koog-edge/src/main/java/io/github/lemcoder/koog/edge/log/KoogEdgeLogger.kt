package io.github.lemcoder.koog.edge.log

import android.util.Log

interface KoogEdgeLogger {
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

internal object AndroidEdgeLogger : KoogEdgeLogger {
    private val tag = this::class.simpleName
    override fun info(message: String) {
        Log.i(tag, message)
    }

    override fun warning(message: String) {
        Log.w(tag, message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    fun w(message: String) {
        Log.w(tag, message)
    }

    fun w(messageProducer: () -> String) {
        Log.w(tag, messageProducer())
    }
}