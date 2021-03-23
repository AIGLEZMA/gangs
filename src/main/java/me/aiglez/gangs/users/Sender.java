package me.aiglez.gangs.users;

import me.aiglez.gangs.helpers.Message;

import java.util.Set;

public interface Sender {

    void message(final String message, final Object... replacements);

    void message(final String message, final Set<User> exemptions, final Object... replacements);
    /*
     * This one will fetch the message from the config
     */
    void message(final Message message, final Object... replacements);

    void message(final Message message, final Set<User> exemptions, final Object... replacements);

    @Deprecated
    void messagec(final String string, Object... replacement);
}
