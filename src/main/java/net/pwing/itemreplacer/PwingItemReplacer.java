package net.pwing.itemreplacer;

import net.pwing.itemreplacer.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class that handles item replacement
 *
 * Created by Redned on 6/26/2019.
 */
public class PwingItemReplacer {

    /**
     * Handles replacing items from the players inventory from the given ItemStacks.
     *
     * @param player The player who's inventory you want to search for items
     * @param toReplace The item you want to replace
     * @param replacement The replacement item
     * @param exactName Checks if the name is exactly the same if true
     * @param exactLore Checks if the lore is exactly the same if true
     */
    public void replaceItems(Player player, ItemStack toReplace, ItemStack replacement, boolean exactName, boolean exactLore) {
        // Check if the item you want to replace is null
        if (toReplace == null)
            return;

        // Check if the replacement item is null
        if (replacement == null)
            return;

        // Loop through the player's inventory
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getContents()[i];
            // Check if the item is null
            if (item == null)
                continue;

            // Check if the item is replaceable in the first place
            if (!isReplaceable(item, toReplace, exactName, exactLore))
                continue;

            // Set the item in the inventory to the new replacement
            player.getInventory().setItem(i, replacement);
        }
    }

    /**
     * Handles replacing items from the players inventory from the given config and path.
     * Loops through the specified config path. Use with caution, this may cause lag if excessively used.
     *
     * @param path The path you want to look in for replaceable items
     * @param config The config you want to search in
     * @param player The player who's inventory you want to search for items
     */
    public void replaceItems(String path, FileConfiguration config, Player player) {
        // Loop through the configuration sections for the specified path
        for (String section : config.getConfigurationSection(path).getKeys(false)) {
            boolean exactName = config.getBoolean(path + "." + section + ".to-replace.exact-name", false);
            boolean exactLore = config.getBoolean(path + "." + section + ".to-replace.exact-lore", false);

            ItemStack replacement = ItemUtil.readItemFromConfig(path + "." + section + ".replacement", config);
            ItemStack toReplace = ItemUtil.readItemFromConfig(path + "." + section + ".to-replace", config);

            // Check if the replacement is null
            if (replacement == null)
                continue;

            // Check if the replacement item is null
            if (toReplace == null)
                continue;

            // Replace the items
            replaceItems(player, toReplace, replacement, exactName, exactLore);
        }
    }

    /**
     * Checks if an item is replaceable given the current config, path and options.
     *
     * @param item The item you want to check for if its replaceable
     * @param path The path you want to look in for replaceable items
     * @param config The config you want to search in
     * @param exactName Checks if the name is exactly the same if true
     * @param exactLore Checks if the lore is exactly the same if true
     * @param section The config section you want to check items for
     *
     * @return If the item is replaceable
     */
    public boolean isReplaceable(ItemStack item, String path, FileConfiguration config, boolean exactName, boolean exactLore, String section) {
        // Check if the item is null
        if (item == null)
            return false;

        ItemStack replacement = ItemUtil.readItemFromConfig(path + "." + section + ".replacement", config);
        ItemStack toReplace = ItemUtil.readItemFromConfig(path + "." + section + ".to-replace", config);

        // Check if the replacement is null
        if (replacement == null)
            return false;

        // Check if the replacement item is null
        if (toReplace == null)
            return false;

        // Check if it's replaceable now
        return isReplaceable(item, toReplace, exactName, exactLore);
    }

    /**
     * Checks if an item is replaceable given the current item and options.
     *
     * @param item The item you want to check for if its replaceable
     * @param toReplace The item you are searching for to be replaced
     * @param exactName Checks if the name is exactly the same if true
     * @param exactLore Checks if the lore is exactly the same if true
     *
     * @return If the item is replaceable
     */
    public boolean isReplaceable(ItemStack item, ItemStack toReplace, boolean exactName, boolean exactLore) {
        // Check if the item has item meta
        if (item.getItemMeta() != null && toReplace.getItemMeta() != null) {
            // Check if the name is exact
            if (exactName) {
                // Check if the item has a display name
                if (!item.getItemMeta().hasDisplayName())
                    return false;

                // Check if the item we're wanting to replace also has a display name
                if (!toReplace.getItemMeta().hasDisplayName()) {
                    return false;
                }

                // Check if the display name is the same
                if (!item.getItemMeta().getDisplayName().equals(toReplace.getItemMeta().getDisplayName()))
                    return false;

            } else {
                // Check if the item we're wanting to replace has a display name
                if (toReplace.getItemMeta().hasDisplayName()) {
                    // Check if the item has a display name
                    if (!item.getItemMeta().hasDisplayName())
                        return false;

                    // Check if the item display name contains what we want to replace
                    if (!toReplace.getItemMeta().getDisplayName().contains(item.getItemMeta().getDisplayName()))
                        return false;
                }
            }

            // Check if the lore is exact
            if (exactLore) {
                // Check if the item has a lore
                if (!item.getItemMeta().hasLore())
                    return false;

                // Check if the item we're wanting to replace also has a lore
                if (!toReplace.getItemMeta().hasLore())
                    return false;

                // Check if the lore is the same
                if (!item.getItemMeta().getLore().equals(toReplace.getItemMeta().getLore()))
                    return false;
            } else {
                // Check if the item we're wanting to replace has a lore
                if (toReplace.getItemMeta().hasLore()) {
                    // Check if the item has a lore
                    if (!item.getItemMeta().hasLore())
                        return false;

                    boolean contains = false;
                    // Check if the item lore contains what we want to replace
                    for (String lore : item.getItemMeta().getLore()) {
                        if (toReplace.getItemMeta().getLore().contains(lore))
                            contains = true;
                    }

                    // If it doesn't contain
                    if (!contains)
                        return false;
                }
            }
        }

        return true;
    }
}