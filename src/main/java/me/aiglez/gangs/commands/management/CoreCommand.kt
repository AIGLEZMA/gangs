package me.aiglez.gangs.commands.management

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.menus.CoreMenu
import me.aiglez.gangs.users.User
import me.aiglez.gangs.utils.Placeholders
import org.bukkit.command.CommandSender

@CommandAlias("gang")
class CoreCommand : BaseCommand() {

    @Subcommand("core")
    fun core(@Conditions("has_gang") user: User) {
        CoreMenu.create(user)
    }

    @Subcommand("forcecoreupgrade")
    @CommandCompletion("@gangs")
    @CommandPermission("gang.admin.forcecoreupgrade")
    fun forceCoreUpgrade(sender: CommandSender, gang: Gang) {
        val core = gang.core ?: return
        if(core.level >= Configuration.getInteger("core-settings", "max-level")) {
            sender.sendMessage(Placeholders.replaceIn(Message.CORE_ADMINUPGRADE_MAX_LEVEL.value, gang.name))
            return
        }

        core.upgrade()
        gang.message(Message.CORE_ADMINUPGRADE_UPGRADED)
        sender.sendMessage(Placeholders.replaceIn(Message.CORE_ADMINUPGRADE_UPGRADED.value, gang.name, core.level))
    }
}