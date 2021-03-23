package me.aiglez.gangs.commands.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.GangsRanking
import me.aiglez.gangs.menus.BalanceTopMenu
import me.aiglez.gangs.users.User
import me.lucko.helper.Services

@CommandAlias("gang")
class TopCommand : BaseCommand() {

    @Subcommand("top")
    fun top(user: User) {
        user.message("&eLast updated: &6${Services.load(GangsRanking::class.java).lastUpdated() ?: "&cnot yet"}")
        BalanceTopMenu.make(user)
    }
}