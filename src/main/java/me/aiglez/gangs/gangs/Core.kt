package me.aiglez.gangs.gangs

import com.google.common.base.Preconditions
import com.google.gson.JsonObject
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Log
import me.clip.autosell.multipliers.Multipliers
import me.clip.autosell.objects.Multiplier
import me.lucko.helper.gson.JsonBuilder

class Core(val gang: Gang, var level: Int, var booster: Double) {

    fun upgrade() {
        Preconditions.checkArgument(
            level + 1 <= Configuration.getInteger("core-settings", "max-level"),
            "max level reached"
        )
        level++

        // remove previous level booster
        for (member in gang.members) {
            removeBooster(member)
        }

        this.booster = Configuration.getDouble("core-settings", "levels", level, "booster")
        // add current level booster
        for (member in gang.members) {
            addBooster(member, this.booster)
        }
    }

    fun addBooster(member: User, amount: Double) {
        Log.debug("Incrementing booster by $amount")
        val multiplier: Multiplier? = Multipliers.getMultiplierByUuid(member.uniqueId.toString())
        if (multiplier != null) {
            Log.debug("Player already has a multiplier ${multiplier.multiplier}")

            multiplier.isPermanent = true
            multiplier.multiplier += amount

            Log.debug("Multiplier affected ${multiplier.multiplier}")
        } else {
            val new = Multiplier(member.uniqueId.toString(), member.player.name, 0, amount, true)
            Log.debug("Adding new multiplier with id ${member.uniqueId}")

            Multipliers.addMultiplier(new)
        }
    }

    fun removeBooster(member: User) {
        Log.debug("Removing booster from " + member.uniqueId)
        val multiplier: Multiplier? = Multipliers.getMultiplierByUuid(member.uniqueId.toString())
        if (multiplier != null) {
            Log.debug("Found booster: ${multiplier.multiplier} and removing ${this.booster}")
            multiplier.multiplier -= this.booster
        }
    }

    fun serialize(): JsonObject {
        return JsonBuilder.`object`()
            .add("level", this.level)
            .add("booster", this.booster)
            .build()
    }

    companion object {

        @JvmStatic
        fun newCore(gang: Gang): Core {
            return Core(gang, 1, 0.0)
        }

        @JvmStatic
        fun deserialize(json: JsonObject, gang: Gang): Core? {
            return try {
                val level = json["level"].asInt
                val booster = json["booster"].asDouble

                Core(gang, level, booster)
            } catch (t: Throwable) {
                t.printStackTrace()
                null
            }
        }
    }
}