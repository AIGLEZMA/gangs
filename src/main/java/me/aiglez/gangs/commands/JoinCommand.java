package me.aiglez.gangs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.users.User;

@CommandAlias("gang")
public class JoinCommand extends BaseCommand {

    @Subcommand("join") @Syntax("<gang>") @CommandCompletion("@gangs_invited")
    public void join(final User user, final Gang gang) {
        if(!gang.isInvited(user)) {
            user.messagec("join.not-invited", gang.getName());
            return;
        }

        if(user.hasGang()) {
            user.messagec("join.already-member");
            return;
        }

        int max = 6;
        if(gang.getMembers().size() == max) {
            user.messagec("join.limit-reached", gang.getName());
            return;
        }

        // booster

        gang.removeInvite(user);
        gang.addMember(user);
        user.setGang(gang);

        user.messagec("join.joined", gang.getName());
    }

    @Subcommand("forcejoin") @Syntax("<gang>") @CommandCompletion("@gangs") @CommandPermission("gang.admin.forcejoin")
    public void forceJoin(final User user, final Gang gang) {
        int max = 6;
        if(gang.getMembers().size() == max) {
            user.messagec("join.limit-reached", gang.getName());
            return;
        }

        gang.addMember(user);
        user.setGang(gang);

        user.message("&c[Admin] Successfully joined {0}", gang.getName());
    }
}
