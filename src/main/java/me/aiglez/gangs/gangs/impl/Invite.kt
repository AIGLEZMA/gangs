package me.aiglez.gangs.gangs.impl

import me.aiglez.gangs.users.User
import me.lucko.helper.time.DurationFormatter
import me.lucko.helper.time.Time
import java.time.Instant
import java.util.concurrent.TimeUnit

const val EXPIRE_AFTER = 5L

data class Invite(val holder: User, val instant: Instant) {

    fun expired(): Boolean {
        return Time.diffToNow(this.instant).toMillis() >= TimeUnit.MINUTES.toMillis(EXPIRE_AFTER)
    }

    fun format(): String? {
        val duration = Time.diffToNow(instant)
        return DurationFormatter.CONCISE_LOW_ACCURACY.format(duration)
    }

}