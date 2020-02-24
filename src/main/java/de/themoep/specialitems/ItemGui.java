package de.themoep.specialitems;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
public class ItemGui implements Listener {

    private final SpecialItems plugin;
    private Set<UUID> viewers = new HashSet<>();

    public ItemGui(SpecialItems plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void show(Player player) {
        player.closeInventory();
        viewers.add(player.getUniqueId());
        int invSize = plugin.getItemManager().getSpecialItems().size();
        invSize = invSize + 9 - (invSize % 9);
        Inventory inv = plugin.getServer().createInventory(null, invSize, plugin.getName());
        for (SpecialItem item : plugin.getItemManager().getSpecialItems()) {
            inv.addItem(item.getItem());
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (viewers.contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            if (event.isLeftClick() && event.getClickedInventory() == event.getView().getTopInventory()) {
                if (plugin.checkPerm(event.getWhoClicked(), "specialitems.gui.take", "gui.take")) {
                    if (event.getWhoClicked().getInventory().addItem(event.getCurrentItem()).size() == 0) {
                        if (event.getWhoClicked() instanceof Player) {
                            ((Player) event.getWhoClicked()).updateInventory();
                        }
                    } else {
                        event.getWhoClicked().sendMessage(plugin.getTag() + ChatColor.RED + " You need an empty slot in your inventory!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getOpenInventory() == event.getView().getTopInventory() && viewers.contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewers.contains(event.getPlayer().getUniqueId())) {
            viewers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        if (viewers.contains(event.getPlayer().getUniqueId())) {
            viewers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (viewers.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().closeInventory();
            viewers.remove(event.getPlayer().getUniqueId());
        }
    }

    public void destroy() {
        for (UUID id : viewers) {
            Player player = plugin.getServer().getPlayer(id);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        }
        viewers.clear();
    }
}
