package me.aiglez.gangs.exceptions;

import java.util.UUID;

public class OfflinePlayerNotFoundException extends RuntimeException {

    public OfflinePlayerNotFoundException(final UUID uniqueId) {
        super("Couldn't find an offlineplayer instance with unique-id (" + uniqueId + ")");
    }

}
