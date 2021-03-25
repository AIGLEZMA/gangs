package me.aiglez.gangs

import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.managers.MineManager
import me.aiglez.gangs.menus.BalanceTopMenu
import me.aiglez.gangs.menus.CoreMenu
import me.aiglez.gangs.menus.MineMenu
import me.aiglez.gangs.menus.PermissionMenu
import me.aiglez.gangs.users.User
import me.lucko.helper.Services
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Item
import org.bukkit.Material
import kotlin.math.max

class GangsMenu {

    companion object {
        @JvmStatic
        val GRAY_STAINED_GLASS_PANE: Item = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
            .data(7).name("&7").buildConsumer { e -> e.isCancelled = true }

        @JvmStatic
        fun permission(user: User) {
            if (user.offlinePlayer.isOnline) PermissionMenu(
                user,
                Configuration.getString("menu-settings", "permission", "title")
            ).open()
        }

        @JvmStatic
        fun balancetop(user: User) {
            if (user.offlinePlayer.isOnline) BalanceTopMenu(
                user, Configuration.getString("menu-settings", "balance-top", "name")
            ).open()
        }

        @JvmStatic
        fun core(user: User) {
            if(user.offlinePlayer.isOnline) CoreMenu(
                user.gang, user, Configuration.getString("menu-settings", "core", "name")
            ).open()
        }

        @JvmStatic
        fun mine(user: User) {
            if(user.offlinePlayer.isOnline) {
                val lines = max((Services.load(MineManager::class.java).levels.size / 8), 1)
                MineMenu(
                    user.gang,
                    user,
                    Configuration.getString("menu-settings", "mine", "name"),
                    lines
                ).open()
            }
        }
    }

}