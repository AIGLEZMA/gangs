package me.aiglez.gangs.exceptions;

import me.aiglez.gangs.users.User;

public class PlayerNotConnectedException extends RuntimeException {

    public PlayerNotConnectedException(final User user) {
        super("Couldn't get Player instance from user " + user.getOfflinePlayer().getName());
    }
}
