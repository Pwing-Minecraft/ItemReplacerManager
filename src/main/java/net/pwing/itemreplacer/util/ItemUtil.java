package net.pwing.itemreplacer.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemUtil {

    private static Map<Attribute, String> attributeStringMap = initMap();

    private static Map<Attribute, String> initMap() {
        Map<Attribute, String> attributeStringMap = new HashMap<Attribute, String>();
        attributeStringMap.put(Attribute.GENERIC_MAX_HEALTH, "generic.maxHealth");
        attributeStringMap.put(Attribute.GENERIC_FOLLOW_RANGE, "generic.followRange");
        attributeStringMap.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "generic.knockbackResistance");
        attributeStringMap.put(Attribute.GENERIC_MOVEMENT_SPEED, "generic.movementSpeed");
        attributeStringMap.put(Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage");
        attributeStringMap.put(Attribute.GENERIC_ARMOR, "generic.armor");
        attributeStringMap.put(Attribute.GENERIC_ARMOR_TOUGHNESS, "generic.armorToughness");
        attributeStringMap.put(Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed");
        attributeStringMap.put(Attribute.GENERIC_LUCK, "generic.luck");
        return attributeStringMap;
    }

    public static ItemStack readItemFromConfig(String configPath, FileConfiguration config) {
        ItemStack stack = new ItemStack(Material.STONE);

        if (!config.contains(configPath))
            return null;

        ItemMeta meta = stack.getItemMeta();
        for (String str : config.getConfigurationSection(configPath).getKeys(false)) {
            switch (str) {
                case "type":
                case "material":
                case "item":
                    stack = new ItemStack(Material.matchMaterial(config.getString(configPath + "." + str).toUpperCase()));
                    break;
                case "durability":
                case "data":
                    stack.setDurability((short) config.getInt(configPath + "." + str));
                    break;
                case "custom-model-data":
                case "model-data":
                    meta.setCustomModelData(config.getInt(configPath + "." + str));
                    break;
                case "amount":
                    stack.setAmount(config.getInt(configPath + "." + str));
                    break;
                case "name":
                case "display-name":
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(configPath + "." + str)));
                    break;
                case "enchants":
                case "enchantments":
                    for (String enchant : config.getStringList(configPath + "." + str)) {
                        String[] split = enchant.split(" ");
                        int level = Integer.parseInt(split[1]);

                        if (!isEnchantment(split[0]))
                            break;

                        Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(split[0].toLowerCase()));
                        meta.addEnchant(enchantment, level, true);
                    }
                    break;
                case "lore":
                    List<String> lore = config.getStringList(configPath + "." + str);
                    List<String> newLore = new ArrayList<String>();
                    lore.forEach(loreStr -> newLore.add(ChatColor.translateAlternateColorCodes('&', loreStr)));
                    meta.setLore(newLore);
                    break;
                case "unbreakable":
                    meta.setUnbreakable(config.getBoolean(configPath + "." + str));
                    break;
                case "owner":
                case "head-owner":
                    if (meta instanceof SkullMeta) {
                        SkullMeta skullMeta = (SkullMeta) meta;
                        skullMeta.setOwner(config.getString(configPath + "." + str));
                    }
                    break;
                case "color":
                case "colour":
                    String[] colorSplit = config.getString(configPath + "." + str).split(",");
                    Color color = null;

                    if (colorSplit.length == 3)
                        color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                    else
                        color = fromHex(config.getString(configPath + "." + str));

                    if (color != null) {
                        if (meta instanceof PotionMeta) {
                            PotionMeta potionMeta = (PotionMeta) meta;
                            potionMeta.setColor(color);
                        }
                        if (meta instanceof LeatherArmorMeta) {
                            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
                            armorMeta.setColor(color);
                        }
                    }
                    break;
                case "item-flags":
                    for (String flag : config.getStringList(configPath + "." + str)) {
                        if (!isItemFlag(flag))
                            continue;

                        meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                    break;
                case "effects":
                case "potion-effects":
                    for (String effect : config.getStringList(configPath + "." + str)) {
                        String[] effectSplit = effect.split(" ");
                        PotionEffectType effectType = PotionEffectType.getByName(effectSplit[0]);
                        if (effectType == null)
                            continue;

                        int duration = duration = Integer.parseInt(effectSplit[1]) * 20;
                        int amplifier =  amplifier = Integer.parseInt(effectSplit[2]) - 1;

                        if (meta instanceof PotionMeta) {
                            PotionMeta potionMeta = (PotionMeta) meta;
                            potionMeta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), true);
                        }
                    }
                    break;
                case "attributes":
                    for (String attributeStr : config.getStringList(configPath + "." + str)) {
                        String[] attributeSplit = attributeStr.split(" ");
                        if (!isAttribute(attributeSplit[0]))
                            continue;

                        EquipmentSlot slot = null;
                        if (isEquipmentSlot(attributeSplit[1]))
                            slot = EquipmentSlot.valueOf(attributeSplit[1].toUpperCase());

                        Attribute attribute = Attribute.valueOf(attributeSplit[0].toUpperCase());
                        meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attributeStringMap.get(attribute), Double.parseDouble(attributeSplit[2]), AttributeModifier.Operation.ADD_NUMBER, slot));
                    }
                    break;
                default:
                    break;
            }
        }

        stack.setItemMeta(meta);
        return stack;
    }

    private static Color fromHex(String hex) {
        java.awt.Color jColor = java.awt.Color.decode(hex);
        return Color.fromRGB(jColor.getRed(), jColor.getGreen(), jColor.getBlue());
    }

    public static boolean isEnchantment(String str) {
        return EnchantmentWrapper.getByKey(NamespacedKey.minecraft(str.toLowerCase())) != null;
    }

    public static boolean isItemFlag(String str) {
        try {
            ItemFlag.valueOf(str.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {/* do nothing */}

        return false;
    }

    public static boolean isAttribute(String str) {
        try {
            Attribute.valueOf(str);
            return true;
        } catch (IllegalArgumentException ex) {/* do nothing */}

        return false;
    }

    public static boolean isEquipmentSlot(String str) {
        try {
            EquipmentSlot.valueOf(str);
            return true;
        } catch (IllegalArgumentException ex) {/* do nothing */}

        return false;
    }
}
