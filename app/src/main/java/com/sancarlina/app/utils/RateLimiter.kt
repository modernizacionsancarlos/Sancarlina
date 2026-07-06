package com.sancarlina.app.utils

import android.os.SystemClock

object RateLimiter {
    private val lastActionTimestamps = mutableMapOf<String, Long>()

    /**
     * Checks if the action is allowed. If allowed, records the timestamp and returns true.
     * If not allowed (cooldown has not expired), returns false.
     *
     * @param actionKey Unique key identifying the action (e.g., "login", "submit_form").
     * @param cooldownMillis Cooldown period in milliseconds.
     */
    @Synchronized
    fun isActionAllowed(actionKey: String, cooldownMillis: Long): Boolean {
        val now = SystemClock.elapsedRealtime()
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
        val now = SystemClock.elapsedRealtime()
        val lastTime = lastActionTimestamps[actionKey] ?: return 0L
        val elapsed = now - lastTime
        return if (elapsed < cooldownMillis) cooldownMillis - elapsed else 0L
    }
}
