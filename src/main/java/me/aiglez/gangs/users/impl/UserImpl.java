package me.aiglez.gangs.users.impl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.managers.GangManager;
import me.aiglez.gangs.users.User;
import me.aiglez.gangs.utils.Log;
import me.aiglez.gangs.utils.Placeholders;
import me.lucko.helper.Services;
import me.lucko.helper.gson.JsonBuilder;
import me.lucko.helper.text3.Text;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserImpl implements User {

    private final OfflinePlayer offlinePlayer;
    private String lastKnownGang;
    private Gang gang;
    private boolean chat, creating;
    private double queuedBooster;

    public UserImpl(final OfflinePlayer offlinePlayer, final String lastKnownGang) {
        this.offlinePlayer = offlinePlayer;
        this.lastKnownGang = lastKnownGang;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    @Override
    public Gang getGang() {
        if (this.lastKnownGang != null && this.gang == null) {
            // in case it's already loaded by the balance top
            final Optional<Gang> inCache = Services.load(GangManager.class).getGangs()
                    .stream().filter(filter -> filter.getName().equals(lastKnownGang)).findAny();
            if (inCache.isPresent()) {
                setGang(inCache.get());
                Log.debug("Setting " + this.offlinePlayer.getName() + "'s gang to " + this.gang.getName());
                lastKnownGang = null;
            } else {
                Log.debug("Loading " + lastKnownGang.toString());
                final Optional<Gang> gang = Services.load(GangManager.class).loadGang(lastKnownGang);
                gang.ifPresent(found -> {
                    setGang(found);
                    Log.debug("Setting " + this.offlinePlayer.getName() + "'s gang to " + this.gang.getName());
                    lastKnownGang = null; // the gang been loaded
                });
            }
        }

        return this.gang;
    }

    @Override
    public void setGang(@Nullable final Gang gang) {
        if (gang == null) {
            Log.debug("Setting " + getOfflinePlayer().getName() + " gang to null");
        }
        this.gang = gang;
    }

    @Override
    public boolean hasGang() {
        return this.getGang() != null;
    }

    @Override
    public boolean chatEnabled() {
        return this.chat;
    }

    @Override
    public void setChatEnabled(final boolean status) {
        this.chat = status;
    }

    @Override
    public boolean isCreating() {
        return this.creating;
    }

    @Override
    public void setCreating(final boolean status) {
        this.creating = status;
    }

    @Override
    public boolean hasQueuedBooster() {
        return this.queuedBooster != 0D;
    }

    @Override
    public double getQueuedBooster() {
        return this.queuedBooster;
    }

    @Override
    public void addQueuedBooster(double booster) {
        Preconditions.checkArgument(booster > 0, "booster must be positive");
        this.queuedBooster += booster;
    }

    @Override
    public void resetQueuedBooster() {
        this.queuedBooster = 0D;
    }

    @Override
    public void message(String message, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        if (this.offlinePlayer.isOnline())
            this.offlinePlayer.getPlayer().sendMessage(Text.colorize(Placeholders.replaceIn(message, replacements)));
    }

    @Deprecated
    @Override
    public void messagec(final String string, Object... replacement) {

    }

    @Override
    public void message(Message message, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        message(message.getValue(), replacements);
    }

    @Deprecated
    @Override
    public void message(String message, Set<User> exemptions, Object... replacements) {
    }

    @Deprecated
    @Override
    public void message(Message message, Set<User> exemptions, Object... replacements) {
    }

    @Nonnull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("unique-id", this.offlinePlayer.getUniqueId().toString())
                .add("gang", this.gang != null ? this.gang.getName() : "none")
                .build();
    }
}
