package net.pwing.itemreplacer;

import net.pwing.itemreplacer.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Redned on 6/26/2019.
 */
public class PwingItemReplacer {

    public void replaceItems(Player player, ItemStack toReplace, ItemStack replacement, boolean exactName, boolean exactLore) {
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getContents()[i];
            if (!isReplaceable(item, toReplace, exactName, exactLore))
                continue;

            if (replacement == null)
                continue;

            player.getInventory().setItem(i, replacement);
        }
    }

    public void replaceItems(String path, FileConfiguration config, Player player) {
        for (String section : config.getConfigurationSection("items").getKeys(false)) {
            boolean exactName = config.getBoolean(path + "." + section + ".to-replace.exact-name", false);
            boolean exactLore = config.getBoolean(path + "." + section + ".to-replace.exact-lore", false);

            ItemStack replacement = ItemUtil.readItemFromConfig(path + "." + section + ".replacement", config);
            if (replacement == null)
                continue;

            ItemStack toReplace = ItemUtil.readItemFromConfig(path + "." + section + ".to-replace", config);
            if (toReplace == null)
                continue;

            replaceItems(player, toReplace, replacement, exactName, exactLore);
        }
    }

    public boolean isReplaceable(ItemStack item, String path, FileConfiguration config, boolean exactName, boolean exactLore, String section) {
        if (item == null)
            return false;

        ItemStack toReplace = ItemUtil.readItemFromConfig(path + "." + section + ".to-replace", config);
        if (toReplace == null)
            return false;

        ItemStack replacement = ItemUtil.readItemFromConfig(path + "." + section + ".replacement", config);
        if (replacement == null)
            return false;

        return isReplaceable(item, toReplace, exactName, exactLore);
    }

    public boolean isReplaceable(ItemStack item, ItemStack toReplace, boolean exactName, boolean exactLore) {
        if (item.getItemMeta() != null && toReplace.getItemMeta() != null) {
            if (exactName) {
                if (!item.getItemMeta().hasDisplayName())
                    return false;

                if (!toReplace.getItemMeta().hasDisplayName()) {
                    return false;
                }

                if (!item.getItemMeta().getDisplayName().equals(toReplace.getItemMeta().getDisplayName()))
                    return false;

            } else {
                if (toReplace.getItemMeta().hasDisplayName()) {
                    if (!item.getItemMeta().hasDisplayName())
                        return false;

                    if (!item.getItemMeta().getDisplayName().contains(toReplace.getItemMeta().getDisplayName()))
                        return false;
                }
            }

            if (exactLore) {
                if (item.getItemMeta().hasLore())
                    return false;

                if (!toReplace.getItemMeta().hasLore())
                    return false;

                if (!item.getItemMeta().getLore().equals(toReplace.getItemMeta().getLore()))
                    return false;
            } else {
                if (toReplace.getItemMeta().hasLore()) {
                    if (!item.getItemMeta().hasLore())
                        return false;

                    boolean contains = false;
                    for (String lore : toReplace.getItemMeta().getLore()) {
                        if (item.getItemMeta().getLore().contains(lore))
                            contains = true;
                    }
                    if (!contains)
                        return false;
                }
            }
        }

        return true;
    }
}