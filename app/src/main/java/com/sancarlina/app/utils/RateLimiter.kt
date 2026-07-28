package com.sancarlina.app.utils

import android.os.SystemClock

object RateLimiter {
    private val lastActionTimestamps = mutableMapOf<String, Long>()

    private val actionAttemptCounts = mutableMapOf<String, MutableList<Long>>()

    private fun getCurrentTime(): Long {
        return try {
            SystemClock.elapsedRealtime()
        } catch (e: Throwable) {
            System.currentTimeMillis()
        }
    }

    /**
     * Checks if the action is allowed. If allowed, records the timestamp and returns true.
     * If not allowed (cooldown has not expired), returns false.
     *
     * @param actionKey Unique key identifying the action (e.g., "login", "submit_form").
     * @param cooldownMillis Cooldown period in milliseconds.
     */
    @Synchronized
    fun isActionAllowed(actionKey: String, cooldownMillis: Long): Boolean {
        val now = getCurrentTime()
        val lastTime = lastActionTimestamps[actionKey] ?: 0L
        if (now - lastTime >= cooldownMillis) {
            lastActionTimestamps[actionKey] = now
            return true
        }
        return false
    }

    /**
     * Gets the remaining time of the cooldown in milliseconds, or 0 if allowed.
     */
    @Synchronized
    fun getRemainingTime(actionKey: String, cooldownMillis: Long): Long {
        val now = getCurrentTime()
        val lastTime = lastActionTimestamps[actionKey] ?: return 0L
        val elapsed = now - lastTime
        return if (elapsed < cooldownMillis) cooldownMillis - elapsed else 0L
    }

    /**
     * Checks if an action is allowed within a sliding window of max attempts.
     * E.g., admin_login: 5 attempts per 60 seconds (60000ms).
     */
    @Synchronized
    fun isWindowAllowed(actionKey: String, maxAttempts: Int, windowMillis: Long): Boolean {
        val now = getCurrentTime()
        val timestamps = actionAttemptCounts.getOrPut(actionKey) { mutableListOf() }
        timestamps.removeAll { now - it > windowMillis }
        if (timestamps.size < maxAttempts) {
            timestamps.add(now)
            return true
        }
        return false
    }

    /**
     * Gets remaining cooldown time in milliseconds for a rate-limited sliding window.
     */
    @Synchronized
    fun getRemainingWindowTime(actionKey: String, windowMillis: Long): Long {
        val now = getCurrentTime()
        val timestamps = actionAttemptCounts[actionKey] ?: return 0L
        timestamps.removeAll { now - it > windowMillis }
        val oldestInWindow = timestamps.minOrNull() ?: return 0L
        val elapsed = now - oldestInWindow
        return if (elapsed < windowMillis) windowMillis - elapsed else 0L
    }
}
