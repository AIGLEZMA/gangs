package me.aiglez.gangs.commands.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import me.aiglez.gangs.GangsMenu;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.users.User;
import me.lucko.helper.Services;

@SuppressWarnings("AccessStaticViaInstance")
@CommandAlias("gang")
public class PermissionsCommand extends BaseCommand {

    @Subcommand("permissions")
    public void permissions(@Conditions("has_gang") User user) {
        final Gang gang = user.getGang();
        if (gang.getRank(user) != Rank.LEADER) {
            user.message(Message.NOT_LEADER);
            return;
        }

        Services.load(GangsMenu.class).permission(user);
    }
}
