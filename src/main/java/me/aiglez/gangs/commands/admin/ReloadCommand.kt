package me.aiglez.gangs.commands.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import me.aiglez.gangs.managers.ConfigurationManager
import me.lucko.helper.Services
import org.bukkit.command.CommandSender
import kotlin.system.measureTimeMillis

@CommandAlias("gang")
class ReloadCommand : BaseCommand() {

    @Subcommand("reload")
    @CommandPermission("gang.admin.reload")
    fun reloadCommand(sender: CommandSender) {
        var loaded = true
        val took = measureTimeMillis {
            if (!Services.load(ConfigurationManager::class.java).loadConfiguration()) {
                loaded = false
            }
            if (!Services.load(ConfigurationManager::class.java).loadLanguage()) {
                loaded = false
            }
        }
        if (loaded) {
            sender.sendMessage("§aSuccessfully reloaded config files (took: $took ms)")
        } else {
            sender.sendMessage("§cAn error occurred while reload config files")
        }
    }
}