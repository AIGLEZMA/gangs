package me.aiglez.gangs.listeners

import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.users.User
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerListeners : Listener {

    @EventHandler
    fun onChatEvent(e: AsyncPlayerChatEvent) {
        val user = User.get(e.player)
        if (user.chatEnabled() && user.hasGang()) {
            user.gang.message(Configuration.getString("chat-format"), user.gang.name, user.player.name, e.message)
            e.isCancelled = true
        }
    }
}