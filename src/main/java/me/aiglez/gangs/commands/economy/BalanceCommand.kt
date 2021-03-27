package me.aiglez.gangs.commands.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User

@CommandAlias("gang")
class BalanceCommand : BaseCommand() {

    @Subcommand("balance")
    fun balance(@Conditions("has_gang") user: User) {
        val gang = user.gang
        val balance = Economy.formatWithCommas(gang.balance)
        user.message(Message.BALANCE, balance)
    }
}