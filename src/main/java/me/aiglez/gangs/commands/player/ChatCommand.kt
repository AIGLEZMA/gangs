package me.aiglez.gangs.commands.player

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User

@CommandAlias("gang")
class ChatCommand : BaseCommand() {

    @Subcommand("chat")
    @Syntax("<on/off>")
    fun chatCommand(user: User, state: Boolean) {
        user.setChatEnabled(state)
        when (state) {
            true -> user.message(Message.CHAT_ENABLED)
            else -> user.message(Message.CHAT_DISABLED)
        }
    }
}