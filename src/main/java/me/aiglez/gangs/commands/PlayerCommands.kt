package me.aiglez.gangs.commands

import me.aiglez.gangs.gangs.Gang
import me.aiglez.gangs.managers.GangManager
import me.aiglez.gangs.users.User
import me.lucko.helper.Commands
import me.lucko.helper.Services
import me.lucko.helper.command.context.CommandContext
import me.lucko.helper.utils.Players
import org.bukkit.entity.Player

import java.util.*
import java.util.function.Predicate

class PlayerCommands {

    fun registerCommands() {
        Commands.parserRegistry().register(Gang::class.java) { value: String ->
            Services.load(GangManager::class.java).getGang(value)
        }
        Commands.parserRegistry().register(User::class.java) { value: String ->
            Optional.of(
                User.get(Players.getOfflineNullable(value))
            )
        }

        Commands.create()
            .assertPlayer("&cYou must be a player bro")
            .assertFunction(HAS_GANG_PREDICATE)
            .handler {
                run {
                    val user = User.get(it.sender())
                    it.sender().sendMessage("§aRunning")
                    val gang = user.gang
                    it.sender().sendMessage("§eGang: ${gang.name}")
                }
            }
            .register("gangtest")

    }

    companion object {

        val HAS_GANG_PREDICATE = Predicate<CommandContext<out Player>> {
            User.get(it.sender()).hasGang().also { bool -> if (!bool) it.sender().sendMessage("§cYou must have a gang") }
        }

    }
}
