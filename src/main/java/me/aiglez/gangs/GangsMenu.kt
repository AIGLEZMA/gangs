package me.aiglez.gangs

import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.menus.PermissionMenu
import me.aiglez.gangs.users.User
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Item
import org.bukkit.Material

class GangsMenu {

    companion object {
        @JvmStatic
        val GRAY_STAINED_GLASS_PANE: Item = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
            .data(7).name("&7").buildConsumer { e -> e.isCancelled = true }

        @JvmStatic
        fun permission(user: User) {
            if (user.offlinePlayer.isOnline) PermissionMenu(user,
                Configuration.getString("menu-settings", "permission", "title")
            )
        }
    }

}