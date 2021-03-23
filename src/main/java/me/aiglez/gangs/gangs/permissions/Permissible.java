package me.aiglez.gangs.gangs.permissions;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aiglez.gangs.helpers.DefaultPermissions;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.gson.JsonBuilder;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

public class Permissible implements GsonSerializable {

    private final EnumMap<Rank, Map<Permission, Boolean>> permissions = Maps.newEnumMap(Rank.class);

    public Permissible() {
        this.permissions.put(Rank.RECRUIT, Maps.newEnumMap(DefaultPermissions.RECRUIT)); // we must use enummap cause it's immutable
        this.permissions.put(Rank.MEMBER, Maps.newEnumMap(DefaultPermissions.MEMBER));
        this.permissions.put(Rank.OFFICER, Maps.newEnumMap(DefaultPermissions.OFFICER));
        this.permissions.put(Rank.CO_LEADER, Maps.newEnumMap(DefaultPermissions.CO_LEADER));
        this.permissions.put(Rank.LEADER, Maps.newEnumMap(DefaultPermissions.LEADER));

    }

    public static Permissible newPermissible() {
        return new Permissible();
    }

    public void setPermission(final Rank rank, final Permission permission, final boolean flag) {
        Preconditions.checkNotNull(rank, "rank may not be null");
        Preconditions.checkNotNull(permission, "permission may not be null");

        if(rank == Rank.LEADER) return;
        permissions.get(rank).put(permission, flag);
    }

    public boolean hasPermission(final Rank rank, final Permission permission) {
        Preconditions.checkNotNull(rank, "rank may not be null");
        Preconditions.checkNotNull(permission, "permission may not be null");

        return this.permissions.get(rank).get(permission);
    }


    @Nonnull
    @Override
    public JsonElement serialize() {
        final JsonArray array = new JsonArray();
        for (final Entry<Rank, Map<Permission, Boolean>> entry : this.permissions.entrySet()) {
            final JsonObject object = new JsonObject();
            object.addProperty("rank", entry.getKey().getOrdinal());

            final JsonArray array2 = new JsonArray();
            for (final Permission permission : entry.getValue().keySet()) {
                array2.add(JsonBuilder.object()
                        .add("permission", permission.toString())
                        .add("state", entry.getValue().get(permission))
                        .build()
                );
            }

            object.add("permissions", array2);
            array.add(object);
        }
        return array;
    }

    public static Permissible deserialize(final JsonElement element) {
        final JsonArray array = element.getAsJsonArray();
        try {
            final Permissible permissible = Permissible.newPermissible();

            for (final JsonElement element1 : array) {
                final JsonObject object = element1.getAsJsonObject();
                final Rank rank = Rank.byOrdinal(object.get("rank").getAsInt());

                final JsonArray array2 = object.get("permissions").getAsJsonArray();
                for (final JsonElement jsonElement2 : array2) {
                    final JsonObject object1 = jsonElement2.getAsJsonObject();

                    final Permission permission = Permission.valueOf(object1.get("permission").getAsString().toUpperCase());
                    final boolean state = object1.get("state").getAsBoolean();

                    permissible.setPermission(rank, permission, state);
                }
            }

            return permissible;
        } catch (Exception e) {
            e.printStackTrace();;
            return newPermissible();
        }
    }

    /**
     * Enum for permissions
     */
    public enum Permission {

        INVITE("invite"),
        KICK("kick"),
        PROMOTE("promote"),
        UPGRADE_CORE("upgrade core"),
        UPGRADE_MINE("upgrade mine");

        private final String coolName;
        Permission(final String coolName) {
            this.coolName = coolName;
        }

        public String getCoolName() { return this.coolName; }
    }
}
