package me.aiglez.gangs.commands.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import me.aiglez.gangs.economy.Economy
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
import me.lucko.helper.Services

class WithdrawCommand : BaseCommand() {

    @Subcommand("withdraw")
    @CommandPermission("gang.admin.withdraw")
    @Syntax("<amount>")
    fun withdraw(@Conditions("has_gang") user: User, amount: Long) {
        val gang = user.gang
        if (gang.balance < amount) {
            user.message(Message.INSUFFICIENT_FUNDS)
            return
        }

        gang.withdrawBalance(amount)
        Services.load(Economy::class.java).add(user, amount.toDouble())

        user.message(Message.WITHDRAW, Economy.format(amount))
    }

}