package me.aiglez.gangs.economy;

import com.google.common.base.Preconditions;
import me.aiglez.gangs.exceptions.DependencyNotFoundException;
import me.aiglez.gangs.users.User;
import me.lucko.helper.Helper;
import me.lucko.helper.Services;

import java.util.Optional;

public class VaultEconomy implements Economy {

    private final net.milkbowl.vault.economy.Economy vault;

    public VaultEconomy() {
        if(Helper.server().getPluginManager().isPluginEnabled("Vault")) {
            final Optional<net.milkbowl.vault.economy.Economy> optional = Services.get(net.milkbowl.vault.economy.Economy.class);
            this.vault = optional.orElseThrow(() -> new DependencyNotFoundException("Vault"));

        } else {
            throw new DependencyNotFoundException("Vault");
        }
    }

    @Override
    public void add(final User user, final double amount) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkArgument(amount >= 0, "amount may not be negative");
        Preconditions.checkArgument(this.vault.hasAccount(user.getOfflinePlayer()), "player does not have bank account");
        this.vault.depositPlayer(user.getOfflinePlayer(), amount);
    }

    @Override
    public void remove(final User user, final double amount) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkArgument(amount >= 0, "amount may not be negative");
        Preconditions.checkArgument(this.vault.hasAccount(user.getOfflinePlayer()), "player does not have bank account");

        this.vault.withdrawPlayer(user.getOfflinePlayer(), amount);
    }

    @Override
    public double balance(final User user) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkArgument(this.vault.hasAccount(user.getOfflinePlayer()), "player does not have bank account");

        return this.vault.getBalance(user.getOfflinePlayer());
    }
}
