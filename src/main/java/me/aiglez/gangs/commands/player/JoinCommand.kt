package me.aiglez.gangs.commands.player

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.helpers.Configuration
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User

@CommandAlias("gang")
class JoinCommand : BaseCommand() {

    @Subcommand("join")
    @Syntax("<gang>")
    @CommandCompletion("@gangs_invited")
    fun join(user: User, gang: Gang) {
        if(user.hasGang()) {
            user.message(Message.ALREADY_MEMBER)
            return
        }

        if(gang.isInvited(user)) {
            if(gang.members.size == Configuration.getInteger("max-members")) {
                user.message(Message.JOIN_FULL, gang.name)
                gang.message(Message.JOIN_FULL_ALERT, user.player.name)
                return
            }

            user.gang = gang

            gang.removeInvite(user)
            gang.addMember(user)

            // apply AutoSell multiplier
            gang.core.addBooster(user, gang.core.booster)

            user.message(Message.JOIN_JOINED, gang.name)
            gang.message(Message.JOIN_ANNOUNCEMENT, setOf(user), user.player.name)
        } else {
            user.message(Message.NOT_INVITED, gang.name)
        }
    }

    @Subcommand("forcejoin")
    @Syntax("<gang>")
    @CommandCompletion("@gangs")
    @CommandPermission("gang.admin.forcejoin")
    fun forceJoin(user: User, gang: Gang) {

    }
}