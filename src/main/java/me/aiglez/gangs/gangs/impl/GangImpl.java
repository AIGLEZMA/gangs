package me.aiglez.gangs.gangs.impl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.aiglez.gangs.exceptions.LeaderNotFoundException;
import me.aiglez.gangs.gangs.Core;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.Mine;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.helpers.Message;
import me.aiglez.gangs.users.User;
import me.lucko.helper.gson.JsonBuilder;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

public class GangImpl implements Gang {

    private final UUID uniqueId;
    private final String name;
    private final Permissible permissible;
    private final Map<User, Rank> members;
    private final Set<Invite> invites;
    private Core core; // lateinit
    private long balance;
    private Mine mine; // lateinit

    public GangImpl(final UUID uniqueId, final String name, final Map<User, Rank> members, final Permissible permissible, final Set<Invite> invites, final long balance) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.permissible = permissible;
        this.members = members;
        this.invites = invites;
        this.balance = balance;
    }


    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Core getCore() {
        return this.core;
    }

    @Override
    public void setCore(final Core core) {
        Preconditions.checkNotNull(core, "core may not be null");
        this.core = core;
    }

    @Override
    public Mine getMine() {
        return this.mine;
    }

    @Override
    public void setMine(final Mine mine) {
        Preconditions.checkNotNull(mine, "mine may not be null");
        this.mine = mine;
    }

    @Override
    public Permissible getPermissible() {
        return this.permissible;
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(this.members.keySet());
    }

    @Override
    public void addMember(User member) {
        Preconditions.checkNotNull(member, "member may not be null");
        this.members.put(member, Rank.RECRUIT);
    }

    @Override
    public boolean removeMember(User member) {
        Preconditions.checkNotNull(member, "member may not be null");
        try {
            this.members.remove(member);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Set<Invite> getUnsafeInvites() {
        return this.invites;
    }

    @Override
    public boolean addInvite(final User user) {
        Preconditions.checkNotNull(user, "user may not be null");
        if (!isInvited(user)) {
            return this.invites.add(new Invite(user, Instant.now()));
        }
        return false;
    }

    @Override
    public void removeInvite(User user) {
        Preconditions.checkNotNull(user, "user may not be null");
        for (final Invite invite : this.invites) {
            if (invite.getHolder().getUniqueId().equals(user.getUniqueId())) {
                this.invites.remove(invite);
                return;
            }
        }
    }

    @Override
    public boolean isInvited(final User user) {
        // for loop in this case, we could do it with streams x)
        for (final Invite invite : this.invites) {
            if (invite.getHolder().getUniqueId().equals(user.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Rank getRank(final User member) {
        Preconditions.checkNotNull(member, "member may not be null");
        return this.members.getOrDefault(member, null);
    }

    @Override
    public void setRank(final User member, final Rank rank) {
        Preconditions.checkNotNull(member, "member may not be null");
        Preconditions.checkNotNull(rank, "rank may not be null");
        this.members.put(member, rank);
    }

    @Override
    public User getLeader() {
        final Optional<User> optional = this.members.keySet().stream()
                .filter(member -> getRank(member) == Rank.LEADER).findAny();
        return optional.orElseThrow(() -> new LeaderNotFoundException(this.name));
    }

    @Override
    public void depositBalance(long amount) {
        this.balance += amount;
    }

    @Override
    public void withdrawBalance(long amount) {
        this.balance -= amount;
    }

    @Override
    public long getBalance() {
        return this.balance;
    }


    @Override
    public void message(String message, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        this.members.keySet().forEach(m -> m.message(message, replacements));
    }

    @Override
    public void message(String message, Set<User> exemptions, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        this.members.keySet().stream().filter(user -> !exemptions.contains(user)).forEach(m -> m.message(message, replacements));
    }

    @Override
    public void message(Message message, Set<User> exemptions, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        message(message.getValue(), exemptions, replacements);
    }

    @Override
    public void messagec(String string, Object... replacement) {

    }

    @Override
    public void message(Message message, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        message(message.getValue(), replacements);
    }

    @Nonnull
    @Override
    public JsonElement serialize() {
        final JsonArray members = new JsonArray();
        for (final Entry<User, Rank> entry : this.members.entrySet()) {
            members.add(JsonBuilder.object()
                    .add("unique-id", entry.getKey().getUniqueId().toString())
                    .add("rank", entry.getValue().getOrdinal())
                    .build()
            );
        }

        return JsonBuilder.object()
                .add("unique-id", this.uniqueId.toString())
                .add("name", this.name)
                .add("balance", this.balance)
                .add("members", members)
                .add("mine", this.mine.serialize())
                .add("permissible", this.permissible.serialize())
                .add("core", this.core.serialize())
                .build();
    }
}
