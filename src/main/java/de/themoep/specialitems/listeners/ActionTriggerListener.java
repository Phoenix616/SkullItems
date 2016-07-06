package de.themoep.specialitems.listeners;

import de.themoep.specialitems.SpecialItem;
import de.themoep.specialitems.SpecialItems;
import de.themoep.specialitems.actions.ActionTrigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
public class ActionTriggerListener implements Listener {
    private final SpecialItems plugin;

    public ActionTriggerListener(SpecialItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }
        ActionTrigger trigger = ActionTrigger.UNSUPPORTED;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                trigger = ActionTrigger.RIGHT_CLICK_HAND;
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                trigger = ActionTrigger.LEFT_CLICK_HAND;
                break;
        }

        if (trigger == ActionTrigger.UNSUPPORTED) {
            return;
        }

        if (plugin.getItemManager().executeActions(event.getPlayer(), event.getItem(), trigger)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ActionTrigger trigger = ActionTrigger.UNSUPPORTED;
        switch (event.getClick()) {
            case LEFT:
                trigger = ActionTrigger.LEFT_CLICK_INV;
                break;
            case SHIFT_LEFT:
                trigger = ActionTrigger.SHIFT_LEFT_CLICK_INV;
                break;
            case RIGHT:
                trigger = ActionTrigger.RIGHT_CLICK_INV;
                break;
            case SHIFT_RIGHT:
                trigger = ActionTrigger.SHIFT_RIGHT_CLICK_INV;
                break;
            case MIDDLE:
                trigger = ActionTrigger.MIDDLE_CLICK_INV;
                break;
        }

        if (trigger == ActionTrigger.UNSUPPORTED) {
            return;
        }

        if (plugin.getItemManager().executeActions((Player) event.getWhoClicked(), event.getCurrentItem(), trigger)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (plugin.getItemManager().executeActions(
                event.getPlayer(), event.getItemDrop().getItemStack(), ActionTrigger.DROP
        )) {
            event.setCancelled(true);
        }
    }
}
