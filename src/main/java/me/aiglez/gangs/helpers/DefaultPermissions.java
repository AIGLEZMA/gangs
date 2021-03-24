package me.aiglez.gangs.helpers;

import me.aiglez.gangs.gangs.permissions.Permissible;

import java.util.HashMap;
import java.util.Map;

/** A helper for Permissible class */
public class DefaultPermissions {

  public static final Map<Permissible.Permission, Boolean> RECRUIT =
      new HashMap<Permissible.Permission, Boolean>() {
        {
          put(Permissible.Permission.INVITE, false);
          put(Permissible.Permission.KICK, false);
          put(Permissible.Permission.PROMOTE, false);
          put(Permissible.Permission.UPGRADE_CORE, false);
          put(Permissible.Permission.UPGRADE_MINE, false);
        }
      };

  public static final Map<Permissible.Permission, Boolean> MEMBER =
      new HashMap<Permissible.Permission, Boolean>() {
        {
          put(Permissible.Permission.INVITE, false);
          put(Permissible.Permission.KICK, false);
          put(Permissible.Permission.PROMOTE, false);
          put(Permissible.Permission.UPGRADE_CORE, false);
          put(Permissible.Permission.UPGRADE_MINE, false);
        }
      };

  public static final Map<Permissible.Permission, Boolean> OFFICER =
      new HashMap<Permissible.Permission, Boolean>() {
        {
          put(Permissible.Permission.INVITE, true);
          put(Permissible.Permission.KICK, true);
          put(Permissible.Permission.PROMOTE, false);
          put(Permissible.Permission.UPGRADE_CORE, true);
          put(Permissible.Permission.UPGRADE_MINE, true);
        }
      };

  public static final Map<Permissible.Permission, Boolean> CO_LEADER =
      new HashMap<Permissible.Permission, Boolean>() {
        {
          put(Permissible.Permission.INVITE, true);
          put(Permissible.Permission.KICK, true);
          put(Permissible.Permission.PROMOTE, true);
          put(Permissible.Permission.UPGRADE_CORE, true);
          put(Permissible.Permission.UPGRADE_MINE, true);
        }
      };

  public static final Map<Permissible.Permission, Boolean> LEADER =
      new HashMap<Permissible.Permission, Boolean>() {
        {
          put(Permissible.Permission.INVITE, true);
          put(Permissible.Permission.KICK, true);
          put(Permissible.Permission.PROMOTE, true);
          put(Permissible.Permission.UPGRADE_CORE, true);
          put(Permissible.Permission.UPGRADE_MINE, true);
        }
      };
}
