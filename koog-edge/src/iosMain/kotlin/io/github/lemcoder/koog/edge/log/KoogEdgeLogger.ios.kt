package io.github.lemcoder.koog.edge.log

import platform.Foundation.NSLog

internal class NSLogKoogEdgeLogger : KoogEdgeLogger {
    override fun info(message: String) {
        NSLog("INFO: $message")
    }

    override fun warning(message: String) {
        NSLog("WARNING: $message")
    }

    override fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            NSLog("ERROR: $message\nThrowable: ${throwable.message}")
        } else {
            NSLog("ERROR: $message")
        }
    }
}

internal actual fun getPlatformLogger(): KoogEdgeLogger = NSLogKoogEdgeLogger()