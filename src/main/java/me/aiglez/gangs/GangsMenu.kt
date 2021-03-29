package me.aiglez.gangs

import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.menus.BalanceTopMenu
import me.aiglez.gangs.menus.CoreMenu
import me.aiglez.gangs.menus.MineMenu
import me.aiglez.gangs.menus.PermissionMenu
import me.aiglez.gangs.users.User
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Item
import org.bukkit.Material
import org.bukkit.entity.Player

class GangsMenu {

    companion object {
        @JvmStatic
        val GRAY_STAINED_GLASS_PANE: Item = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
            .data(7).name("&7").buildConsumer { e -> e.isCancelled = true }

        @JvmStatic
        fun permission(to: Player, gang: Gang) {
            if (to.isOnline) PermissionMenu(
                to, gang,
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
                MineMenu(
                    user.gang,
                    user,
                    Configuration.getString("menu-settings", "mine", "name"),
                    Configuration.getInteger("menu-settings", "mine", "lines")
                ).open()
            }
        }
    }

}