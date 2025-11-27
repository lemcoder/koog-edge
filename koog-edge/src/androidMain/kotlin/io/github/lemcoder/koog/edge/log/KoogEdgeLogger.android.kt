package io.github.lemcoder.koog.edge.log

import android.util.Log

internal class AndroidEdgeLogger : KoogEdgeLogger {
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

internal actual fun getPlatformLogger(): KoogEdgeLogger {
    return AndroidEdgeLogger()
}