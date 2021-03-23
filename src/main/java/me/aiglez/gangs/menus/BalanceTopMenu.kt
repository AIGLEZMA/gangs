package me.aiglez.gangs.menus

import me.aiglez.gangs.GangsRanking
import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.Services
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Gui
import me.lucko.helper.menu.scheme.MenuScheme
import org.bukkit.Material
import java.util.function.Consumer

class BalanceTopMenu(val user: User, title: String) : Gui(user.player, 4, title) {

    override fun redraw() {
        if(isFirstDraw) {
            val populator = PANES.newPopulator(this)
            for (i in populator.slots) {
                populator.accept(
                    ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                        .data(7)
                        .name("§7")
                        .buildConsumer { e -> e.isCancelled = true })
            }
        }

        for ((index, gang) in Services.load(GangsRanking::class.java).cache().withIndex()) {
            val name = Placeholders.replaceIn(
                Configuration.getString("menu-settings", "balance-top", "items", "gang", "name"),
                index + 1, gang.name
            )
            val lore: MutableList<String> = mutableListOf()
            for (line in Configuration.getList("menu-settings", "balance-top", "items", "gang", "lore")) {
                lore += Placeholders.replaceIn(
                    line,
                    gang.leader.player.name,
                    Economy.format(gang.balance),
                    gang.core.level,
                    gang.mine.level.ordinal,
                    gang.members.size
                )
            }
            gang.members.forEach(Consumer { member: User -> lore += "&7 - " + member.player.name })
            try {
                setItem(
                    SLOTS[index], ItemStackBuilder.of(Material.DIAMOND_PICKAXE)
                        .name(name)
                        .lore(lore)
                        .buildConsumer { e -> e.isCancelled = true })
            } catch (e: ArrayIndexOutOfBoundsException) {
                user.message("${e.message}")
            }
        }
    }

    companion object {

        private val SLOTS = intArrayOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
        )

        private val PANES = MenuScheme()
            .mask("111111111")
            .mask("100000001")
            .mask("100000001")
            .mask("111111111")

        fun make(user: User) {
            BalanceTopMenu(user, Configuration.getString("menu-settings", "balance-top", "name")).open()
        }
    }
}