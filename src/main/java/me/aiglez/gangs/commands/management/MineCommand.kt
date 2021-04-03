package me.aiglez.gangs.commands.management

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.aiglez.gangs.GangsMenu
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.gangs.MineLevel
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.managers.MineManager
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.Services
import me.lucko.helper.text3.Text
import org.bukkit.command.CommandSender

@CommandAlias("gang")
@Subcommand("mine")
class MineCommand : BaseCommand() {

    @Default
    fun mine(@Conditions("has_gang") user: User) {
        val gang = user.gang
        gang.mine.teleport(user)
        user.message(Message.MINE_TELEPORT)
    }

    /*
    // TODO: remove this later
    @Subcommand("mined")
    fun mined(@Conditions("has_gang") user: User) {
        val gang = user.gang
        user.message("&eYour gang members mined ${gang.mine.mined}")
    }

    // TODO: remove this later
    @Subcommand("reset")
    fun reset(@Conditions("has_gang") user: User) {
        val gang = user.gang
        user.message("&eReset result: ${gang.mine.reset()}")
    }
     */

    @Subcommand("upgrade")
    fun upgrade(@Conditions("has_gang") user: User) {
        GangsMenu.mine(user)
    }

    @Subcommand("forcemineupgrade")
    @CommandPermission("gang.admin.forcemineupgrade")
    @CommandCompletion("@gangs")
    fun forceMineUpgrade(sender: CommandSender, gang: Gang) {
        val unlocked = gang.core.level * Configuration.getInteger("mine-settings", "level-unlocked-per-core")
        val nextLevel: MineLevel? = Services.load(MineManager::class.java).getLevel(gang.mine.level.ordinal + 1)

        if (nextLevel != null) {
            if (nextLevel.ordinal > unlocked) {
                sender.sendMessage(Text.colorize(Placeholders.replaceIn(
                    Message.MINE_ADMINUPGRADE_NOT_UNLOCKED.value,
                    nextLevel.ordinal,
                    gang.name
                )))
                return
            }

            gang.mine.upgrade(nextLevel)
            gang.message(Message.MINE_ADMINUPGRADE_ANNOUNCEMENT)
            sender.sendMessage(Text.colorize(Placeholders.replaceIn(
                Message.MINE_ADMINUPGRADE_UPGRADED.value,
                gang.name,
                nextLevel.ordinal
            )))

        } else {
            sender.sendMessage(
                Text.colorize(
                    Placeholders.replaceIn(
                    Message.MINE_ADMINUPGRADE_LEVELNOTFOUND.value,
                    gang.mine.level.ordinal + 1
                )))
        }
    }
}