package com.rover12421.tol.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.slf4j.Marker
import java.lang.management.ManagementFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by rover12421 on 11/30/19.
 */
@Suppress("NOTHING_TO_INLINE")
object Log {
    init {
        val processName = "${SimpleDateFormat("yyyyMMdd.HHmmss").format(Date())}-${ManagementFactory.getRuntimeMXBean().name}"
        MDC.put("AllLogFileNameSuffix", processName)
        MDC.put("ErrLogFileNameSuffix", processName)

        Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler { t, e ->
            error("[UncaughtExceptionHandler]", e)
        })

        Runtime.getRuntime().addShutdownHook(ShutdownHookThread())
    }
    
    val logger = LoggerFactory.getLogger(Logger::class.java)

    @JvmStatic inline fun warn(msg: String?) {
        logger.warn(msg)
    }

    @JvmStatic inline fun warn(format: String?, arg: Any?) {
        logger.warn(format, arg)
    }

    @JvmStatic inline fun warn(format: String?, vararg arguments: Any?) {
        logger.warn(format, arguments)
    }

    @JvmStatic inline fun warn(format: String?, arg1: Any?, arg2: Any?) {
        logger.warn(format, arg1, arg2)
    }

    @JvmStatic inline fun warn(msg: String?, t: Throwable?) {
        logger.warn(msg, t)
    }

    @JvmStatic inline fun warn(marker: Marker?, msg: String?) {
        logger.warn(marker, msg)
    }

    @JvmStatic inline fun warn(marker: Marker?, format: String?, arg: Any?) {
        logger.warn(marker, format, arg)
    }

    @JvmStatic inline fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        logger.warn(marker, format, arg1, arg2)
    }

    @JvmStatic inline fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        logger.warn(marker, format, *arguments)
    }

    @JvmStatic inline fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        logger.warn(marker, msg, t)
    }

    @JvmStatic inline fun getName(): String {
        return logger.name
    }

    @JvmStatic inline fun info(msg: String?) {
        logger.info(msg)
    }

    @JvmStatic inline fun info(format: String?, arg: Any?) {
        logger.info(format, arg)
    }

    @JvmStatic inline fun info(format: String?, vararg arguments: Any?) {
        logger.info(format, arguments)
    }

    @JvmStatic inline fun info(format: String?, arg1: Any?, arg2: Any?) {
        logger.info(format, arg1, arg2)
    }

    @JvmStatic inline fun info(msg: String?, t: Throwable?) {
        logger.info(msg, t)
    }

    @JvmStatic inline fun info(marker: Marker?, msg: String?) {
        logger.info(marker, msg)
    }

    @JvmStatic inline fun info(marker: Marker?, format: String?, arg: Any?) {
        logger.info(marker, format, arg)
    }

    @JvmStatic inline fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        logger.info(marker, format, arg1, arg2)
    }

    @JvmStatic inline fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        logger.info(marker, format, *arguments)
    }

    @JvmStatic inline fun info(marker: Marker?, msg: String?, t: Throwable?) {
        logger.info(marker, msg, t)
    }

    @JvmStatic inline fun isErrorEnabled(): Boolean {
        return logger.isErrorEnabled
    }

    @JvmStatic inline fun isErrorEnabled(marker: Marker?): Boolean {
        return logger.isErrorEnabled(marker)
    }

    @JvmStatic inline fun error(msg: String?) {
        logger.error(msg)
    }

    @JvmStatic inline fun error(format: String?, arg: Any?) {
        logger.error(format, arg)
    }

    @JvmStatic inline fun error(format: String?, vararg arguments: Any?) {
        logger.error(format, arguments)
    }

    @JvmStatic inline fun error(format: String?, arg1: Any?, arg2: Any?) {
        logger.error(format, arg1, arg2)
    }

    @JvmStatic inline fun error(msg: String?, t: Throwable?) {
        logger.error(msg, t)
    }

    @JvmStatic inline fun error(marker: Marker?, msg: String?) {
        logger.error(marker, msg)
    }

    @JvmStatic inline fun error(marker: Marker?, format: String?, arg: Any?) {
        logger.error(marker, format, arg)
    }

    @JvmStatic inline fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        logger.error(marker, format, arg1, arg2)
    }

    @JvmStatic inline fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        logger.error(marker, format, *arguments)
    }

    @JvmStatic inline fun error(marker: Marker?, msg: String?, t: Throwable?) {
        logger.error(marker, msg, t)
    }

    @JvmStatic inline fun isDebugEnabled(): Boolean {
        return logger.isDebugEnabled
    }

    @JvmStatic inline fun isDebugEnabled(marker: Marker?): Boolean {
        return logger.isDebugEnabled(marker)
    }

    @JvmStatic inline fun debug(msg: String?) {
        logger.debug(msg)
    }

    @JvmStatic inline fun debug(format: String?, arg: Any?) {
        logger.debug(format, arg)
    }

    @JvmStatic inline fun debug(format: String?, vararg arguments: Any?) {
        logger.debug(format, arguments)
    }

    @JvmStatic inline fun debug(format: String?, arg1: Any?, arg2: Any?) {
        logger.debug(format, arg1, arg2)
    }

    @JvmStatic inline fun debug(msg: String?, t: Throwable?) {
        logger.debug(msg, t)
    }

    @JvmStatic inline fun debug(marker: Marker?, msg: String?) {
        logger.debug(marker, msg)
    }

    @JvmStatic inline fun debug(marker: Marker?, format: String?, arg: Any?) {
        logger.debug(marker, format, arg)
    }

    @JvmStatic inline fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        logger.debug(marker, format, arg1, arg2)
    }

    @JvmStatic inline fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        logger.debug(marker, format, *arguments)
    }

    @JvmStatic inline fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        logger.debug(marker, msg, t)
    }

    @JvmStatic inline fun isInfoEnabled(): Boolean {
        return logger.isDebugEnabled
    }

    @JvmStatic inline fun isInfoEnabled(marker: Marker?): Boolean {
        return logger.isInfoEnabled(marker)
    }

    @JvmStatic inline fun trace(msg: String?) {
        logger.trace(msg)
    }

    @JvmStatic inline fun trace(format: String?, arg: Any?) {
        logger.trace(format, arg)
    }

    @JvmStatic inline fun trace(format: String?, vararg arguments: Any?) {
        logger.trace(format, arguments)
    }

    @JvmStatic inline fun trace(format: String?, arg1: Any?, arg2: Any?) {
        logger.trace(format, arg1, arg2)
    }

    @JvmStatic inline fun trace(msg: String?, t: Throwable?) {
        logger.trace(msg, t)
    }

    @JvmStatic inline fun trace(marker: Marker?, msg: String?) {
        logger.trace(marker, msg)
    }

    @JvmStatic inline fun trace(marker: Marker?, format: String?, arg: Any?) {
        logger.trace(marker, format, arg)
    }

    @JvmStatic inline fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        logger.trace(marker, format, arg1, arg2)
    }

    @JvmStatic inline fun trace(marker: Marker?, format: String?, vararg arguments: Any?) {
        logger.trace(marker, format, *arguments)
    }

    @JvmStatic inline fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        logger.trace(marker, msg, t)
    }

    @JvmStatic inline fun isWarnEnabled(): Boolean {
        return logger.isWarnEnabled
    }

    @JvmStatic inline fun isWarnEnabled(marker: Marker?): Boolean {
        return logger.isWarnEnabled(marker)
    }

    @JvmStatic inline fun isTraceEnabled(): Boolean {
        return logger.isTraceEnabled
    }

    @JvmStatic inline fun isTraceEnabled(marker: Marker?): Boolean {
        return logger.isTraceEnabled(marker)
    }
}