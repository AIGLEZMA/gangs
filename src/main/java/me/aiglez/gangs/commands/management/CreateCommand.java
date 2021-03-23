package me.aiglez.gangs.commands.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.google.common.base.Preconditions;
import me.aiglez.gangs.gangs.Core;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.Mine;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.managers.GangManager;
import me.aiglez.gangs.managers.MineManager;
import me.aiglez.gangs.users.User;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandAlias("gang")
public class CreateCommand extends BaseCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("[^A-Za-z0-9]");

    @Subcommand("create") @Syntax("<name>")
    public void create(final User user, final String name) {
        if(user.isCreating()) {
            user.message(Message.CREATE_ALREADY_CREATING);
            return;
        }

        if(user.hasGang()) {
            user.message(Message.CREATE_ALREADY_MEMBER);
            return;
        }

        if(!validateName(name)) {
            user.message(Message.CREATE_INVALID_NAME, name);
            return;
        }

        if(Services.load(GangManager.class).isNameTaken(name)) {
            user.message(Message.CREATE_NAME_TAKEN, name);
            return;
        }

        user.setCreating(true);

        final Gang gang = Gang.newGang(name);
        gang.setRank(user, Rank.LEADER);
        user.setGang(gang);

        user.message(Message.CREATE_IN_PROGRESS);

        final Optional<Mine> newMine = Services.load(MineManager.class).createNewMine(user);
        if(!newMine.isPresent()) {
            user.message(Message.CREATE_MINE_ERROR);
            user.setGang(null);
            user.setCreating(false);

            return;
        }
        gang.setMine(newMine.get());
        gang.setCore(Core.newCore(gang));

        user.setCreating(false);
        Services.load(GangManager.class).registerGang(gang);
        user.message(Message.CREATE_CREATED);

        // reset the mine after 3 seconds
        Schedulers.sync().runLater(() -> gang.getMine().reset(), 3, TimeUnit.SECONDS);
    }

    private boolean validateName(final String name) {
        Preconditions.checkNotNull(name, "name may not be null");
        if(name.length() < 3 || name.length() > 12) return false;
        final Matcher matcher = NAME_PATTERN.matcher(name);
        return !matcher.find();
    }
}
