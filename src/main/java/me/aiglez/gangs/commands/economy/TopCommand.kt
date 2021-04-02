package me.aiglez.gangs.commands.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.GangsMenu
import me.aiglez.gangs.GangsRanking
import me.aiglez.gangs.users.User
import me.lucko.helper.Services
import org.bukkit.entity.Player

@CommandAlias("gang")
class TopCommand : BaseCommand() {

    @Subcommand("top")
    fun top(user: User) = GangsMenu.balancetop(user)
}