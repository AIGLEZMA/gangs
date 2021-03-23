package me.aiglez.gangs.gangs;

import me.aiglez.gangs.users.User;
import me.lucko.helper.time.Time;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Invite {

    private static final long EXPIRES_AFTER = 5; // in minutes

    private final User invited;
    private final Gang to;

    private Instant instant;

    public Invite(final User invited, final Gang to) {
        this.invited = invited;
        this.to = to;
        this.instant = Time.now();
    }

    public User getInvited() { return this.invited; }

    public boolean isExpired() {
        return Time.diffToNow(instant).toMillis() >= TimeUnit.MINUTES.toMillis(EXPIRES_AFTER);
    }

    public Instant getInstant() {
        return this.instant;
    }
}
