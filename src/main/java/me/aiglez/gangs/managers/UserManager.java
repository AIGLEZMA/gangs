package me.aiglez.gangs.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.aiglez.gangs.exceptions.OfflinePlayerNotFoundException;
import me.aiglez.gangs.helpers.Configuration;
import me.aiglez.gangs.users.User;
import me.aiglez.gangs.users.impl.UserImpl;
import me.aiglez.gangs.utils.Log;
import me.lucko.helper.Helper;
import me.lucko.helper.serialize.GsonStorageHandler;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class UserManager {


    private static final File DATA_FOLDER =
            new File(Helper.hostPlugin().getDataFolder() + File.separator + "data");
    private final Set<User> users = Sets.newHashSet();
    private final GsonStorageHandler<Set<User>> storage;

    public UserManager() {
        storage = new GsonStorageHandler<>("users", ".json", DATA_FOLDER, new TypeToken<Set<User>>() {});
    }

    /*
     * Remember gangs depend on user instance but users don't depend on gangs !
     */
    public User getUser(final UUID uniqueId) {
        Preconditions.checkNotNull(uniqueId, "unique-id may not be null");
        final OfflinePlayer offlinePlayer =
                Players.getOffline(uniqueId)
                        .orElseThrow(() -> new OfflinePlayerNotFoundException(uniqueId));
        for (final User user : this.users) {
            if (user.getUniqueId().equals(uniqueId)) {
                return user;
            }
        }

        final User user = new UserImpl(offlinePlayer);
        this.users.add(user);
        return user;
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(this.users);
    }

    public void saveUsers() {
        if (this.users.isEmpty()) {
            Log.warn("No user found (in cache) to save");
            return;
        }

        if (Configuration.getBoolean("backup")) {
            this.storage.saveAndBackup(
                    this.users.stream().filter(User::hasGang).collect(Collectors.toSet()));
            Log.info("Saved " + this.users.size() + " user(s) (with backup)");
            return;
        }

        this.storage.save(this.users.stream().filter(User::hasGang).collect(Collectors.toSet()));
        Log.info("Saved " + this.users.size() + " user(s)");
    }

    public void loadUsers() {
        final Optional<Set<User>> loaded = this.storage.load();
        if (loaded.isPresent()) {
            this.users.addAll(loaded.get());
            Log.info("Loaded " + this.users.size() + " user(s)");
            return;
        }

        Log.warn("No user found to load");
    }
}
