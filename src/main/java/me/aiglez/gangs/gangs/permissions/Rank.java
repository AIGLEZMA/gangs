package me.aiglez.gangs.gangs.permissions;

import com.google.common.base.Preconditions;

public enum Rank {

    LEADER("leader", 4),
    CO_LEADER("co-leader", 3),
    OFFICER("officer", 2),
    MEMBER("member", 1),
    RECRUIT("recruit", 0);

    private final String coolName;
    private final int ordinal;

    Rank(final String coolName, final int ordinal) {
        this.coolName = coolName;
        this.ordinal = ordinal;
    }

    public String getCoolName() {
        return this.coolName;
    }

    public int getOrdinal() { return this.ordinal; }

    /*
     * Return the superior between two ranks (an7tajoha mn ba3ed f promotes)
     */
    public static Rank superior(final Rank a, final Rank b) {
        Preconditions.checkNotNull(a, "rank a may not be null");
        Preconditions.checkNotNull(b, "rank b may not be null");
        return (a.getOrdinal() > b.getOrdinal()) ? a : b;
    }

    public static Rank byOrdinal(final int ordinal) {
        switch (ordinal) {
            case 4: return LEADER;
            case 3: return CO_LEADER;
            case 2: return OFFICER;
            case 1: return MEMBER;
            case 0:
            default:
                return RECRUIT;
        }
    }

}
