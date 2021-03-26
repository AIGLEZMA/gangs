package me.aiglez.gangs.commands.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.users.User;

import java.util.Objects;

@CommandAlias("gang")
public class RoleCommand extends BaseCommand {

    @Subcommand("promote")
    @Syntax("<player>")
    @CommandCompletion("@members")
    public void promote(@Conditions("has_gang|has_permission:name=promote") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }

        if(isMember(target, gang)) {
            final Rank current = gang.getRank(target);
            final Rank next = Rank.byOrdinal(current.getOrdinal() + 1);

            // next == leader
            if(next == Rank.LEADER) {
                user.message(Message.PROMOTE_LEADER);
                return;
            }

            // you can't promote your superior or someone with the same rank
            if(next == gang.getRank(user) || Rank.superior(next, gang.getRank(user)) == next) {
                user.message(Message.PROMOTE_SUPERIOR_OR_SAME);
                return;
            }

            gang.setRank(target, next);

            user.message(Message.PROMOTE_PROMOTED, target.getPlayer().getName(), next.getCoolName()); // executor
            target.message(Message.PROMOTE_PROMOTED_BY, next.getCoolName(), user.getPlayer().getName()); // target

            gang.message(Message.PROMOTE_ANNOUNCEMENT, Sets.newHashSet(target, user),
                    user.getPlayer().getName(), target.getPlayer().getName(), next.getCoolName());
        } else {
            user.message(Message.NOT_MEMBER_OTHER, target.getPlayer().getName());
        }
    }

    @Subcommand("forcepromote")
    @Syntax("<player>")
    @CommandCompletion("@members")
    @CommandPermission("gang.admin.forcepromote")
    public void forcePromote(@Conditions("has_gang") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }

        if(isMember(target, gang)) {
            final Rank current = gang.getRank(target);
            final Rank next = Rank.byOrdinal(current.getOrdinal() + 1);

            // next == leader
            if(next == Rank.LEADER) {
                user.message(Message.PROMOTE_LEADER);
                return;
            }

            gang.setRank(target, next);

            user.message(Message.PROMOTE_PROMOTED, target.getPlayer().getName(), next.getCoolName()); // executor
            target.message(Message.PROMOTE_PROMOTED_BY, next.getCoolName(), user.getPlayer().getName()); // target

            gang.message(Message.PROMOTE_ANNOUNCEMENT, Sets.newHashSet(target, user),
                    user.getPlayer().getName(), target.getPlayer().getName(), next.getCoolName());
        } else {
            user.message(Message.NOT_MEMBER_OTHER, target.getPlayer().getName());
        }
    }

    @Subcommand("demote")
    @Syntax("<player>")
    @CommandCompletion("@members")
    public void demote(@Conditions("has_gang|has_permission:name=promote") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (!user.test(Permissible.Permission.PROMOTE)) {
            user.message(Message.NO_PERMISSION);
            return;
        }

        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }

        if(isMember(target, gang)) {
            final Rank current = gang.getRank(target);
            if (current == Rank.RECRUIT) {
                user.message(Message.DEMOTE_RECRUIT);
                return;
            }

            final Rank executorRank = gang.getRank(user);
            if (executorRank == current || Rank.superior(executorRank, current) == current) {
                user.message(Message.DEMOTE_SUPERIOR_OR_SAME);
            }
            final Rank previous = Rank.byOrdinal(current.getOrdinal() - 1);

            gang.setRank(target, previous);

            user.message(Message.DEMOTE_DEMOTED, target.getPlayer().getName(), previous.getCoolName()); // executor
            target.message(Message.DEMOTE_DEMOTED_BY, previous.getCoolName(), user.getPlayer().getName()); // target

            gang.message(Message.DEMOTE_ANNOUNCEMENT, Sets.newHashSet(target, user),
                    user.getPlayer().getName(), target.getPlayer().getName(), previous.getCoolName());
        } else {
            user.message(Message.NOT_MEMBER_OTHER, target.getPlayer().getName());
        }
    }

    @Subcommand("forcedemote")
    @Syntax("<player>")
    @CommandCompletion("@members_without_context")
    @CommandPermission("gang.admin.forcedemote")
    public void forceDemote(@Conditions("has_gang") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (!user.test(Permissible.Permission.PROMOTE)) {
            user.message(Message.NO_PERMISSION);
            return;
        }

        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }

        if(isMember(target, gang)) {
            final Rank current = gang.getRank(target);
            if (current == Rank.RECRUIT) {
                user.message(Message.DEMOTE_RECRUIT);
                return;
            }

            final Rank previous = Rank.byOrdinal(current.getOrdinal() - 1);
            gang.setRank(target, previous);

            user.message(Message.DEMOTE_DEMOTED, target.getPlayer().getName(), previous.getCoolName()); // executor
            target.message(Message.DEMOTE_DEMOTED_BY, previous.getCoolName(), user.getPlayer().getName()); // target

            gang.message(Message.DEMOTE_ANNOUNCEMENT, Sets.newHashSet(target, user),
                    user.getPlayer().getName(), target.getPlayer().getName(), previous.getCoolName());
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
