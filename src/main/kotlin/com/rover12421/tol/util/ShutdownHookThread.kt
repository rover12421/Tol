package com.rover12421.tol.util

import java.lang.management.ManagementFactory
import java.time.Duration
import java.time.LocalDateTime


/**
 * Created by rover12421 on 12/1/19.
 */
class ShutdownHookThread : Thread() {
    val startTime = LocalDateTime.now()

    override fun run() {
        val endTime = LocalDateTime.now()
        val name = ManagementFactory.getRuntimeMXBean().name

        val duration = Duration.between(startTime, endTime)
        Log.info("$name running start : $startTime")
        Log.info("$name running end : $endTime")
        Log.info("$name running time : $duration")
        Log.info("$name running time(ms) : ${duration.toMillis()}")
        Log.info("$name running time(m) : ${duration.toMinutes()}")
    }
}