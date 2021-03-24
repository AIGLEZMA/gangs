package me.aiglez.gangs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.users.User;

import java.util.Objects;

@CommandAlias("gang")
public class KickCommand extends BaseCommand {

    @Subcommand("kick")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    public void kick(final User user, @Flags("other") final User target) {
        if (!user.hasGang()) {
            user.messagec("kick.not-member");
            return;
        }
        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.messagec("kick.self");
            return;
        }
        if (!target.hasGang()) {
            user.messagec("kick.target-not-member", target.getPlayer().getName());
            return;
        }

        final Gang gang = user.getGang();
        if (!Objects.equals(target.getGang().getUniqueId(), gang.getUniqueId())) {
            user.messagec("kick.target-not-member", target.getPlayer().getName());
            return;
        }

        if (!user.test(Permissible.Permission.KICK)) {
            user.messagec("kick.no-permission");
            return;
        }

        final Rank superior = Rank.superior(gang.getRank(user), gang.getRank(target));
        if (superior == gang.getRank(target)) {
            user.messagec("kick.kick-superior");
            return;
        }

        // booster
        gang.removeMember(target);
        target.setGang(null);

        user.messagec("kick.kicked", target.getPlayer().getName());
        target.messagec("kick.kicked-from", gang.getName());
    }

    @Subcommand("forcekick")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    @CommandPermission("gang.admin.forcekick")
    public void forceKick(final User user, @Flags("other") final User target) {
        if (!user.hasGang()) {
            user.messagec("kick.not-member");
            return;
        }
        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.messagec("kick.self");
            return;
        }
        if (!target.hasGang()) {
            user.messagec("kick.target-not-member", target.getPlayer().getName());
            return;
        }

        final Gang gang = user.getGang();
        if (!Objects.equals(target.getGang().getUniqueId(), gang.getUniqueId())) {
            user.messagec("kick.target-not-member", target.getPlayer().getName());
            return;
        }

        gang.removeMember(target);
        target.setGang(null);

        user.messagec("kick.kicked", target.getPlayer().getName());
        target.messagec("kick.kicked-from", gang.getName());
    }
}
