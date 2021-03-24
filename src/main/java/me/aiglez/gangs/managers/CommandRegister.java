package me.aiglez.gangs.managers;

import co.aikar.commands.*;
import me.aiglez.gangs.commands.admin.ReloadCommand;
import me.aiglez.gangs.commands.admin.ToggleMinePlaceCommand;
import me.aiglez.gangs.commands.economy.BalanceCommand;
import me.aiglez.gangs.commands.economy.DepositCommand;
import me.aiglez.gangs.commands.economy.TopCommand;
import me.aiglez.gangs.commands.management.*;
import me.aiglez.gangs.commands.player.ChatCommand;
import me.aiglez.gangs.commands.player.LeaveCommand;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.users.User;
import me.lucko.helper.Helper;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandRegister {

    private final BukkitCommandManager manager;

    public CommandRegister() {
        this.manager = new BukkitCommandManager(Helper.hostPlugin());

        registerContexts();
        registerCompletions();
        registerConditions();
        registerSubCommands();
        try {
            this.manager.getLocales().loadYamlLanguageFile("lang.yml", Locale.ENGLISH);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        this.manager.getLocales().setDefaultLocale(Locales.ENGLISH);
    }

    private void registerSubCommands() {
        this.manager.registerCommand(new ToggleMinePlaceCommand());
        this.manager.registerCommand(new ReloadCommand());

        this.manager.registerCommand(new BalanceCommand());
        this.manager.registerCommand(new DepositCommand());
        this.manager.registerCommand(new TopCommand());


        this.manager.registerCommand(new CoreCommand());
        this.manager.registerCommand(new CreateCommand());
        this.manager.registerCommand(new DisbandCommand());
        this.manager.registerCommand(new InvitationCommand());
        this.manager.registerCommand(new MineCommand());

        this.manager.registerCommand(new ChatCommand());
        this.manager.registerCommand(new LeaveCommand());
    }

    private void registerContexts() {
        final CommandContexts<BukkitCommandExecutionContext> contexts = manager.getCommandContexts();
        contexts.registerIssuerAwareContext(User.class, c -> {
            final boolean optional = c.isOptional();
            final CommandSender sender = c.getSender();
            final boolean isPlayer = sender instanceof Player;

            if (c.hasFlag("other")) {
                final String name = c.popFirstArg();
                if (name == null) {
                    if (optional) {
                        return null;
                    } else {
                        throw new InvalidCommandArgument();
                    }
                }

                final Player player = ACFBukkitUtil.findPlayerSmart(c.getIssuer(), name);
                if (player == null) {
                    if (optional) {
                        return null;
                    }
                    throw new InvalidCommandArgument(false);
                }
                return User.get(player);

            } else {
                final Player player = isPlayer ? (Player) sender : null;
                if (player == null && !optional) {
                    throw new InvalidCommandArgument("§cThis command is player only", false);
                }

                return User.get(player);
            }
        });

        contexts.registerContext(Gang.class, c -> {
            final String name = c.popFirstArg();
            final boolean optional = c.isOptional();
            if (name == null) {
                if (optional) {
                    return null;
                } else {
                    throw new InvalidCommandArgument();
                }
            }

            final Optional<Gang> gang = Services.load(GangManager.class).getGang(name);

            if (!gang.isPresent() && !optional) {
                throw new InvalidCommandArgument(Text.colorize(Message.GANG_NOT_FOUND.getValue()), false);
            }

            return gang.orElse(null);
        });
    }

    private void registerCompletions() {
        final CommandCompletions<BukkitCommandCompletionContext> completions = this.manager.getCommandCompletions();
        completions.registerAsyncCompletion("gangs", c -> Services.load(GangManager.class)
                .getGangs()
                .stream()
                .map(Gang::getName)
                .collect(Collectors.toList()));

        completions.registerAsyncCompletion("invites", c -> {
            final User user = c.getContextValue(User.class);
            if (user == null) return null;

            return Services.load(GangManager.class)
                    .getGangs()
                    .stream().filter(gang -> gang.isInvited(user))
                    .map(Gang::getName)
                    .collect(Collectors.toList());
        });
        completions.registerAsyncCompletion("members", c -> {
            final User user = c.getContextValue(User.class);
            if (user == null || !user.hasGang()) return null;
            return user.getGang().getMembers()
                    .stream()
                    .filter(member -> member.getOfflinePlayer().isOnline())
                    .map(member -> member.getOfflinePlayer().getName())
                    .collect(Collectors.toList());
        });
    }

    private void registerConditions() {
        manager.getCommandConditions().addCondition(User.class, "has_gang", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (!value.hasGang()) {
                throw new ConditionFailedException(Text.colorize(Message.NOT_MEMBER.getValue()));
            }
        });
        manager.getCommandConditions().addCondition(User.class, "has_permission", (c, exec, value) -> {
            if (value == null || !c.hasConfig("name")) {
                return;
            }
            final Permissible.Permission permission = Permissible.Permission.valueOf(c.getConfigValue("name", "").toUpperCase());

            if (!value.test(permission)) {
                throw new ConditionFailedException(Text.colorize(Message.NO_PERMISSION.getValue()));
            }
        });
    }
}
