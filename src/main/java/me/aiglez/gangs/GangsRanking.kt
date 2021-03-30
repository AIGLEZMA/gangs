package me.aiglez.gangs

import com.google.common.collect.ImmutableSortedSet
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.managers.GangManager
import me.aiglez.gangs.utils.Log
import me.lucko.helper.Services
import me.lucko.helper.time.DurationFormatter
import me.lucko.helper.time.Time
import java.time.Instant
import java.util.*
import java.util.stream.Collectors


class GangsRanking {

    private val EXPIRE_AFTER = 5
    private val LIMIT: Long = 14

    private val cache: SortedSet<Gang> = sortedSetOf(kotlin.Comparator { a, b -> compareValues(a.balance, b.balance) })
    private var lastUpdated: Instant? = null

    fun cache(): ImmutableSortedSet<Gang> {
        if (lastUpdated == null) {
            lastUpdated = Instant.now()
            update()
        } else {
            val duration = Time.diffToNow(lastUpdated)
            if (duration.toMinutes() >= EXPIRE_AFTER) {
                update()
            }
        }
        return ImmutableSortedSet.copyOf(cache)
    }

    fun update() {
        cache.clear()
        this.cache.addAll(
            Services.load(GangManager::class.java).gangs.stream()
                .sorted { a, b -> compareValues(a.balance, b.balance) }
                .limit(LIMIT)
                .collect(Collectors.toList())
        )
    }

    fun lastUpdated(): String? {
        if (lastUpdated == null) return null
        val duration = Time.diffToNow(lastUpdated)
        return DurationFormatter.CONCISE_LOW_ACCURACY.format(duration)
    }

    fun handleDisband(gang: Gang) {
        Log.debug("Removing ${gang.name} from balance top (result: " +
                "${this.cache.remove(gang)}"
        )
    }
}