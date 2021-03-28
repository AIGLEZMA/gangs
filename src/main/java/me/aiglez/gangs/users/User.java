package me.aiglez.gangs.users;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aiglez.gangs.exceptions.OfflinePlayerNotFoundException;
import me.aiglez.gangs.exceptions.PlayerNotConnectedException;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.managers.UserManager;
import me.aiglez.gangs.users.impl.UserImpl;
import me.lucko.helper.Services;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface User extends GsonSerializable, Sender {

    static User get(final Player player) {
        Preconditions.checkNotNull(player, "player may not be null");
        return Services.load(UserManager.class).getUser(player.getUniqueId());
    }

    static User get(final OfflinePlayer offlinePlayer) {
        Preconditions.checkNotNull(offlinePlayer, "player may not be null");
        return Services.load(UserManager.class).getUser(offlinePlayer.getUniqueId());
    }

    static User deserialize(final JsonElement element) {
        final JsonObject object = element.getAsJsonObject();
        try {
            final UUID uniqueId = UUID.fromString(object.get("unique-id").getAsString());
            final OfflinePlayer offlinePlayer =
                    Players.getOffline(uniqueId)
                            .orElseThrow(() -> new OfflinePlayerNotFoundException(uniqueId));
            final String lastKnownGang = object.get("gang").getAsString();

            // see UserImpl serialize method
            return new UserImpl(
                    offlinePlayer, lastKnownGang.equalsIgnoreCase("none") ? null : lastKnownGang);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    default UUID getUniqueId() {
        return this.getOfflinePlayer().getUniqueId();
    }

    OfflinePlayer getOfflinePlayer();

    default Player getPlayer() {
        if (getOfflinePlayer().isOnline()) {
            return getOfflinePlayer().getPlayer();
        }
        throw new PlayerNotConnectedException(this);
    }

    Gang getGang();

    void setGang(final Gang gang);

    boolean hasGang();

    default boolean test(final Permissible.Permission permission) {
        Preconditions.checkNotNull(permission, "permission may not be null");
        if (!hasGang()) return false;
        final Gang gang = getGang();
        return gang.getPermissible().hasPermission(gang.getRank(this), permission);
    }

    boolean chatEnabled();

    void setChatEnabled(final boolean status);

    boolean isCreating();

    void setCreating(final boolean status);
}
