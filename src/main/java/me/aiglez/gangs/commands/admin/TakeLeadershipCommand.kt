package me.aiglez.gangs.commands.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.gangs.permissions.Rank
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User

@CommandAlias("gang")
class TakeLeadershipCommand : BaseCommand() {

    @Subcommand("takeleadership")
    @CommandPermission("gang.admin.takeleadership")
    @CommandCompletion("@gangs")
    fun takeLeadership(user: User, gang: Gang) {
        if (user.hasGang()) {
            user.message(Message.ALREADY_MEMBER)
            return
        }

        val oldLeader = gang.leader

        gang.setRank(gang.leader, Rank.CO_LEADER)
        gang.addMember(user)
        gang.setRank(user, Rank.LEADER)

        user.message(Message.TAKELEADERSHIP_TAKEN, gang.name)
        oldLeader.message(Message.TAKELEADERSHIP_TAKEN_BY_ADMIN)

        user.message(gang.leader.player.name)
    }

}