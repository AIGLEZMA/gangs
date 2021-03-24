package me.aiglez.gangs.economy;

import com.google.common.base.Preconditions;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import me.aiglez.gangs.exceptions.DependencyNotFoundException;
import me.aiglez.gangs.users.User;
import me.lucko.helper.Helper;

public class TokenEnchantEconomy implements Economy {

    private final TokenEnchantAPI tokenEnchant;

    public TokenEnchantEconomy() {
        if (Helper.server().getPluginManager().isPluginEnabled("TokenEnchant")) {
            this.tokenEnchant = TokenEnchantAPI.getInstance();
            if (tokenEnchant == null) throw new DependencyNotFoundException("TokenEnchant");
        } else {
            throw new DependencyNotFoundException("TokenEnchant");
        }
    }

    @Override
    public void add(final User user, final double amount) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkArgument(amount >= 0, "amount may not be negative");
        this.tokenEnchant.addTokens(user.getOfflinePlayer(), amount);
    }

    @Override
    public void remove(final User user, final double amount) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkArgument(amount >= 0, "amount may not be negative");
        this.tokenEnchant.removeTokens(user.getOfflinePlayer(), amount);
    }

    @Override
    public double balance(final User user) {
        Preconditions.checkNotNull(user, "user may not be null");
        return this.tokenEnchant.getTokens(user.getOfflinePlayer());
    }
}
