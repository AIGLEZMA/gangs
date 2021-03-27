package me.aiglez.gangs.gangs

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sk89q.worldedit.bukkit.BukkitUtil
import com.sk89q.worldedit.regions.Region
import me.aiglez.gangs.managers.MineManager
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Log
import me.aiglez.gangs.utils.SerializeUtil
import me.aiglez.gangs.utils.WorldEditUtil
import me.lucko.helper.Schedulers
import me.lucko.helper.Services
import me.lucko.helper.gson.JsonBuilder
import org.bukkit.Location
import java.util.concurrent.TimeUnit

const val MINABLE = 100 // 117649

class Mine(private val gang: Gang, var level: MineLevel, var mined: Long, private val location: Location) {

    lateinit var region: Region
    lateinit var minableRegion: Region


    fun upgrade(level: MineLevel) {
        if (this.level.ordinal >= level.ordinal) {
            Log.warn("Cannot upgrade a mine from ${this.level.ordinal} to ${level.ordinal}")
            return
        }
        this.level = level
    }

    fun handle(amount: Int = 1) {
        this.mined += amount
        if (this.mined >= (80 * MINABLE) / 100) {
            if (!reset()) this.mined -= amount // error, reset to default
        }
    }

    fun reset(): Boolean {
        removeMiningPlayers()
        val result = WorldEditUtil.fill(this.minableRegion, this.level.blocks)
        if (result) {
            this.mined = 0
        }
        return result
    }

    fun delete() {
        removeAllPlayers()
        Schedulers.sync().runLater({ WorldEditUtil.fill(this.region, null) }, 5, TimeUnit.SECONDS)
    }

    fun teleport(user: User) {
        if (user.offlinePlayer.isOnline) user.player.teleport(this.location)
    }

    private fun removeAllPlayers() {
        this.gang.members.filter { user -> user.offlinePlayer.isOnline }
            .map { user -> user.player }
            .filter { player -> this.region.contains(BukkitUtil.toVector(player.location)) }
            .forEach { player -> player.performCommand("spawn") }
    }

    private fun removeMiningPlayers() {
        this.gang.members.filter { user -> user.offlinePlayer.isOnline }
            .filter { user -> this.minableRegion.contains(BukkitUtil.toVector(user.player.location)) }
            .forEach { user -> teleport(user) }
    }


    fun serialize(): JsonElement {
        return JsonBuilder.`object`()
            .add("level", this.level.ordinal)
            .add("mined", this.mined)
            .add("location", SerializeUtil.serialize(this.location))
            .add(
                "regions", JsonBuilder.`object`()
                    .add("minable", SerializeUtil.serialize(this.minableRegion))
                    .add("main", SerializeUtil.serialize(this.region))
                    .build()
            )
            .build()
    }

    companion object {

        @JvmStatic
        fun deserialize(json: JsonObject, gang: Gang): Mine? {
            try {
                val level: MineLevel? = Services.load(MineManager::class.java).getLevel(json["level"].asInt)
                if (level == null) {
                    Log.warn("Couldn't find the mine level ${json["level"].asInt}")
                    return null
                }
                val mined = json["mined"].asLong
                val location: Location? = SerializeUtil.deserialize(json["location"].asJsonObject)
                if (location == null) {
                    Log.warn("There was an error deserializing a location")
                    return null
                }

                val mine = Mine(gang, level, mined, location)

                val region = SerializeUtil.deserializeRegion(json["regions"].asJsonObject["main"].asJsonObject)
                val minable = SerializeUtil.deserializeRegion(json["regions"].asJsonObject["minable"].asJsonObject)

                mine.minableRegion = minable
                mine.region = region

                Log.debug("Deserialized ${gang.name}'s mine (level: $level, mined: $mined) minable area: ${minable.area} total area: ${region.area}")

                return mine
            } catch (e: Throwable) {
                e.printStackTrace() // remove later
                return null
            }
        }

    }
}