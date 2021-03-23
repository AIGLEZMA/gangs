package me.aiglez.gangs.listeners

import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.Helper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerListeners : Listener {

    @EventHandler
    fun onChatEvent(e: AsyncPlayerChatEvent) {
        val user = User.get(e.player)
        if (user.chatEnabled() && user.hasGang()) {
            val message = Placeholders.replaceIn(Configuration.getString("chat-format"), user.gang.name, user.player.name, e.message)
            user.gang.message(message)
            Helper.console().sendMessage("[Gangs - CHAT LOG] $message")
            e.isCancelled = true
        }
    }
}