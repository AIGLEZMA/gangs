package me.aiglez.gangs.menus

import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.gangs.MineLevel
import me.aiglez.gangs.gangs.permissions.Permissible
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.managers.MineManager
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Log
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.Services
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Gui
import me.lucko.helper.menu.Item
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class MineMenu(private val gang: Gang, val user: User, title: String, lines: Int) : Gui(user.player, lines, title) {

    override fun redraw() {
        val currentLevel = gang.mine.level
        val unlocked = currentLevel.ordinal * Configuration.getInteger("mine-settings", "level-unlocked-per-core")

        for ((slot, level) in Services.load(MineManager::class.java).levels.withIndex()) {
            // < level
            if (level.ordinal < currentLevel.ordinal) {
                setItem(slot, alreadyBought(level))
            } else if (level.ordinal == currentLevel.ordinal) {
                setItem(slot, currentLevel(currentLevel))
            } else if (level.ordinal > currentLevel.ordinal) {
                if (level.ordinal <= unlocked) {
                    setItem(slot, unlocked(level))
                } else {
                    setItem(slot, locked(level, currentLevel))
                }
            }
        }
    }

    /*
     * A lot of boilerplate code, must fix this later
     */

    private fun alreadyBought(level: MineLevel): Item {
        val builder = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
        builder.data(5)
        builder.name(
            Placeholders.replaceIn(
                Configuration.getString("menu-settings", "mine", "items", "bought", "name"), level.ordinal
            )
        )
        builder.lore("&7Blocks:")
        builder.lore(level.lore)
        builder.lore("&7")
        builder.lore("&7Cost to upgrade: &80")
        return builder.buildConsumer { e -> e.isCancelled = true }
    }

    private fun currentLevel(level: MineLevel): Item {
        val builder = ItemStackBuilder.of(Material.NETHER_STAR)
        builder.name(
            Placeholders.replaceIn(
                Configuration.getString("menu-settings", "mine", "items", "current", "name"), level.ordinal
            )
        )
        builder.lore("&7Blocks:")
        builder.lore(level.lore)
        builder.lore("&7")
        builder.lore("&7Cost to upgrade: &80")
        return builder.buildConsumer { e -> e.isCancelled = true }
    }

    private fun unlocked(level: MineLevel): Item {
        val builder = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
        builder.data(1)
        builder.name(
            Placeholders.replaceIn(
                Configuration.getString("menu-settings", "mine", "items", "unlocked", "name"), level.ordinal
            )
        )
        builder.lore("&7Blocks:")
        builder.lore(level.lore)
        builder.lore("&7")
        builder.lore("&7Cost to upgrade: &e${Economy.format(level.upgradeCost)}")
        return builder.buildConsumer(ClickType.LEFT) { e ->
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
                        close()
                        return@run
                    }

                    val gang = user.gang
                    val current = gang.mine.level

                    // was upgraded but someone else ?
                    if (current.ordinal == level.ordinal) {
                        close()
                        return@run
                    }

                    val cost = MineLevel.calculateCost(current, level)
                    if (gang.balance <= cost) {
                        user.message(Message.INSUFFICIENT_FUNDS)
                        close()
                        return@run
                    }

                    gang.withdrawBalance(cost)
                    Log.debug(
                        "Current level: ${current.ordinal}, upgrading to ${level.ordinal}, cost: ${
                            Economy.format(cost)
                        }"
                    )
                    gang.mine.upgrade(level)

                    user.message(Message.MENU_CORE_UPGRADED, current.ordinal, level.ordinal, Economy.format(cost))
                    gang.message(Message.MENU_CORE_ANNOUNCEMENT, setOf(user), user.player.name, level.ordinal)
                    e.isCancelled = true
                    redraw()
                }
            }
        }
    }

    private fun locked(level: MineLevel, current: MineLevel): Item {
        val builder = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
        builder.data(14)
        builder.name(
            Placeholders.replaceIn(
                Configuration.getString("menu-settings", "mine", "items", "locked", "name"), level.ordinal
            )
        )
        builder.lore("&7Blocks:")
        builder.lore(level.lore)
        builder.lore("&7")
        builder.lore("&7Cost to upgrade: &e${Economy.format(MineLevel.calculateCost(current, level))}")
        return builder.buildConsumer { e -> e.isCancelled = true }
    }
}