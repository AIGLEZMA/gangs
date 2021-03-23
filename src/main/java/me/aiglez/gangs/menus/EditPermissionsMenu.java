package me.aiglez.gangs.menus;

import me.aiglez.gangs.gangs.Gang;
import me.aiglez.gangs.gangs.permissions.Permissible;
import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.users.User;
import me.aiglez.gangs.utils.Placeholders;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;

public class EditPermissionsMenu extends Gui {

    private static final ConfigurationNode CONFIG = null;
    private static final MenuScheme BUTTONS = new MenuScheme()
            .mask("000000000")
            .mask("001111100")
            .mask("000010000");

    private final User user;
    private final Rank rank;

    public EditPermissionsMenu(final User user, final Rank rank, final String title) {
        super(user.getPlayer(), 3, title);
        this.user = user;
        this.rank = rank;
        redraw();
    }


    public static void make(final User user, final Rank rank) {
        new EditPermissionsMenu(user, rank, CONFIG.getNode("title").getString("")).open();
    }


    @Override
    public void redraw() {
        if (!user.hasGang()) {
            return;
        }
        final Gang gang = user.getGang();

        BUTTONS.newPopulator(this)
                .acceptIfSpace(
                        ItemStackBuilder.of(Material.STAINED_GLASS_PANE).data(7).buildConsumer(e -> e.setCancelled(true))
                );

        // back
        setItem(22, ItemStackBuilder.of(Material.PAPER)
                .name(CONFIG.getNode("items", "back", "name").getString(""))
                .lore(CONFIG.getNode("items", "back", "lore").getList(String::valueOf))
                .buildConsumer(e -> {
                    e.setCancelled(true);
                    close();
                    PermissionsMenu.make(this.user);
                }));

        int currentSlot = 11;
        for (final Permissible.Permission permission : Permissible.Permission.values()) {
            boolean state = gang.getPermissible().hasPermission(rank, permission);
            String path = state ? "granted" : "denied";

            setItem(currentSlot, ItemStackBuilder.of(state ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                    .name(Placeholders.replaceIn(CONFIG.getNode("items", path, "name").getString(""), permission.getCoolName()))
                    .lore(CONFIG.getNode("items", path, "lore").getList(String::valueOf))
                    .buildConsumer(e -> {
                        gang.getPermissible().setPermission(rank, permission, !state);
                        if (!state) {
                            user.messagec("permission.granted", permission.getCoolName(), rank.getCoolName());
                        } else {
                            user.messagec("permission.denied", permission.getCoolName(), rank.getCoolName());
                        }
                        redraw();
                    })
            );

            currentSlot++;
        }
    }
}
