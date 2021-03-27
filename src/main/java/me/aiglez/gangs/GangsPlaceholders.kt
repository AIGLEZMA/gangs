package me.aiglez.gangs

import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.users.User
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.lucko.helper.Helper
import org.bukkit.entity.Player

class GangsPlaceholders : PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: Player?, identifier: String): String {
        if (player == null) return ""
        val gang: Gang? = User.get(player).gang

        return when (identifier) {
            "name" -> {
                gang?.name ?: "Gang"
            }
            "balance" -> {
                if (gang != null) Economy.format(gang.balance) else ""
            }
            "core_level" -> {
                gang?.core?.level?.toString() ?: ""
            }
            "mine_level" -> {
                gang?.mine?.level?.ordinal?.toString() ?: ""
            }
            "booster" -> {
                gang?.core?.booster?.toString() ?: ""
            }
            else -> ""
        }
    }

    override fun getIdentifier(): String {
        return "gangs"
    }

    override fun getAuthor(): String {
        return Helper.hostPlugin().description.authors[0]
    }

    override fun getVersion(): String {
        return Helper.hostPlugin().description.version
    }
}