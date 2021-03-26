package me.aiglez.gangs.commands.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.impl.Invite;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.users.User;

import java.util.Objects;

@CommandAlias("gang")
public class InvitationCommand extends BaseCommand {

    @Subcommand("decline|reject")
    @Syntax("<gang>")
    @CommandCompletion("@gangs_invited")
    public void decline(final User user, final Gang gang) {
        if (!gang.isInvited(user)) {
            user.message(Message.NOT_INVITED, gang.getName());
            return;
        }

        gang.removeInvite(user);
        user.message(Message.DECLINE_DECLINED, gang.getName());
    }

    @Subcommand("invite")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void invite(@Conditions("has_gang") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (!user.test(Permissible.Permission.INVITE)) {
            user.message(Message.NO_PERMISSION);
            return;
        }
        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }
        if (gang.isInvited(target)) {
            user.message(Message.INVITE_ALREADY, target.getPlayer().getName());
            return;
        }

        if (target.hasGang()) {
            if (Objects.equals(target.getGang().getName(), gang.getName())) {
                user.message(Message.INVITE_ALREADY_MEMBER, target.getPlayer().getName());
            } else {
                user.message(
                        Message.INVITE_ALREADY_MEMBER_OTHER,
                        target.getPlayer().getName(),
                        target.getGang().getName());
            }
            return;
        }

        if (gang.addInvite(target)) {
            user.message(Message.INVITE_INVITED_SENDER, target.getPlayer().getName());
            target.message(Message.INVITE_INVITED_SENDER, gang.getName());
        }
    }

    // TODO: remove later
    @Subcommand("invites")
    public void invitesCommand(@Conditions("has_gang") final User user) {
        final Gang gang = user.getGang();
        for (final Invite invite : gang.getInvites()) {
            user.message(
                    "&7 - {0} (passed: &e{1})",
                    invite.getHolder().getOfflinePlayer().getName(), invite.format());
        }
    }
}
