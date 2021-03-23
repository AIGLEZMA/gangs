package me.aiglez.gangs.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.aiglez.gangs.users.User;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.SoftValue;

@CommandAlias("gang")
public class ToggleMinePlaceCommand extends BaseCommand {

    public static final MetadataKey<Boolean> MINE_PLACE_METADATA = MetadataKey.createBooleanKey("mine-place");

    @Subcommand("togglemineplace")
    @CommandPermission("gang.admin.togglemineplace")
    public void toggleMinePlaceCommand(final User user) {
        if(Metadata.provide(user.getPlayer()).has(MINE_PLACE_METADATA)) {
            Metadata.provide(user.getPlayer()).remove(MINE_PLACE_METADATA);
            user.messagec("toggle-mine-place.toggled-off");
        } else {
            Metadata.provide(user.getPlayer()).put(MINE_PLACE_METADATA, SoftValue.of(true));
            user.messagec("toggle-mine-place.toggled-on");
        }
    }


}
