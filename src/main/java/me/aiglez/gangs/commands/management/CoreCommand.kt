package me.aiglez.gangs.commands.management

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.aiglez.gangs.GangsMenu
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import me.lucko.helper.text3.Text
import org.bukkit.command.CommandSender

@CommandAlias("gang")
class CoreCommand : BaseCommand() {

    @Subcommand("core")
    fun core(@Conditions("has_gang") user: User) {
        GangsMenu.core(user)
    }

    @Subcommand("forcecoreupgrade")
    @CommandCompletion("@gangs")
    @CommandPermission("gang.admin.forcecoreupgrade")
    fun forceCoreUpgrade(sender: CommandSender, gang: Gang) {
        val core = gang.core ?: return
        if (core.level == Configuration.getInteger("core-settings", "max-level")) {
            sender.sendMessage(Text.colorize(
                Placeholders.replaceIn(Message.CORE_ADMINUPGRADE_MAX_LEVEL.value, gang.name)
            ))
            return
        }

        core.upgrade()
        gang.message(Message.CORE_ADMINUPGRADE_ANNOUNCEMENT)
        sender.sendMessage(Text.colorize(
            Placeholders.replaceIn(Message.CORE_ADMINUPGRADE_UPGRADED.value, gang.name, core.level)
        ))
    }
}