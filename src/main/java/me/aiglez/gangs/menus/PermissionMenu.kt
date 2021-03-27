package me.aiglez.gangs.menus

import me.aiglez.gangs.GangsMenu
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.gangs.permissions.Rank
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.users.User
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Gui
import me.lucko.helper.menu.scheme.MenuPopulator
import me.lucko.helper.menu.scheme.MenuScheme
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class PermissionMenu(val to: Player, val gang: Gang, title: String) : Gui(to, 3, title) {

    override fun redraw() {
        if (isFirstDraw) {
            val populator = MenuPopulator(this, SCHEME)
            for (i in populator.slots) populator.accept(GangsMenu.GRAY_STAINED_GLASS_PANE)
        }

        for ((index, rank) in listOf("recruit", "member", "officer", "co-leader").withIndex()) {
            val material = Material.matchMaterial(
                Configuration.getString("menu-settings", "permission", "items", rank, "material")
            ) ?: Material.STONE

            setItem(
                SLOTS[index], ItemStackBuilder.of(material)
                    .name(Configuration.getString("menu-settings", "permission", "items", rank, "name"))
                    .lore(Configuration.getList("menu-settings", "permission", "items", rank, "lore"))
                    .buildConsumer(ClickType.LEFT) { e ->
                        run {
                            val clicker = e.whoClicked
                            if (clicker is Player) {
                                val user = User.get(clicker)
                                if (!user.hasGang()) {
                                    close()
                                    return@run
                                }
                                EditPermissionMenu(
                                    to, gang, Rank.byOrdinal(index),
                                    Configuration.getString("menu-settings", "edit-permission", "title")
                                ).open()
                            }
                        }
                    }
            )
        }
    }

    companion object {
        private val SLOTS = arrayOf(10, 12, 14, 16)

        private val SCHEME = MenuScheme()
            .mask("111111111")
            .mask("101010101")
            .mask("111111111")
    }
}