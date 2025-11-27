package io.github.lemcoder.koog.edge.log

interface KoogEdgeLogger {
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

object KoogEdgeLog: KoogEdgeLogger  {
    private var customLogger: KoogEdgeLogger? = null
    private val loggerInstance: KoogEdgeLogger by lazy { getPlatformLogger() }

    private val logger: KoogEdgeLogger
        get() = customLogger ?: loggerInstance

    fun setLogger(logger: KoogEdgeLogger) {
        customLogger = logger
    }

    override fun info(message: String) = logger.info(message)

    override fun warning(message: String) = logger.warning(message)

    override fun error(message: String, throwable: Throwable?) = logger.error(message, throwable)

    // Convenience lambda versions
    fun i(message: () -> String) = info(message())
    fun w(message: () -> String) = warning(message())

    fun e(message: String, throwable: Throwable? = null) = error(message, throwable)
}
internal expect fun getPlatformLogger(): KoogEdgeLogger
