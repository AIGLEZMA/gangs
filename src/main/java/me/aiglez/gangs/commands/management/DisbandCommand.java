package me.aiglez.gangs.commands.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.Sets;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.managers.GangManager;
import me.aiglez.gangs.users.User;
import me.aiglez.gangs.utils.Placeholders;
import me.lucko.helper.Services;
import me.lucko.helper.metadata.ExpireAfterAccessValue;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.text3.Component;
import me.lucko.helper.text3.Text;
import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.adapter.bukkit.TextAdapter;
import me.lucko.helper.text3.event.ClickEvent;
import me.lucko.helper.text3.event.HoverEvent;
import me.lucko.helper.text3.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@CommandAlias("gang")
public class DisbandCommand extends BaseCommand {

    private final static long EXPIRE_AFTER = 10;
    private final static Component CONFIRM_MESSAGE = TextComponent.builder()
            .content(Text.colorize(Message.DISBAND_CONFIRM.getValue()))
            .clickEvent(ClickEvent.runCommand("/gang disband"))
            .hoverEvent(HoverEvent.showText(TextComponent.of("Click to confirm disband")
                    .color(TextColor.AQUA)))
            .build();

    private final MetadataKey<Boolean> DISBAND_METADATA_KEY = MetadataKey.createBooleanKey("disband");

    @Subcommand("disband|delete")
    public void disband(@Conditions("has_gang") final User user) {
        final Gang gang = user.getGang();
        if (gang.getRank(user) != Rank.LEADER) {
            user.message(Message.DISBAND_MUST_BE_LEADER);
            return;
        }

        if (Metadata.provide(user.getPlayer()).has(DISBAND_METADATA_KEY)) {
            user.message(Message.DISBAND_DISBANDED);
            gang.message(Message.DISBAND_ANNOUNCEMENT, Sets.newHashSet(user), user.getPlayer().getName());

            gang.getMine().delete();
            gang.getMembers().forEach(member -> {
                gang.getCore().removeBooster(member);
                member.setGang(null);
            });
            Services.load(GangManager.class).unregisterGang(gang);

            Metadata.provideForPlayer(user.getPlayer()).remove(DISBAND_METADATA_KEY);
        } else {
            Metadata.provideForPlayer(user.getPlayer()).put(DISBAND_METADATA_KEY, ExpireAfterAccessValue.of(true, EXPIRE_AFTER, TimeUnit.SECONDS));
            TextAdapter.sendMessage(user.getPlayer(), CONFIRM_MESSAGE);
        }
    }

    @Subcommand("forcedisband")
    @Syntax("<gang>")
    @CommandCompletion("@gangs")
    @CommandPermission("gang.admin.forcedisband")
    public void forceDisband(final CommandSender sender, final Gang gang) {
        final String name = sender instanceof Player ? sender.getName() : "CONSOLE";

        sender.sendMessage(Placeholders.replaceIn(Message.DISBAND_FORCEDISBANDED.getValue(), gang.getName()));

        gang.message(Message.DISBAND_ANNOUNCEMENT, name);
        gang.getMine().delete();
        gang.getMembers().forEach(member -> {
            gang.getCore().removeBooster(member);
            member.setGang(null);
        });
        Services.load(GangManager.class).unregisterGang(gang);
    }
}
