package me.aiglez.gangs.helpers;

import me.aiglez.gangs.managers.ConfigurationManager;
import me.lucko.helper.Services;
import me.lucko.helper.config.ConfigurationNode;

@SuppressWarnings("unused")
public enum Message {
    NOT_MEMBER("not-member", "&cYou're not member of any gang"),
    NO_PERMISSION("no-permission", "&cYou don't have permission to perform this action"),
    NOT_LEADER("not-leader", "&cYou must be the leader of your gang to perform this action"),
    GANG_NOT_FOUND("gang-not-found", "&cCouldn't find any gang matching that name"),
    INVALID_AMOUNT("invalid-amount", "&cAmount must be positive"),
    INSUFFICIENT_FUNDS("insufficient-funds", "&cYour gang doesn't have that amount"),

    BALANCE("balance", "&eYour gang has ${0}"),

    CHAT_ENABLED("chat.enabled", "&aYou have enabled gang chat"),
    CHAT_DISABLED("chat.disabled", "&cYou have disabled gang chat"),

    MINE_TOGGLEPLACE_DISABLED(
            "mine.toggleplace.disabled",
            "&cYou have disabled mine toggle place, you can no longer place blocks at mines"),
    MINE_TOGGLEPLACE_ENABLED("mine.toggleplace.enabled", "&aYou can now place blocks at mines"),

    CREATE_ALREADY_CREATING(
            "create.already-creating", "&ePlease wait, until the creation of your gang is done"),
    CREATE_ALREADY_MEMBER(
            "create.already-member",
            "&cYou are already member of a gang. Type /gang leave, to leave your current gang"),
    CREATE_INVALID_NAME(
            "create.invalid-name",
            "{0} is not a valid name, (it should not contain any special characters or spaces)"),
    CREATE_NAME_TAKEN("create.name-taken", "{0} is already taken"),
    CREATE_MINE_ERROR(
            "create.mine-creation-error", "&cAn error occurred while creating your gang's mine"),
    CREATE_IN_PROGRESS("create.in-progress", "&eYour gang will be created in a few seconds..."),
    CREATE_CREATED("create.created", "&aYou have successfully create a new gang"),

    DISBAND_DISBANDED("disband.disbanded", "&eYou have successfully disbanded your gang"),
    DISBAND_ANNOUNCEMENT("disband.announcement", "&e{0} disbanded your gang"),
    DISBAND_CONFIRM("disband.confirm", "&eClick here to confirm."),
    DISBAND_FORCEDISBANDED("disband.force-disbanded", "&eYou have (force) disbanded {0}"),

    MINE_RESET_ANNOUNCEMENT("mine.reset.announcement", "&cThe mine has been reset"),
    MINE_TELEPORT("mine.teleport", "&aYou have been teleported to your gang's mine"),

    MINE_ADMINUPGRADE_LEVELNOTFOUND(
            "mine.admin-level-not-found", "&cLevel {0} was not found in cache"),
    MINE_ADMINUPGRADE_UPGRADED("mine.admin-upgrade", "&aYou have upgraded {0}'s mine to level {1}"),
    MINE_ADMINUPGRADE_ANNOUNCEMENT(
            "mine.admin-upgrade-announcement", "&eYour mine has been upgraded by an administrator"),

    DEPOSIT_INSUFFICIENT_FUNDS("deposit.insufficient-funds", "&cYou don't have that amount"),
    DEPOSIT_DEPOSIT("deposit.success", "&eYou have deposed ${0} into your gang's bank"),

    LEAVE_LEADER("leave.leader", "&cYou are the leader, you must first disband the gang"),
    LEAVE_LEFT("leave.left", "&cYou left your gang"),
    LEAVE_ANNOUNCEMENT("leave.announcement", "&e{0} has left your gang"),

    INVITE_SELF("invite.self", "&cYou can't invite yourself"),
    INVITE_ALREADY("invite.already", "&c{0} is already invited to your gang"),
    INVITE_ALREADY_MEMBER("invite.already-member.your", "&c{0} is already member of your gang"),
    INVITE_ALREADY_MEMBER_OTHER("invite.already-member.other", "&c{0} is already member of {1}"),
    INVITE_INVITED_SENDER("invite.invited-sender", "&aYou have invited {0} to your gang"),
    INVITE_INVITED_TARGET("invite.invited-target", "&aYou have been invited to {0}"),

    DECLINE_DECLINED("decline.declined", "&aYou have declined the invitation to {0}"),
    DECLINE_NOT_INVITED("decline.not-invited", "&cYou are not invited to {0}"),

    CORE_ADMINUPGRADE_MAX_LEVEL("core.admin-upgrade-max-level", "&c{0} reached the max level"),
    CORE_ADMINUPGRADE_UPGRADED("core.admin-upgrade", "&aYou have upgraded {0}'s core to level {1}"),
    CORE_ADMINUPGRADE_ANNOUNCEMENT(
            "core.admin-upgrade-announcement", "&eYour core has been upgraded by an administrator"),

    MENU_CORE_NO_ACCESS(
            "menu.core.no-access", "&cYou don't have permission to upgrade your gang's core"),
    MENU_CORE_MAXLEVEL("menu.core.max-level", "&cYour core has reached the max level"),
    MENU_CORE_UPGRADED(
            "menu.core.upgraded", "&eYou have upgraded your gang's core from level {0} to {1} for ${2}"),
    MENU_CORE_ANNOUNCEMENT("menu.core.announcement", "&a{0} upgraded your gang's core to level {1}"),

    MENU_MINE_UPGRADED(
            "menu.mine.upgraded", "&6You have upgraded your gang's mine from level {0} to {1} for ${2}"),
    MENU_MINE_ANNOUNCEMENT("menu.mine.announcement", "&a{0} upgraded your gang's mine to level {1}"),

    MENU_EDIT_PERMISSION_REVOKED("menu.edit-permission.revoked", "&cYou have revoked the permission {0} to {1} rank"),
    MENU_EDIT_PERMISSION_GRANTED("menu.edit-permission.granted", "&aYou have granted the permission {0} to {1} rank"),


    HEY("hey", "&cHey");

    private static final ConfigurationNode NODE =
            Services.load(ConfigurationManager.class).getLanguageNode();

    private final String defaultValue, path;

    Message(final String path, final String defaultValue) {
        this.defaultValue = defaultValue;
        this.path = path;
    }

    public String getValue() {
        return NODE.getNode(toPath()).getString(this.defaultValue);
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public Object[] toPath() {
        return this.path.toLowerCase().split("\\.");
    }
}
