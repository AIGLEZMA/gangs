package me.aiglez.gangs.commands.player

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Conditions
import co.aikar.commands.annotation.Subcommand
import com.sk89q.worldedit.bukkit.BukkitUtil
import me.aiglez.gangs.gangs.permissions.Rank
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User

@CommandAlias("gang")
class LeaveCommand : BaseCommand() {

    @Subcommand("leave")
    fun leave(@Conditions("has_gang") user: User) {
        val gang = user.gang
        if (gang.getRank(user) == Rank.LEADER) {
            user.message(Message.LEAVE_LEADER)
            return
        }

        if (gang.removeMember(user)) {
            // remove AutoSell multiplier
            gang.core.removeBooster(user)

            // teleport to spawn
            val region = gang.mine.region
            if (region.contains(BukkitUtil.toVector(user.player.location))) {
                user.message(Message.LEAVE_SPAWN)
                user.player.performCommand("/spawn")
            }

            user.gang = null
            user.message(Message.LEAVE_LEFT)
            gang.message(Message.LEAVE_ANNOUNCEMENT, setOf(user), user.player.name)
        }
    }

}