package me.aiglez.gangs.commands.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.helpers.Message
import me.aiglez.gangs.users.User
import me.lucko.helper.metadata.Metadata
import me.lucko.helper.metadata.MetadataKey
import me.lucko.helper.metadata.SoftValue
import org.bukkit.entity.Player

@CommandAlias("gang")
class ToggleMinePlaceCommand : BaseCommand() {

    @Subcommand("togglemineplace")
    @CommandPermission("gang.admin.togglemineplace")
    fun toggleMinePlaceCommand(user: User) {
        val metadataMap = Metadata.provide(user.player)
        when (metadataMap.has(MINE_PLACE_METADATA)) {
            true -> {
                metadataMap.remove(MINE_PLACE_METADATA)
                user.message(Message.MINE_TOGGLEPLACE_DISABLED)
            }
            else -> {
                metadataMap.put(MINE_PLACE_METADATA, SoftValue.of(true))
                user.message(Message.MINE_TOGGLEPLACE_ENABLED)
            }
        }
    }

    companion object {
        // should i let this here as static ?
        private val MINE_PLACE_METADATA: MetadataKey<Boolean> = MetadataKey.createBooleanKey("mine-place")

        fun canPlace(player: Player): Boolean {
            return Metadata.provide(player).has(MINE_PLACE_METADATA)
        }
    }
}