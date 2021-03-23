package me.aiglez.gangs.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.utils.Log;
import me.lucko.helper.Helper;
import me.lucko.helper.serialize.GsonStorageHandler;

import java.io.File;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class GangManager {

    private static final File DATA_FOLDER = new File(Helper.hostPlugin().getDataFolder() + File.separator + "data");
    private static final File GANGS_DATA_FOLDER = new File(DATA_FOLDER + File.separator + "gangs");

    private final Set<Gang> gangs = Sets.newHashSet();
    private final Set<String> takenNames = Sets.newHashSet();

    public GangManager() {
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
                    new GsonStorageHandler<>(gang.getName(), ".json", GANGS_DATA_FOLDER, new TypeToken<Gang>() {
                    });

            storageHandler.save(gang);
        }
        Log.info("Successfully saved " + this.gangs.size() + " gang(s)");
    }

    public Optional<Gang> loadGang(final String name) {
        Preconditions.checkNotNull(name, "name may not be null");
        final Optional<Gang> optional = new GsonStorageHandler<>(name, ".json", GANGS_DATA_FOLDER, new TypeToken<Gang>() {
        }).load();

        optional.ifPresent(this.gangs::add);

        return optional;
    }

    public void loadTakenNames() {
        this.takenNames.addAll(Arrays.asList(Objects.requireNonNull(GANGS_DATA_FOLDER.list())));
        Log.info("Loaded " + this.takenNames.size() + " taken name(s)");
    }

    public boolean isNameTaken(final String name) {
        Preconditions.checkNotNull(name, "name");
        for (final String takenName : this.takenNames) {
            if (takenName.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
