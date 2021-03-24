package me.aiglez.gangs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.users.User;

import java.util.Objects;

@CommandAlias("gang")
public class RoleCommand extends BaseCommand {

    @Subcommand("promote")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    public void promote(final User user, @Flags("other") final User target) {
        if (!user.hasGang()) {
            user.messagec("promote.not-member");
            return;
        }
        final Gang gang = user.getGang();
        if (!user.test(Permissible.Permission.PROMOTE)) {
            user.messagec("promote.no-permission");
            return;
        }
        if (!target.hasGang()) {
            user.messagec("promote.target-not-member", target.getOfflinePlayer().getName());
            return;
        } else {
            if (!Objects.equals(target.getGang().getName(), gang.getName())) {
                user.messagec("promote.target-not-member", target.getOfflinePlayer().getName());
                return;
            }
        }

        final Rank currentRank = gang.getRank(target); // recruit
        final Rank nextRank = Rank.byOrdinal(currentRank.getOrdinal() + 1); // member
        final Rank superiorRank = Rank.superior(gang.getRank(user), currentRank); // co-leader

        if (nextRank == Rank.LEADER) {
            user.messagec("promote.promote-leader");
            return;
        }
        if (currentRank == Rank.LEADER) {
            user.messagec("promote.highest-rank");
            return;
        }
        if (superiorRank == currentRank || Rank.superior(nextRank, superiorRank) == nextRank) {
            user.messagec("promote.promote-superior");
            return;
        }

        gang.setRank(target, nextRank);

        target.messagec("promote.promoted-to", nextRank.getCoolName());
        user.messagec("promote.promoted", target.getPlayer().getName(), nextRank.getCoolName());
    }

    @Subcommand("forcepromote")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    @CommandPermission("gang.admin.forcepromote")
    public void forcePromote(final User user, @Flags("other") final User target) {
        if (!user.hasGang()) {
            user.messagec("promote.not-member");
            return;
        }
        final Gang gang = user.getGang();
        if (!target.hasGang()) {
            user.messagec("promote.target-not-member", target.getOfflinePlayer().getName());
            return;
        } else {
            if (!Objects.equals(target.getGang().getName(), gang.getName())) {
                user.messagec("promote.target-not-member", target.getOfflinePlayer().getName());
                return;
            }
        }

        final Rank currentRank = gang.getRank(target); // recruit
        final Rank nextRank = Rank.byOrdinal(currentRank.getOrdinal() + 1); // member
        if (nextRank == Rank.LEADER) {
            user.messagec("promote.promote-leader");
            return;
        }
        if (currentRank == Rank.LEADER) {
            user.messagec("promote.highest-rank");
            return;
        }

        gang.setRank(target, nextRank);

        target.messagec("promote.promoted-to", nextRank.getCoolName());
        user.messagec("promote.promoted", target.getPlayer().getName(), nextRank.getCoolName());
    }

    @Subcommand("demote")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    public void demote(final User user, @Flags("other") final User target) {
        if (!user.hasGang()) {
            user.messagec("demote.not-member");
            return;
        }
        final Gang gang = user.getGang();
        if (!user.test(Permissible.Permission.PROMOTE)) {
            user.messagec("demote.no-permission");
            return;
        }
        if (!target.hasGang()) {
            user.messagec("demote.target-not-member", target.getOfflinePlayer().getName());
            return;
        } else {
            if (!Objects.equals(target.getGang().getName(), gang.getName())) {
                user.messagec("demote.target-not-member", target.getOfflinePlayer().getName());
                return;
            }
        }

        final Rank currentRank = gang.getRank(target); // recruit
        if (currentRank == Rank.RECRUIT) {
            user.messagec("demote.lowest-rank", target.getOfflinePlayer().getName());
            return;
        }

        final Rank previousRank = Rank.byOrdinal(currentRank.getOrdinal() - 1); // member
        if (currentRank.getOrdinal() >= gang.getRank(user).getOrdinal()) {
            user.messagec("demote.demote-superior");
            return;
        }

        gang.setRank(target, previousRank);

        target.messagec("demote.demoted-to", previousRank.getCoolName());
        user.messagec("demote.demoted", target.getPlayer().getName(), previousRank.getCoolName());
    }

    @Subcommand("forcedemote")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    @CommandPermission("gang.admin.forcedemote")
    public void forceDemote(final User user, final User target) {
        if (!user.hasGang()) {
            user.messagec("demote.not-member");
            return;
        }
        final Gang gang = user.getGang();
        if (!target.hasGang()) {
            user.messagec("demote.target-not-member", target.getOfflinePlayer().getName());
            return;
        } else {
            if (!Objects.equals(target.getGang().getName(), gang.getName())) {
                user.messagec("demote.target-not-member", target.getOfflinePlayer().getName());
                return;
            }
        }

        final Rank currentRank = gang.getRank(target); // recruit
        if (currentRank == Rank.RECRUIT) {
            user.messagec("demote.lowest-rank", target.getOfflinePlayer().getName());
            return;
        }

        final Rank previousRank = Rank.byOrdinal(currentRank.getOrdinal() - 1); // member

        gang.setRank(target, previousRank);

        target.messagec("demote.demoted-to", previousRank.getCoolName());
        user.messagec("demote.demoted", target.getPlayer().getName(), previousRank.getCoolName());
    }
}
