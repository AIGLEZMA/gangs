package me.aiglez.gangs.commands.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
import me.lucko.helper.Services

@CommandAlias("gang")
class DepositCommand : BaseCommand() {

    @Subcommand("deposit")
    @Syntax("<amount>")
    fun deposit(@Conditions("has_gang") user: User, amount: Long) {
        val gang = user.gang
        val economy = Services.load(Economy::class.java)
        if (amount <= 0) {
            user.message(Message.INVALID_AMOUNT)
            return
        }

        if (amount.toDouble() > economy.balance(user)) {
            user.message(Message.DEPOSIT_INSUFFICIENT_FUNDS)
            return
        }

        economy.remove(user, amount.toDouble())
        gang.depositBalance(amount)
        user.message(Message.DEPOSIT_DEPOSIT, Economy.formatWithCommas(amount))
    }
}