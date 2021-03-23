package me.aiglez.gangs.menus;

import me.aiglez.gangs.gangs.permissions.Rank;
import me.aiglez.gangs.users.User;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;

public class PermissionsMenu extends Gui {

    private static final ConfigurationNode CONFIG = null;
    private static final MenuScheme PANES = new MenuScheme()
            .mask("111111111")
            .mask("101010101")
            .mask("111111111");

    private final User user;

    public PermissionsMenu(final User user, final String title) {
        super(user.getPlayer(), 3, title);
        this.user = user;
        redraw();
    }


    public static void make(final User user) {
        new PermissionsMenu(user, CONFIG.getNode("title").getString("")).open();
    }


    @Override
    public void redraw() {
        final MenuPopulator populator = PANES.newPopulator(this);
        for (int i = 0; i < 23; i++) {
            populator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).data(7).buildConsumer(e -> e.setCancelled(true)));
        }

        setItem(10, ItemStackBuilder.of(Material.IRON_BLOCK)
                .name(CONFIG.getNode("items", "recruit", "name").getString(""))
                .lore(CONFIG.getNode("items", "recruit", "lore").getList(String::valueOf))
                .buildConsumer(e -> {
                    e.setCancelled(true);
                    close();
                    EditPermissionsMenu.make(this.user, Rank.RECRUIT);
                }));

        setItem(12, ItemStackBuilder.of(Material.GOLD_BLOCK)
                .name(CONFIG.getNode("items", "member", "name").getString(""))
                .lore(CONFIG.getNode("items", "member", "lore").getList(String::valueOf))
                .buildConsumer(e -> {
                    e.setCancelled(true);
                    close();
                    EditPermissionsMenu.make(this.user, Rank.MEMBER);
                }));

        setItem(14, ItemStackBuilder.of(Material.DIAMOND_BLOCK)
                .name(CONFIG.getNode("items", "officer", "name").getString(""))
                .lore(CONFIG.getNode("items", "officer", "lore").getList(String::valueOf))
                .buildConsumer(e -> {
                    e.setCancelled(true);
                    close();
                    EditPermissionsMenu.make(this.user, Rank.OFFICER);
                }));

        setItem(16, ItemStackBuilder.of(Material.EMERALD_BLOCK)
                .name(CONFIG.getNode("items", "co-leader", "name").getString(""))
                .lore(CONFIG.getNode("items", "co-leader", "lore").getList(String::valueOf))
                .buildConsumer(e -> {
                    e.setCancelled(true);
                    close();
                    EditPermissionsMenu.make(this.user, Rank.CO_LEADER);
                }));
    }
}
