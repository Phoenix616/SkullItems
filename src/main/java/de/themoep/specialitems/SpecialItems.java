package de.themoep.specialitems;

import de.themoep.specialitems.listeners.ActionTriggerListener;
import de.themoep.specialitems.listeners.ItemCraftListener;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

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

public class SpecialItems extends JavaPlugin {

    private ItemManager itemManager;

    public void onEnable() {
        loadConfig();
        getCommand("specialitems").setExecutor(new SpecialItemCommand(this));
        getServer().getPluginManager().registerEvents(new ItemCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new ActionTriggerListener(this), this);
    }

    protected void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        itemManager = new ItemManager(this);
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public String getLang(String key, String... repl) {
        String msg =  getConfig().getString("lang." + key);
        if(msg == null) {
            return getTag() + ChatColor.RED + ": Unknown language key " + ChatColor.GOLD + key;
        }
        for(int i = 0; i + 1 < repl.length; i += 2) {
            msg = msg.replace("%" + repl[i] + "%", repl[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Get the tag to use in chat for this plugin including formatting
     */
    public String getTag() {
        return ChatColor.YELLOW + "[" + ChatColor.RED + getName() + ChatColor.YELLOW + "]" + ChatColor.RESET;
    }

    public boolean checkPerm(CommandSender sender, String permission) {
        return checkPerm(sender, permission, "general");
    }

    public boolean checkPerm(CommandSender sender, String permission, String type) {
        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage(getLang("nopermission." + type, "perm", permission));
        return false;
    }
}
