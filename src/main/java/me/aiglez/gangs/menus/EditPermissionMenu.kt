package me.aiglez.gangs.menus

import me.aiglez.gangs.GangsMenu
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.gangs.permissions.Permissible
import me.aiglez.gangs.gangs.permissions.Rank
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Gui
import me.lucko.helper.menu.scheme.MenuPopulator
import me.lucko.helper.menu.scheme.MenuScheme
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class EditPermissionMenu(val to: Player, val gang: Gang, val rank: Rank, title: String) : Gui(to, 3, title) {

    override fun redraw() {
        if (isFirstDraw) {
            // borders
            val populator = MenuPopulator(this, SCHEME)
            for (i in populator.slots) populator.accept(GangsMenu.GRAY_STAINED_GLASS_PANE)

            // back item
            setItem(BACK_SLOT, ItemStackBuilder.of(Material.PAPER)
                .name(Configuration.getString("menu-settings", "edit-permission", "items", "back", "name"))
                .lore(Configuration.getList("menu-settings", "edit-permission", "items", "back", "lore"))
                .buildConsumer(ClickType.LEFT) { e ->
                    run {
                        val clicker = e.whoClicked
                        if (clicker is Player) {
                            val user = User.get(clicker)
                            // was kicked while in the menu ?
                            if (!user.hasGang()) {
                                close()
                                return@run
                            }
                            // was force demoted while in the menu ?
                            if (user.gang.getRank(user) != Rank.LEADER) {
                                close()
                                return@run
                            }

                            GangsMenu.permission(clicker, user.gang)
                        }
                        e.isCancelled = true
                    }
                }
            )
        }

        val permissible = gang.permissible
        for ((index, permission) in Permissible.Permission.values().withIndex()) {
            val granted = permissible.hasPermission(rank, permission)

            setItem(SLOTS[index], ItemStackBuilder.of(if (granted) Material.EMERALD_BLOCK else Material.REDSTONE_BLOCK)
                .name(
                    Placeholders.replaceIn(
                        Configuration.getString(
                            "menu-settings",
                            "edit-permission",
                            "items",
                            if (granted) "granted" else "revoked",
                            "name"
                        ),
                        permission.coolName.capitalize()
                    )
                )
                .lore(
                    Configuration.getList(
                        "menu-settings",
                        "edit-permission",
                        "items",
                        if (granted) "granted" else "revoked",
                        "lore"
                    )
                )
                .buildConsumer(ClickType.LEFT) { e ->
                    run {
                        val clicker = e.whoClicked
                        if (clicker is Player) {
                            val user = User.get(clicker)
                            // was kicked while in the menu ?
                            if (!user.hasGang()) {
                                close()
                                return@run
                            }
                            // was force demoted while in the menu ?
                            if (user.gang.getRank(user) != Rank.LEADER) {
                                close()
                                return@run
                            }

                            // update
                            val localPermissible = user.gang.permissible
                            if (localPermissible.hasPermission(rank, permission)) {
                                // revoke permission
                                user.gang.permissible.setPermission(rank, permission, false)
                                user.message(
                                    Message.MENU_EDIT_PERMISSION_REVOKED,
                                    permission.coolName,
                                    rank.coolName.capitalize()
                                )
                            } else {
                                // grant permission
                                user.gang.permissible.setPermission(rank, permission, true)
                                user.message(
                                    Message.MENU_EDIT_PERMISSION_GRANTED,
                                    permission.coolName,
                                    rank.coolName.capitalize()
                                )
                            }
                        }
                        e.isCancelled = true
                        redraw()
                    }
                }
            )
        }
    }

    companion object {
        private val SLOTS = arrayOf(11, 12, 13, 14, 15)

        private const val BACK_SLOT = 22

        private val SCHEME = MenuScheme()
            .mask("111111111")
            .mask("110000011")
            .mask("111101111")
    }
}