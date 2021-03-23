package me.aiglez.gangs.commands.player

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.gangs.permissions.Rank
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User

@CommandAlias("gang")
class LeaveCommand : BaseCommand() {

    @Subcommand("leave")
    fun leave(@Conditions("has_gang") user: User) {
        val gang = user.gang
        if(gang.getRank(user) == Rank.LEADER) {
            user.message(Message.LEAVE_LEADER)
            return
        }

        gang.core.removeBooster(user)

        if(gang.removeMember(user)) {
            user.gang = null
            user.message(Message.LEAVE_LEFT)
            gang.message(Message.LEAVE_ANNOUNCEMENT, setOf(user), user.player.name)
        }
    }

}