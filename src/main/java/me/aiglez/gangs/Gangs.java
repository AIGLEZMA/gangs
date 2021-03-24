package me.aiglez.gangs;

import me.aiglez.gangs.economy.Economy;
import me.aiglez.gangs.economy.TokenEnchantEconomy;
import me.aiglez.gangs.economy.VaultEconomy;
import me.aiglez.gangs.exceptions.DependencyNotFoundException;
import me.aiglez.gangs.helpers.Configuration;
import me.aiglez.gangs.listeners.MineListeners;
import me.aiglez.gangs.listeners.PlayerListeners;
import me.aiglez.gangs.managers.*;
import me.aiglez.gangs.utils.Log;
import me.lucko.helper.Helper;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;

@Plugin(
        name = "Gangs", version = "1.0",
        authors = {"AigleZ", "JohanLiebert"},
        depends = {@PluginDependency(value = "Vault", soft = true), @PluginDependency(value = "AutoSell", soft = true),
                @PluginDependency(value = "WorldEdit", soft = true), @PluginDependency(value = "TokenEnchant", soft = true),
                @PluginDependency(value = "PlaceholderAPI", soft = true)
        }
)
public final class Gangs extends ExtendedJavaPlugin {

    private boolean loaded;

    @Override
    protected void enable() {
        Log.info("Loading configurations...");
        final ConfigurationManager manager = new ConfigurationManager();
        provideService(ConfigurationManager.class, manager);
        manager.loadConfiguration();
        manager.loadLanguage();

        Log.info("Setting-up economy");
        if (!setupEconomy()) {
            Helper.plugins().disablePlugin(this);
            return;
        }

        Log.info("Loading users...");
        provideService(UserManager.class, new UserManager());
        getService(UserManager.class).loadUsers();

        Log.info("Loading balance top (& taken names)...");
        provideService(MineManager.class, new MineManager());
        getService(MineManager.class).loadLevels();
        provideService(GangManager.class, new GangManager());
        getService(GangManager.class).loadTakenNames();
        getService(GangManager.class).loadBalanceTop();

        provideService(GangsRanking.class, new GangsRanking());
        provideService(GangsMenu.class, new GangsMenu());

        Log.info("Registering listeners and commands...");
        registerListeners();
        provideService(CommandRegister.class, new CommandRegister());

        Log.info("Starting tasks...");
        launchTasks();

        if (Helper.plugins().isPluginEnabled("PlaceholderAPI")) {
            new GangsPlaceholders().register();
        } else {
            Log.warn("Couldn't register PAPI placeholders, please install PlaceholderAPI");
        }
        loaded = true;
    }

    @Override
    protected void disable() {
        if (!loaded) {
            Log.severe("An error occurred, disabling the plugin.");
            return;
        }
        Log.info("Saving users...");
        getService(UserManager.class).saveUsers();

        Log.info("Saving gangs...");
        getService(GangManager.class).saveBalanceTop();
        getService(GangManager.class).saveGangs();
        getService(GangManager.class).saveTakenNames();
    }

    private void registerListeners() {
        registerListener(new MineListeners());
        registerListener(new PlayerListeners());
    }

    private void launchTasks() {

    }

    private boolean setupEconomy() {
        final String choice = Configuration.getString("economy");
        if (choice.isEmpty()) {
            return false;
        }
        Economy economy = null;
        try {
            if (choice.equalsIgnoreCase("vault")) {
                economy = new VaultEconomy();
            } else if (choice.equalsIgnoreCase("tokenenchant")) {
                economy = new TokenEnchantEconomy();
            }
        } catch (DependencyNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (economy != null) provideService(Economy.class, economy);
        return economy != null;
    }
}
