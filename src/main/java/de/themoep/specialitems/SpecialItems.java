package de.themoep.specialitems;

/*
 * SpecialItems
 * Copyright (c) 2020 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.themoep.specialitems.listeners.ActionTriggerListener;
import de.themoep.specialitems.listeners.ItemCraftListener;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SpecialItems extends JavaPlugin {
    public static NamespacedKey KEY;

    private ItemManager itemManager;
    private ItemGui gui = null;

    public void onEnable() {
        KEY = new NamespacedKey(this, "item");
        loadConfig();
        gui = new ItemGui(this);
        getCommand("specialitems").setExecutor(new SpecialItemCommand(this));
        getServer().getPluginManager().registerEvents(new ItemCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new ActionTriggerListener(this), this);
    }

    protected void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        itemManager = new ItemManager(this);
        if (gui != null) {
            gui.destroy();
        }
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

    public ItemGui getGui() {
        return gui;
    }
}
