package me.aiglez.gangs.menus

import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.gangs.permissions.Permissible
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Gui
import me.lucko.helper.menu.scheme.MenuScheme
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.*

class CoreMenu(private val gang: Gang, val user: User, title: String) : Gui(user.player, 3, title) {

    override fun redraw() {
        if (isFirstDraw) {
            val populator = SCHEME.newPopulator(this)
            for (i in populator.slots) {
                populator.accept(
                    ItemStackBuilder.of(Material.STAINED_GLASS_PANE).data(7).name("&7")
                        .buildConsumer { e -> e.isCancelled = true })
            }
        }

        val lore: MutableList<String> = ArrayList()
        for (line in Configuration.getList("menu-settings", "core", "items", "upgrade", "lore")) {
            lore.add(
                Placeholders.replaceIn(
                    line,
                    retrieveUpgradeCost(gang.core.level + 1),
                    gang.core.level,
                    gang.core.booster
                )
            )
        }
        setItem(
            13, ItemStackBuilder.of(Material.NETHER_STAR)
                .name(Configuration.getString("menu-settings", "core", "items", "upgrade", "name"))
                .lore(lore)
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
                            // was demoted while on the menu ?
                            if (!user.test(Permissible.Permission.UPGRADE_MINE)) {
                                user.message(Message.MENU_CORE_NO_ACCESS)
                                return@run
                            }

                            val gang = user.gang
                            val nextLevel = gang.core.level + 1

                            if (nextLevel >= Configuration.getInteger("core-settings", "max-level")) {
                                user.message(Message.MENU_CORE_MAXLEVEL)
                                return@run
                            }

                            val upgradeCost = retrieveUpgradeCost(nextLevel)
                            if (upgradeCost > gang.balance) {
                                user.message(Message.INSUFFICIENT_FUNDS)
                                return@run
                            }

                            gang.withdrawBalance(upgradeCost)
                            gang.core.upgrade()

                            user.message(
                                Message.MENU_CORE_UPGRADED,
                                gang.core.level - 1,
                                gang.core.level,
                                Economy.format(upgradeCost)
                            )
                            gang.message(Message.MENU_CORE_ANNOUNCEMENT, setOf(user), user.player.name, gang.core.level)

                            redraw()
                        }
                    }
                })
    }

    companion object {

        private val SCHEME = MenuScheme()
            .masks(
                "111111111",
                "111101111",
                "111111111"
            )

        fun retrieveUpgradeCost(level: Int): Long {
            return Configuration.getLong("core-settings", "levels", level, "upgrade-cost")
        }
    }
}