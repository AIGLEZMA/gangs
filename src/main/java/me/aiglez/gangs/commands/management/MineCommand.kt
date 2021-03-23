package me.aiglez.gangs.commands.management

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.menus.MineMenu
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
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

    @Subcommand("mined")
    fun mined(@Conditions("has_gang") user: User) {
        val gang = user.gang
        user.message("&eYour gang members mined ${gang.mine.mined}")
    }

    @Subcommand("upgrade")
    fun upgrade(@Conditions("has_gang") user: User) {
        MineMenu.create(user)
    }

    @Subcommand("forcemineupgrade")
    @Syntax("<gang>")
    @CommandCompletion("@gangs")
    @CommandPermission("gang.admin.forcemineupgrade")
    fun forceMineUpgrade(sender: CommandSender, gang: Gang?) {
        sender.sendMessage("§cNot implemented yet")
    }
}