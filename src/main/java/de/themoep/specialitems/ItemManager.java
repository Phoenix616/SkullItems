package de.themoep.specialitems;

import de.themoep.specialitems.actions.ActionSet;
import de.themoep.specialitems.actions.ActionTrigger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Copyright 2016 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * <p/>
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */
public class ItemManager {
    private final SpecialItems plugin;

    private Map<String, SpecialItem> itemMap;

    public ItemManager(SpecialItems plugin) {
        this.plugin = plugin;
        int itemsLoaded = loadItems();
        plugin.getLogger().log(Level.INFO, itemsLoaded + " special items loaded!");
    }

    /**
     * Load the items from the plugin's config.yml
     * @return
     */
    private int loadItems() {
        // reset item map
        itemMap = new HashMap<>();

        // reset recipes
        Iterator<Recipe> recipes = plugin.getServer().recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (isSpecialItem(recipe.getResult())) {
                recipes.remove();
            }
        }

        ConfigurationSection items = plugin.getConfig().getConfigurationSection("items");
        if (items == null || items.getKeys(false).size() == 0) {
            plugin.getLogger().log(Level.WARNING, "No special items configured?");
            return 0;
        }
        for (String id : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(id);
            SpecialItem item = new SpecialItem(
                    id,
                    itemSection.getString("displayname"),
                    itemSection.getItemStack("item"),
                    new ActionSet(itemSection.getConfigurationSection("actions")),
                    itemSection.getStringList("lore")
            );
            itemMap.put(item.getId(), item);

            // Register recipe
            ConfigurationSection recipeSection = itemSection.getConfigurationSection("recipe");
            if (recipeSection != null && item.getItem() != null) {
                Recipe recipe = null;
                try {
                    String recipeType = recipeSection.getString("type");
                    if ("shapeless".equalsIgnoreCase(recipeType)) {
                        recipe = new ShapelessRecipe(getItemStack(item));
                        for (String matStr : recipeSection.getConfigurationSection("materials").getKeys(false)) {
                            Material mat = Material.valueOf(matStr.toUpperCase());
                            ((ShapelessRecipe) recipe).addIngredient(
                                    recipeSection.getInt("materials." + matStr), mat
                            );
                        }
                    } else if ("shaped".equalsIgnoreCase(recipeType)) {
                        recipe = new ShapedRecipe(getItemStack(item));
                        List<String> shape = recipeSection.getStringList("shape");
                        ((ShapedRecipe) recipe).shape(shape.toArray(new String[shape.size()]));
                        for (String rKey : recipeSection.getConfigurationSection("keys").getKeys(false)) {
                            if (rKey.length() > 1) {
                                throw new IllegalArgumentException(
                                        "Shaped craft key " + rKey + " has to be a char and only be 1 long!"
                                );
                            }
                            Material mat = Material.valueOf(recipeSection.getString("keys." + rKey).toUpperCase());
                            ((ShapedRecipe) recipe).setIngredient(rKey.toCharArray()[0], mat);
                        }
                    } else if ("furnace".equalsIgnoreCase(recipeType)) {
                        recipe = new FurnaceRecipe(getItemStack(item), Material.valueOf(recipeSection.getString("input")));
                        ((FurnaceRecipe) recipe).setExperience((float) recipeSection.getDouble("exp"));
                    } else {
                        throw new IllegalArgumentException(recipeType + " is not a supported or valid recipe type!");
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not load recipe for " + id + "!", e);
                }
                if (recipe != null) {
                    plugin.getServer().addRecipe(recipe);
                }
            }

            // Register permissions
            if (plugin.getConfig().getBoolean("permissions.use")) {
                Permission usePerm = new Permission("specialitems.item." + id.toLowerCase() + ".use");
                try {
                    plugin.getServer().getPluginManager().addPermission(usePerm);
                } catch (IllegalArgumentException e) {
                    // Permission is already defined
                    usePerm = plugin.getServer().getPluginManager().getPermission(usePerm.getName());
                }
                if (plugin.getConfig().getBoolean("permissions.usepertrigger")) {
                    for (ActionTrigger trigger : ActionTrigger.values()) {
                        Permission triggerPerm = new Permission("specialitems.item." + id.toLowerCase() + ".use." + trigger.toString().toLowerCase());
                        triggerPerm.addParent(usePerm, true);
                        try {
                            plugin.getServer().getPluginManager().addPermission(usePerm);
                        } catch (IllegalArgumentException e) {
                            // Permission is already defined
                        }
                    }
                }
            }
            if (plugin.getConfig().getBoolean("permissions.craft")) {
                Permission craftPerm = new Permission("specialitems.item." + id.toLowerCase() + ".craft");
                try {
                    plugin.getServer().getPluginManager().addPermission(craftPerm);
                } catch (IllegalArgumentException e) {
                    // Permission is already defined
                }
            }
            if (plugin.getConfig().getBoolean("permissions.drop")) {
                Permission dropPerm = new Permission("specialitems.item." + id.toLowerCase() + ".drop");
                try {
                    plugin.getServer().getPluginManager().addPermission(dropPerm);
                } catch (IllegalArgumentException e) {
                    // Permission is already defined
                }
            }
        }
        return itemMap.size();
    }

    public Collection<SpecialItem> getSpecialItems() {
        return itemMap.values();
    }

