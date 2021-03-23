package me.aiglez.gangs.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.helpers.Configuration;
import me.aiglez.gangs.utils.Log;
import me.lucko.helper.Helper;
import me.lucko.helper.serialize.GsonStorageHandler;

import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("UnstableApiUsage")
public class GangManager {

    private static final File DATA_FOLDER = new File(Helper.hostPlugin().getDataFolder() + File.separator + "data");

    private final Set<Gang> gangs = Sets.newHashSet();
    private final Set<String> takenNames = Sets.newHashSet();

    private final GsonStorageHandler<Set<String>> takenNamesStorage;
    private final GsonStorageHandler<Set<UUID>> balanceTopStorage;

    public GangManager() {
        this.takenNamesStorage = new GsonStorageHandler<>("taken_names", ".json", DATA_FOLDER, new TypeToken<Set<String>>() {});
        this.balanceTopStorage = new GsonStorageHandler<>("balance_top", ".json", DATA_FOLDER, new TypeToken<Set<UUID>>() {});
    }


    public void registerGang(final Gang gang) {
        Preconditions.checkNotNull(gang, "gang may not be null");
        this.gangs.add(gang);
        this.takenNames.add(gang.getName());
        Log.debug("Registered gang with name : " + gang.getName());
    }

    public void unregisterGang(final Gang gang) {
        Preconditions.checkNotNull(gang, "gang may not be null");
        gang.getMembers().forEach(member -> member.setGang(null));
        this.gangs.remove(gang);
        this.takenNames.remove(gang.getName());
        Log.debug("Unregistered gang with name : " + gang.getName());
    }

    public Set<Gang> getGangs() { return Collections.unmodifiableSet(this.gangs); }

    public void saveGangs() {
        if(this.gangs.isEmpty()) {
            Log.warn("No gang found (in cache) to save");
            return;
        }
        final File file = new File(DATA_FOLDER + File.separator + "/gangs");

        for (final Gang gang : this.gangs) {
            final GsonStorageHandler<Gang> storageHandler =
                    new GsonStorageHandler<>(gang.getUniqueId().toString(), ".json", file, new TypeToken<Gang>() {});

            storageHandler.save(gang);
        }
        Log.info("Successfully saved " + this.gangs.size() + " gang(s)");
    }

    public Optional<Gang> loadGang(final UUID uniqueId) {
        Preconditions.checkNotNull(uniqueId, "unique id may not be null");
        final File file = new File(DATA_FOLDER + File.separator + "/gangs");

        final Optional<Gang> optional = new GsonStorageHandler<>(uniqueId.toString(), ".json", file, new TypeToken<Gang>() {}).load();

        optional.ifPresent(this.gangs::add);

        return optional;
    }

    public void saveTakenNames() {
        if(this.takenNames.isEmpty()) {
            Log.warn("No saved taken name was found. Skipping...");
            return;
        }

        if(Configuration.getBoolean("backup")) {
            this.takenNamesStorage.saveAndBackup(this.takenNames);
        } else {
            this.takenNamesStorage.save(this.takenNames);
        }
        Log.info("Saved " + this.takenNames.size() + " taken names");
    }

    public void loadTakenNames() {
        final Optional<Set<String>> loaded = this.takenNamesStorage.load();
        if(loaded.isPresent()) {
            this.takenNames.addAll(loaded.get());
            Log.info("Loaded " + this.takenNames.size() + " taken name(s)");
            return;
        }

        Log.warn("No taken name found to load");
    }

    public boolean isNameTaken(final String name) {
        Preconditions.checkNotNull(name, "name");
        for (final String takenName : this.takenNames) {
            if(takenName.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void loadBalanceTop() {
        final Optional<Set<UUID>> loaded = this.balanceTopStorage.load();
        final AtomicInteger count = new AtomicInteger();
        loaded.ifPresent(uuids -> {
            for (final UUID uuid : uuids) {
                loadGang(uuid).ifPresent(gang -> {
                    registerGang(gang);
                    count.incrementAndGet();
                });
            }
        });
        Log.info("Loaded " + count.get() + "/14 balance top gangs");
    }

    public void saveBalanceTop() {
    }
}
