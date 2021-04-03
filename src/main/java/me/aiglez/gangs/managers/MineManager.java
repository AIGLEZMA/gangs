package me.aiglez.gangs.managers;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.object.visitor.FastIterator;
import com.boydti.fawe.util.EditSessionBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import me.aiglez.gangs.exceptions.DependencyNotFoundException;
import me.aiglez.gangs.gangs.Mine;
import me.aiglez.gangs.gangs.MineLevel;
import me.aiglez.gangs.helpers.Configuration;
import me.aiglez.gangs.users.User;
import me.aiglez.gangs.utils.Log;
import me.aiglez.gangs.utils.Pair;
import me.lucko.helper.Helper;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.function.chain.Chain;
import me.lucko.helper.item.ItemStackBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("UnstableApiUsage")
public class MineManager {

    private static final int ZERO_TO_SPAWN = 65;
    private static final TypeToken<Map<String, Double>> TYPE = new TypeToken<Map<String, Double>>() {};

    private final World mineWorld;
    private final com.sk89q.worldedit.world.World worldEditWorld;
    private final Schematic schematic;

    private final Map<Integer, MineLevel> levels = new HashMap<>();

    public MineManager() {
        if (!Helper.plugins().isPluginEnabled("WorldEdit")) {
            throw new DependencyNotFoundException("WorldEdit");
        }

        final String worldName = Configuration.getString("mine-settings", "world-name");
        this.mineWorld =
                Helper.world(worldName)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Couldn't find the world where the mines will be pasted"));
        this.worldEditWorld = FaweAPI.getWorld(worldName);

        final File schematic = new File(Helper.hostPlugin().getDataFolder(), "mine.schematic");
        if (!schematic.exists()) {
            Log.severe("Couldn't find the file with name mine.schematic in the plugin's folder");
        }
        try {
            this.schematic = FaweAPI.load(schematic);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't read schematic file");
        }
    }

    public void loadLevels() {
        final ConfigurationNode config = Configuration.getNode("mine-settings", "levels");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getChildrenMap().entrySet()) {
            if (!(entry.getKey() instanceof Integer)) {
                Log.warn("Level must be a number not " + entry.getKey());
                continue;
            }
            final ConfigurationNode node = entry.getValue();

            final int ordinal = (Integer) entry.getKey();
            final List<String> lore = node.getNode("lore").getList(String::valueOf);
            final long upgradeCost = node.getNode("upgrade-cost").getLong();

            try {
                final Map<ItemStack, Double> blocks = Chain.start(node.getNode("blocks").getValue(TYPE))
                        .mapNullSafe(map -> {
                            final Map<ItemStack, Double> m = Maps.newHashMap();
                            for (final Map.Entry<String, Double> e : map.entrySet()) {
                                final String[] split = e.getKey().split(":");

                                final Material material = Material.valueOf(split[0]);
                                if (split.length == 1) {
                                    m.put(ItemStackBuilder.of(material).showAttributes().build(), e.getValue());
                                } else {
                                    final int data = NumberUtils.toInt(split[1], 1);
                                    m.put(ItemStackBuilder.of(material).showAttributes().data(data).build(), e.getValue());
                                }
                            }
                            return m;
                        }, new HashMap<ItemStack, Double>())
                        .end().orElse(Maps.newHashMap());

                this.levels.put(ordinal, new MineLevel(ordinal, upgradeCost, blocks, lore));
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Mine> createNewMine(final User user) {
        Preconditions.checkNotNull(user, "user may not be null");
        final Location location = buildRandomLocation();

        final Pair<Region, EditSession> at = pasteSchematicAt(location);

        if (at == null) {
            return Optional.empty();
        }

        Vector min = null, max = null;

        final FastIterator vectors = new FastIterator(at.getFirst(), at.getSecond());
        for (Vector vector : vectors) {
            final Block blockAt =
                    this.mineWorld.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            if (blockAt.getType() == Material.SPONGE) {
                if (min == null) {
                    min = new Vector(vector.getX(), vector.getY(), vector.getZ());
                    continue;
                }
                if (max == null) {
                    max = new Vector(vector.getX(), vector.getY(), vector.getZ());
                    continue;
                }
            }
        }

        if (min == null || max == null) {
            return Optional.empty();
        }

        final Mine mine =
                new Mine(
                        user.getGang(), this.levels.get(1), 0, location.clone().add(0.5D, ZERO_TO_SPAWN, 0.5D));
        final Region minable = new CuboidRegion(this.worldEditWorld, min, max);

        mine.setMinableRegion(minable);
        mine.setRegion(at.getFirst());

        return Optional.of(mine);
    }

    private Pair<Region, EditSession> pasteSchematicAt(final Location location) {
        Preconditions.checkNotNull(location, "location may not be null");
        final Clipboard clipboard = this.schematic.getClipboard();
        if (clipboard == null) {
            Log.severe("It seems like the schematic doesn't hold any clipboard");
            return null;
        }
        final Location clone = location.clone();
        clone.setY(clipboard.getOrigin().getBlockY());
        final Vector vector = BukkitUtil.toVector(clone);

        final EditSession editSession =
                new EditSessionBuilder(this.worldEditWorld)
                        .allowedRegionsEverywhere()
                        .limitUnlimited()
                        .fastmode(true)
                        .build();
        schematic.paste((com.sk89q.worldedit.world.World) editSession, vector, false, true, null);
        final Region region = clipboard.getRegion();
        region.setWorld(this.worldEditWorld);

        try {
            region.shift(vector.subtract(clipboard.getOrigin()));
        } catch (RegionOperationException e) {
            e.printStackTrace();
        }

        return Pair.of(region, editSession);
    }

    public MineLevel getLevel(final int ordinal) {
        return this.levels.getOrDefault(ordinal, null);
    }

    public Collection<MineLevel> getLevels() {
        return Collections.unmodifiableCollection(levels.values());
    }

    public Location buildRandomLocation() {
        final int maxX = Configuration.getInteger("mine-settings", "random", "max-x");
        final int minX = Configuration.getInteger("mine-settings", "random", "min-x");

        final int maxZ = Configuration.getInteger("mine-settings", "random", "max-z");
        final int minZ = Configuration.getInteger("mine-settings", "random", "min-z");

        final int x = (int) ThreadLocalRandom.current().nextLong((maxX - (minX + 1)) + minX);
        final int z = (int) (ThreadLocalRandom.current().nextLong((maxZ - (minZ) + 1)) + (minZ));

        final Location location = new Location(this.mineWorld, x, 0, z);
        location.setYaw(location.getYaw() + 180f);
        return location;
    }

    public World getMineWorld() {
        return this.mineWorld;
    }

    public com.sk89q.worldedit.world.World getWorldEditWorld() {
        return this.worldEditWorld;
    }
}
