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
public class KickCommand extends BaseCommand {

    @Subcommand("kick")
    @Syntax("<player>")
    @CommandCompletion("@members")
    public void kick(@Conditions("has_gang|has_permission:name=kick") final User user, @Flags("other") final User target) {
        final Gang gang = user.getGang();
        if (Objects.equals(user.getUniqueId(), target.getUniqueId())) {
            user.message(Message.NOT_SELF);
            return;
        }
        if(isMember(target, gang)) {
            final Rank executorRank = gang.getRank(user);
            final Rank targetRank = gang.getRank(target);

            if(executorRank == targetRank || Rank.superior(executorRank, targetRank) == targetRank) {
                user.message(Message.KICK_SUPERIOR_OR_SAME);
                return;
            }

            gang.getCore().removeBooster(target);
            gang.removeMember(target);

            target.setGang(null);

            user.message(Message.KICK_KICK, target.getPlayer().getName());
            target.message(Message.KICK_KICKED_BY, gang.getName(), user.getPlayer().getName());

            gang.message(Message.KICK_ANNOUNCEMENT, Sets.newHashSet(user), target.getPlayer().getName(),
                    user.getPlayer().getName());
        }
    }

    @Subcommand("forcekick")
    @Syntax("<player>")
    @CommandCompletion("@members")
    @CommandPermission("gang.admin.forcekick")
    public void forceKick(@Conditions("has_gang") final User user, @Flags("other") final User target) {

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
