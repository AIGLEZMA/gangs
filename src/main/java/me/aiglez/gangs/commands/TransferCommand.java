package me.aiglez.gangs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.users.User;

import java.util.Objects;

@CommandAlias("gang")
public class TransferCommand extends BaseCommand {

    @Subcommand("transfer") @Syntax("<player>") @CommandCompletion("@members_without_context")
    public void transfer(final User user, @Flags("other") final User target) {
        if(!user.hasGang()) {
            user.messagec("transfer.not-member");
            return;
        }

        final Gang gang = user.getGang();
        final Rank rank = gang.getRank(user);

        if(rank != Rank.LEADER) {
            user.messagec("transfer.not-leader");
            return;
        }

        if(Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.messagec("transfer.self");
            return;
        }

        if(!target.hasGang()) {
            user.messagec("transfer.target-not-member", target.getPlayer().getName());
            return;
        }

        if(!Objects.equals(target.getGang().getUniqueId(), gang.getUniqueId())) {
            user.messagec("transfer.target-not-member", target.getPlayer().getName());
            return;
        }

        gang.setRank(user, Rank.CO_LEADER);
        gang.setRank(target, Rank.LEADER);

        target.messagec("transfer.become-leader");
        user.messagec("transfer.transferred", target.getPlayer().getName());

        gang.messagec("transfer.announcement", user.getPlayer().getName(), gang.getName(),
                target.getPlayer().getName());
    }

    @Subcommand("forcetransfer") @Syntax("<gang> <player>") @CommandCompletion("@members_without_context")
    @CommandPermission("gang.admin.forcetransfer")
    public void forceTransfer(final User user, final Gang gang, final User target) {
        user.message("&cNot implemented yet");
    }

}
