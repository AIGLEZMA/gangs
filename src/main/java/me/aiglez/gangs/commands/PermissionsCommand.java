package me.aiglez.gangs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.menus.PermissionsMenu;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.users.User;

@CommandAlias("gang")
public class PermissionsCommand extends BaseCommand {

    @Subcommand("permissions")
    public void permissionsCommand(User user) {
        if (!user.hasGang()){
            user.messagec("permission.not-member");
            return;
        }

        final Gang gang = user.getGang();
        if (gang.getRank(user) != Rank.LEADER) {
            user.messagec("permission.not-leader");
            return;
        }

        PermissionsMenu.make(user);
    }
}