    /**
     * Get a skull item by it's name.
     * @param id The id of the special item
     * @return The saved special item. Null if none was found. (Use the manipulation methods to change configs!)
     */
    public SpecialItem getSpecialItem(String id) {
        return itemMap.get(id.toLowerCase());
    }

    /**
     * Get a the SpecialItem object from an ItemStack
     * @param item The ItemStack to get the SpecialItem from
     * @return The SpecialItem or <tt>null</tt> if it isn't one or none was found with the encoded item name
     */
    public SpecialItem getSpecialItem(ItemStack item) throws IllegalArgumentException {
        if (!isSpecialItem(item)) {
            return null;
        }

        String hidden = getHiddenString(item);
        if (hidden == null) {
            throw new IllegalArgumentException("Item should be a special item but no hidden id string was found?");
        }

        return getSpecialItem(hidden);
    }

    public ItemStack getItemStack(String name) {
        SpecialItem specialItem = getSpecialItem(name);
        if (specialItem == null) {
            return null;
        }
        return getItemStack(specialItem);
    }

    public ItemStack getItemStack(SpecialItem specialItem) {
        ItemStack item = specialItem.getItem();
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', specialItem.getName()));

        List<String> lore = new ArrayList<>();
        for (String line : specialItem.getLore()) {
            lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', line));
        }
        lore.add(hideString(specialItem.getId(), ChatColor.BLUE + "" + ChatColor.ITALIC + plugin.getName()));
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Hide a string inside another string with chat color characters
     * @param hidden The string to hide
     * @param string The string to hide in
     * @return The string with the hidden string appended
     */
    private String hideString(String hidden, String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.length() - i > 2)
                break;
            if (string.charAt(i) == ChatColor.COLOR_CHAR)
                string = string.substring(0, i);
        }
        // Add hidden string
        for (int i = 0; i < hidden.length(); i++) {
            string += ChatColor.COLOR_CHAR + hidden.substring(i, i + 1);
        }
        return string;
    }

    /**
     * Returns a hidden string in the itemstack which is hidden using the last lore line
     */
    private String getHiddenString(ItemStack item) {
        // Only the color chars at the end of the string is it
        StringBuilder builder = new StringBuilder();
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return null;
        char[] chars = item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == org.bukkit.ChatColor.COLOR_CHAR)
                continue;
            if (i + 1 < chars.length) {
                if (chars[i + 1] == org.bukkit.ChatColor.COLOR_CHAR && i > 1 && chars[i - 1] == org.bukkit.ChatColor.COLOR_CHAR)
                    builder.append(c);
                else if (builder.length() > 0)
                    builder = new StringBuilder();
            } else if (i > 0 && chars[i - 1] == org.bukkit.ChatColor.COLOR_CHAR)
                builder.append(c);
        }
        if (builder.length() == 0)
            return null;
        return builder.toString();
    }

    public boolean isSpecialItem(ItemStack item) {
        return item.hasItemMeta()
                && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).contains(plugin.getName());
    }

    /**
     * Execute all actions for a specific trigger on/with a player
     * @param player The player who triggered this skull item
     * @param itemStack the item stack that may trigger stuff
     * @param trigger The trigger
     * @return Whether or not the event that triggered this should be cancelled,
     * <tt>true</tt> if there are actions and the do not contain ItemActionType.DONT_CANCEL,
     * <tt>false</tt> if there are no actions
     */
    public boolean executeActions(Player player, ItemStack itemStack, ActionTrigger trigger) {
        try {
            SpecialItem item = plugin.getItemManager().getSpecialItem(itemStack);
            if (item == null) {
                return false;
            }

            return plugin.getItemManager().executeActions(player, item, trigger);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, player.getName() + " has an invalid item! " + e.getMessage());
        }
        return false;
    }

    /**
     * Execute all actions for a specific trigger on/with a player
     * @param player The player who triggered this skull item
     * @param item the SpecialItem
     * @param trigger The trigger
     * @return Whether or not the event that triggered this should be cancelled,
     * <tt>true</tt> if there are actions and the do not contain ItemActionType.DONT_CANCEL,
     * <tt>false</tt> if there are no actions
     */
    public boolean executeActions(Player player, SpecialItem item, ActionTrigger trigger) {
        boolean hasPermission = true;
        if (plugin.getConfig().getBoolean("permissions.usepertrigger")) {
            hasPermission = player.hasPermission("specialitems.item." + item.getId() + ".use." + trigger);
        } else if (plugin.getConfig().getBoolean("permissions.use")) {
            hasPermission = player.hasPermission("specialitems.item." + item.getId() + ".use");
        }
        if (hasPermission) {
            return item.getActionSet().execute(trigger, player);
        } else {
            player.sendMessage(plugin.getLang("nopermission"));
        }
        return true;
    }

    public void setValue(String id, String key, Object object) {
        SpecialItem item = itemMap.get(id.toLowerCase());
        if (item != null) {
            plugin.getConfig().set("items." + item.getId() + "." + key.toLowerCase(), object);
            ConfigurationSection itemSection = plugin.getConfig().getConfigurationSection("items." + item.getId());
            item = new SpecialItem(
                    item.getId(),
                    itemSection.getString("displayname"),
                    itemSection.getItemStack("item"),
                    new ActionSet(itemSection.getConfigurationSection("actions")),
                    itemSection.getStringList("lore")
            );
            itemMap.put(item.getId(), item);
            plugin.saveConfig();
        }
    }
}
