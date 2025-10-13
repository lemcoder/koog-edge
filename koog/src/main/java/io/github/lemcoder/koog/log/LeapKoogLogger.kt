package io.github.lemcoder.koog.log

import android.util.Log

interface LeapKoogLogger {
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

internal object AndroidLogger : LeapKoogLogger {
    override fun info(message: String) {
        Log.i("LeapKoogLogger", message)
    }

    override fun warning(message: String) {
        Log.w("LeapKoogLogger", message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Log.e("LeapKoogLogger", message, throwable)
    }

    fun w(message: String) {
        Log.w("LeapKoogLogger", message)
    }

    fun w(messageProducer: () -> String) {
        Log.w("LeapKoogLogger", messageProducer())
    }
}