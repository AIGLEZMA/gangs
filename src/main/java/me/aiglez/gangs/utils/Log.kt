package me.aiglez.gangs.utils

import me.aiglez.gangs.helpers.Configuration
import me.lucko.helper.Helper
import me.lucko.helper.text3.Text
import org.bukkit.ChatColor

object Log {

    @JvmStatic
    fun info(message: String?) {
        if (message != null) print(ChatColor.GREEN, "LOG", message)
    }

    @JvmStatic
    fun warn(message: String?) {
        if (message != null) print(ChatColor.YELLOW, "WARN", message)
    }

    @JvmStatic
    fun debug(message: String?) {
        if (Configuration.getBoolean("debug") && message != null) {
            print(ChatColor.AQUA, "DEBUG", message)
        }
    }

    @JvmStatic
    fun severe(message: String?) {
        if (message != null) print(ChatColor.RED, "SEVERE", message)
    }

    private fun print(color: ChatColor, prefix: String, message: String) {
        Helper.console().sendMessage(
            Text.colorize("$color[Gangs - $prefix] ${ChatColor.RESET}$message")
        )
    }


}