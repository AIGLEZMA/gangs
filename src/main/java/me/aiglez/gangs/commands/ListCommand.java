package me.aiglez.gangs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.users.User;

@CommandAlias("gang")
public class ListCommand extends BaseCommand {

    @Subcommand("list")
    public void list(final User user) {
        if (!user.hasGang()) {
            user.messagec("list.not-member");
            return;
        }
        final Gang gang = user.getGang();
        for (final User member : gang.getMembers()) {
            user.message("&7 - {0} &e{1}", member.getOfflinePlayer().getName(), gang.getRank(member));
        }
    }
}
