package me.aiglez.gangs.commands.player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.aiglez.gangs.users.User;

@CommandAlias("gang")
public class ChatCommand extends BaseCommand {

    @Subcommand("chat") @Syntax("<on/off>")
    public void chatCommand(final User user, final Boolean state) {
        user.setChatEnabled(state);
        if(state) {
            user.messagec("chat.enabled");
        } else {
            user.messagec("chat.disabled");
        }
    }

}
