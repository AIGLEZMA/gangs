package me.aiglez.gangs.exceptions;

public class LeaderNotFoundException extends RuntimeException {

    public LeaderNotFoundException(final String name) {
        super("Couldn't find " + name + "'s leader");
    }
}
