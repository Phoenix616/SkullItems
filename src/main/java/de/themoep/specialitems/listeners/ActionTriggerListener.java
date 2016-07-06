package de.themoep.specialitems.listeners;

import de.themoep.specialitems.SpecialItems;
import de.themoep.specialitems.actions.TriggerType;
import de.themoep.specialitems.actions.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

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
        TriggerType triggerType = TriggerType.UNSUPPORTED;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                triggerType = TriggerType.RIGHT_CLICK_HAND;
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                triggerType = TriggerType.LEFT_CLICK_HAND;
                break;
        }

        if (triggerType == TriggerType.UNSUPPORTED) {
            return;
        }

        Trigger trigger = new Trigger(event, event.getPlayer(), event.getItem(), triggerType);
        plugin.getItemManager().executeActions(trigger);
        if (trigger.shouldCancel()) {
            event.setCancelled(true);
        }
        if (trigger.shouldRemoveItem() && event.getHand() != null) {
            ItemStack item = null;
            if (event.getItem().getAmount() > 1) {
                item = event.getItem();
                item.setAmount(event.getItem().getAmount() - 1);
            }
            switch (event.getHand()) {
                case HAND:
                    event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), item);
                    break;
                case OFF_HAND:
                    event.getPlayer().getInventory().setItemInOffHand(item);
                    break;
                case HEAD:
                    event.getPlayer().getInventory().setHelmet(item);
                    break;
                case CHEST:
                    event.getPlayer().getInventory().setChestplate(item);
                    break;
                case LEGS:
                    event.getPlayer().getInventory().setLeggings(item);
                    break;
                case FEET:
                    event.getPlayer().getInventory().setBoots(item);
                    break;
            }
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        TriggerType triggerType = TriggerType.UNSUPPORTED;
        switch (event.getClick()) {
            case LEFT:
                triggerType = TriggerType.LEFT_CLICK_INV;
                break;
            case SHIFT_LEFT:
                triggerType = TriggerType.SHIFT_LEFT_CLICK_INV;
                break;
            case RIGHT:
                triggerType = TriggerType.RIGHT_CLICK_INV;
                break;
            case SHIFT_RIGHT:
                triggerType = TriggerType.SHIFT_RIGHT_CLICK_INV;
                break;
            case MIDDLE:
                triggerType = TriggerType.MIDDLE_CLICK_INV;
                break;
            case DOUBLE_CLICK:
                triggerType = TriggerType.DOUBLE_CLICK_INV;
                break;
            case DROP:
                triggerType = TriggerType.DROP_INV;
                break;
            case CONTROL_DROP:
                triggerType = TriggerType.CONTROL_DROP_INV;
                break;
            case WINDOW_BORDER_LEFT:
                triggerType = TriggerType.LEFT_BORDER_INV;
                break;
            case WINDOW_BORDER_RIGHT:
                triggerType = TriggerType.RIGHT_BORDER_INV;
                break;
            case NUMBER_KEY:
                triggerType = TriggerType.valueOf("NUMBER_KEY_" + (event.getHotbarButton() + 1) + " + _INV");
                break;
        }

        if (triggerType == TriggerType.UNSUPPORTED) {
            return;
        }

        Trigger trigger = new Trigger(event, (Player) event.getWhoClicked(), event.getCurrentItem(), triggerType);
        plugin.getItemManager().executeActions(trigger);
        if (trigger.shouldCancel()) {
            event.setCancelled(true);
        }
        if (trigger.shouldRemoveItem()) {
            ItemStack item = null;
            if (event.getCurrentItem().getAmount() > 1) {
                item = event.getCurrentItem();
                item.setAmount(event.getCurrentItem().getAmount() - 1);
            }
            event.setCurrentItem(item);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Trigger trigger = new Trigger(event, event.getPlayer(), event.getItemDrop().getItemStack(), TriggerType.DROP);
        plugin.getItemManager().executeActions(trigger);
        if (trigger.shouldCancel()) {
            event.setCancelled(true);
        } else if (trigger.shouldRemoveItem()) {
            // no nice way to remove dropped item if the event was cancelled
            // TODO: Add info to documentation about that incompatibility!
            event.getItemDrop().remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Trigger trigger = new Trigger(event, event.getPlayer(), event.getItem(), TriggerType.CONSUME);
        plugin.getItemManager().executeActions(trigger);
        if (trigger.shouldCancel()) {
            event.setCancelled(true);
        }
        if (trigger.shouldRemoveItem()) {
            ItemStack item = null;
            if (event.getItem().getAmount() > 1) {
                item = event.getItem();
                item.setAmount(event.getItem().getAmount() - 1);
            }
            event.setItem(item);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {
        Trigger trigger = new Trigger(event, (Player) event.getWhoClicked(), event.getRecipe().getResult(), TriggerType.CRAFT);
        plugin.getItemManager().executeActions(trigger);
        if (trigger.shouldCancel()) {
            event.setCancelled(true);
        }
        if (trigger.shouldRemoveItem()) {
            ItemStack item = null;
            if (event.getCurrentItem().getAmount() > 1) {
                item = event.getCurrentItem();
                item.setAmount(event.getCurrentItem().getAmount() - 1);
            }
            event.setCurrentItem(item);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }
}
