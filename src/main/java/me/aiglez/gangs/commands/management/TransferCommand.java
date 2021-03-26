package me.aiglez.gangs.commands.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.users.User;

import java.util.Objects;

@CommandAlias("gang")
public class TransferCommand extends BaseCommand {

    @Subcommand("transfer")
    @Syntax("<player>")
    @CommandCompletion("@members")
    public void transfer(@Conditions("has_gang") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (gang.getRank(user) != Rank.LEADER) {
            user.message(Message.NOT_LEADER);
            return;
        }

        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }

        if(isMember(target, gang)) {
            gang.setRank(user, Rank.CO_LEADER);
            gang.setRank(target, Rank.LEADER);

            user.message(Message.TRANSFER_TRANSFERRED, gang.getName(), target.getPlayer().getName());
            target.message(Message.TRANSFER_TRANSFERRED_BY, user.getPlayer().getName(), gang.getName());

            gang.message(Message.TRANSFER_ANNOUNCEMENT, Sets.newHashSet(user, target),
                    user.getPlayer().getName(), gang.getName(), target.getPlayer().getName()
            );
        } else {
            user.message(Message.NOT_MEMBER_OTHER, target.getPlayer().getName());
        }
    }

    @Subcommand("forcetransfer")
    @Syntax("<gang> <player>")
    @CommandCompletion("@gangs")
    @CommandPermission("gang.admin.forcetransfer")
    public void forceTransfer(final User user, final Gang gang, @Flags("other") final User target) {
        if(isMember(target, gang)) {
            gang.setRank(target, Rank.LEADER);

            user.message(Message.TRANSFER_TRANSFERRED, gang.getName(), target.getPlayer().getName());
            target.message(Message.TRANSFER_TRANSFERRED_BY, user.getPlayer().getName(), gang.getName());

            gang.message(Message.TRANSFER_ANNOUNCEMENT, Sets.newHashSet(user, target),
                    user.getPlayer().getName(), gang.getName(), target.getPlayer().getName()
            );
        } else {
            user.message(Message.NOT_MEMBER_OTHER, target.getPlayer().getName());
        }
    }

    private boolean isMember(final User user, final Gang gang) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkNotNull(gang, "gang may not be null");

        if(user.hasGang()) {
            return Objects.equals(user.getGang().getName(), gang.getName());
        }
        return false;
    }
}
