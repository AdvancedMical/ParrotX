package org.serverct.parrot.parrotx.utils;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.serverct.parrot.parrotx.PPlugin;

import java.util.List;

public class ItemUtil {

    public static ItemStack build(PPlugin plugin, @NonNull ConfigurationSection section) {
        ConfigurationSection itemSection = section.getConfigurationSection("ItemStack");
        if (itemSection != null) {
            try {
                ItemStack result = new ItemStack(EnumUtil.getMaterial(itemSection.getString("Material", "AIR").toUpperCase()));
                ItemMeta meta = result.getItemMeta();

                if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(result.getType());
                if (meta == null) return result;

                String display = itemSection.getString("Display");
                if (display != null) meta.setDisplayName(I18n.color(display));

                List<String> lore = itemSection.getStringList("Lore");
                if (!lore.isEmpty()) {
                    lore.replaceAll(I18n::color);
                    meta.setLore(lore);
                }

                if (itemSection.isConfigurationSection("Enchants")) {
                    ConfigurationSection enchant = itemSection.getConfigurationSection("Enchants");
                    if (enchant != null) {
                        for (String name : enchant.getKeys(false)) {
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                            if (enchantment == null) {
                                plugin.lang.logError(I18n.BUILD, "ItemStack", "目标附魔不存在: " + name);
                                continue;
                            }
                            meta.addEnchant(enchantment, enchant.getInt(name), true);
                        }
                    }
                }

                List<String> itemFlag = itemSection.getStringList("ItemFlags");
                if (!itemFlag.isEmpty()) {
                    for (String flagName : itemFlag) {
                        ItemFlag flag = EnumUtil.valueOf(ItemFlag.class, flagName.toUpperCase());
                        if (flag == null) {
                            plugin.lang.logError(I18n.BUILD, "ItemStack", "目标 ItemFlag 不存在: " + flagName);
                            continue;
                        }
                        meta.addItemFlags(flag);
                    }
                }

                result.setItemMeta(meta);
                return result;
            } catch (Throwable e) {
                plugin.lang.logError(I18n.BUILD, "ItemStack/" + section.getName(), e, null);
            }
        } else plugin.lang.logError(I18n.BUILD, "ItemStack/" + section.getName(), "未找到 ItemStack 数据节");
        return new ItemStack(Material.AIR);
    }

    public static String getName(PPlugin plugin, Material material) {
        String name = material.name();
        if (plugin.lang.hasKey("Material")) {
            String result = plugin.lang.getRaw("Material", "Material", name);
            if (!result.contains("错误")) name = ChatColor.stripColor(result);
        }
        return name;
    }

    public static ItemStack replace(ItemStack item, String placeholder, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (meta.hasDisplayName()) meta.setDisplayName(meta.getDisplayName().replace(placeholder, value));
        List<String> lore = meta.getLore();
        if (lore != null) {
            lore.replaceAll(s -> s.replace(placeholder, value));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
