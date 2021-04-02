package me.aiglez.gangs.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.aiglez.gangs.GangsRanking;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.helpers.Configuration;
import me.aiglez.gangs.utils.Log;
import me.lucko.helper.Helper;
import me.lucko.helper.Services;
import me.lucko.helper.serialize.GsonStorageHandler;

import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class GangManager {

    private static final File DATA_FOLDER =
            new File(Helper.hostPlugin().getDataFolder() + File.separator + "data");
    private static final File GANGS_DATA_FOLDER = new File(DATA_FOLDER + File.separator + "gangs");

    private final Set<Gang> gangs = Sets.newHashSet();

    public GangManager() {}

    public void registerGang(final Gang gang) {
        Preconditions.checkNotNull(gang, "gang may not be null");
        this.gangs.add(gang);
    }

    public void unregisterGang(final Gang gang) {
        Preconditions.checkNotNull(gang, "gang may not be null");
        final File file = new File(GANGS_DATA_FOLDER, gang.getName() + ".json");
        if(file.exists() && !file.delete()) {
            Log.warn("An error occurred while deleting " + gang.getName() + " data file");
            return;
        }

        Services.load(GangsRanking.class).handleDisband(gang);

        gang.getMembers().forEach(member -> member.setGang(null));
        this.gangs.remove(gang);

        Log.debug("Unregistered gang with name : " + gang.getName());
    }

    public Optional<Gang> getGang(final String name) {
        Preconditions.checkNotNull(name, "name may not be null");
        for (final Gang cached : this.gangs) {
            if (cached.getName().equals(name)) {
                return Optional.of(cached);
            }
        }
        return Optional.empty();
    }

    public Set<Gang> getGangs() {
        return Collections.unmodifiableSet(this.gangs);
    }

    public void saveGangs() {
        if (this.gangs.isEmpty()) {
            Log.warn("No gang found (in cache) to save");
            return;
        }

        for (final Gang gang : this.gangs) {
            final GsonStorageHandler<Gang> storageHandler =
                    new GsonStorageHandler<>(
                            gang.getName(), ".json", GANGS_DATA_FOLDER, new TypeToken<Gang>() {});

            storageHandler.saveAndBackup(gang);
        }
        Log.info("Successfully saved " + this.gangs.size() + " gang(s)");
    }

    public void loadGangs() {
        try {
            final File[] list = GANGS_DATA_FOLDER.listFiles();
            if (list == null || list.length == 0) return;
            for (final File file : list) {
                final GsonStorageHandler<Gang> storageHandler =
                        new GsonStorageHandler<>(
                                file.getName().replace(".json", ""), ".json", GANGS_DATA_FOLDER, new TypeToken<Gang>() {});

                storageHandler.load().ifPresent(gang -> {
                    Log.debug("Loaded gang: " + gang.getName());
                    registerGang(gang);

                    gang.getMembers().forEach(user -> user.setGang(gang));
                });
            }
        } catch (SecurityException e) {
            Log.severe("Couldn't load gangs, because of security issues");
        }
    }

    public boolean isNameTaken(final String name) {
        Preconditions.checkNotNull(name, "name");
        for (final Gang gang : this.gangs) {
            if (gang.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
