package me.aiglez.gangs.gangs;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aiglez.gangs.gangs.impl.GangImpl;
import me.aiglez.gangs.gangs.impl.Invite;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.managers.UserManager;
import me.aiglez.gangs.users.Sender;
import me.aiglez.gangs.users.User;
import me.lucko.helper.Services;
import me.lucko.helper.gson.GsonSerializable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Gang extends GsonSerializable, Sender {

    UUID getUniqueId();

    String getName();

    Mine getMine();

    Core getCore();

    void setCore(final Core core);

    void setMine(final Mine mine);

    Permissible getPermissible();

    Set<User> getMembers();

    void addMember(final User member);

    boolean removeMember(final User member);

    Rank getRank(final User member);

    void setRank(final User member, final Rank rank);

    User getLeader();

    void depositBalance(final long amount);

    void withdrawBalance(final long amount);

    long getBalance();

    Set<Invite> getUnsafeInvites();

    default Set<Invite> getInvites() {
        final Set<Invite> invites = Sets.newHashSet();
        for (final Invite invite : getUnsafeInvites()) {
            if(invite.expired()) removeInvite(invite.getHolder());
        }
        return invites;
    }

    boolean addInvite(final User user);

    void removeInvite(final User user);

    boolean isInvited(final User user);


    static Gang newGang(final String name) {
        Preconditions.checkNotNull(name, "name may not be null");
        return new GangImpl(UUID.randomUUID(), name, Maps.newHashMap(), Permissible.newPermissible(), Sets.newHashSet(), 0L);
    }

    static Gang deserialize(final JsonElement element) {
        final JsonObject object = element.getAsJsonObject();
        try {
            final UUID uniqueId = UUID.fromString(object.get("unique-id").getAsString());
            final String name = object.get("name").getAsString();
            final long balance = object.get("balance").getAsLong();

            final Map<User, Rank> members = Maps.newHashMap();
            for (final JsonElement element1 : object.get("members").getAsJsonArray()) {
                final JsonObject object1 = element1.getAsJsonObject();

                final UUID memberUniqueId = UUID.fromString(object1.get("unique-id").getAsString());
                final Rank memberRank = Rank.byOrdinal(object1.get("rank").getAsInt());

                final User user = Services.load(UserManager.class).getUser(memberUniqueId);
                members.put(user, memberRank);
            }

            final Permissible permissible = Permissible.deserialize(object.get("permissible"));

            final Gang gang = new GangImpl(uniqueId, name, members, permissible, Sets.newHashSet(), balance);
            gang.setMine(Mine.deserialize(object.get("mine").getAsJsonObject(), gang));
            gang.setCore(Core.deserialize(object.get("core").getAsJsonObject(), gang));

            return gang;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
