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
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class GangManager {

    private static final File DATA_FOLDER = new File(Helper.hostPlugin().getDataFolder() + File.separator + "data");
    private static final File GANGS_DATA_FOLDER = new File(DATA_FOLDER + File.separator + "gangs");

    private final Set<Gang> gangs = Sets.newHashSet();
    private final Set<String> takenNames = Sets.newHashSet();

    private GsonStorageHandler<Set<String>> balanceTopSorageHandler =
            new GsonStorageHandler<>("balancetop", ".json", GANGS_DATA_FOLDER, new TypeToken<Set<String>>() {
            });

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

    public Optional<Gang> getGang(final String name) {
        Preconditions.checkNotNull(name, "name may not be null");
        for (final Gang cached : this.gangs) {
            if(cached.getName().equals(name)) {
                return Optional.of(cached);
            }
        }
        // load maybe ?
        return loadGang(name);
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
        try {
           final String[] list = GANGS_DATA_FOLDER.list();
           if(list != null) {
               this.takenNames.addAll(Arrays.asList(list));
               Log.info("Loaded " + this.takenNames.size() + " taken name(s)");
           }
        } catch (SecurityException e) {
            Log.severe("Couldn't load taken name, because of security issues");
        }
    }

    public boolean isNameTaken(final String name) {
        Preconditions.checkNotNull(name, "name");
        for (final String takenName : this.takenNames) {
            if (takenName.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void saveBalanceTop() {
        final GangsRanking ranking = Services.load(GangsRanking.class);
        if(ranking.lastUpdated() == null) {
            Log.warn("No gang found in balance top to load");
            return;
        }
        if(Configuration.getBoolean("backup")) {
            balanceTopSorageHandler.saveAndBackup(ranking.cache().stream().map(Gang::getName).collect(Collectors.toSet()));
        } else {
            balanceTopSorageHandler.save(ranking.cache().stream().map(Gang::getName).collect(Collectors.toSet()));
        }
        Log.info("Saved " + ranking.cache().size() + "(/14) gang found on the balance top");
    }

    public void loadBalanceTop() {
        final Optional<Set<String>> loaded = balanceTopSorageHandler.load();
        // Set<String> -->  String(name)  ->  Optional<Gang>  -> if found --> register
        loaded.ifPresent(loadedGang -> loadedGang.forEach(name -> loadGang(name).ifPresent(this::registerGang)));
    }

}
