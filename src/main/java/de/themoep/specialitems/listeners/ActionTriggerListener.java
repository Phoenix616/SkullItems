package de.themoep.specialitems.listeners;

import de.themoep.specialitems.SpecialItems;
import de.themoep.specialitems.actions.TargetedTrigger;
import de.themoep.specialitems.actions.TriggerType;
import de.themoep.specialitems.actions.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
            ItemStack item = removeOne(trigger.getItem());
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
            event.setCurrentItem(removeOne(trigger.getItem()));
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
            event.getPlayer().getInventory().setItemInMainHand(removeOne(trigger.getItem()));
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
            event.setCurrentItem(removeOne(trigger.getItem()));
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Trigger trigger = new Trigger(event, player, player.getInventory().getItemInMainHand(), TriggerType.SHOOT_PROJECTILE);
            plugin.getItemManager().executeActions(trigger);
            if (trigger.shouldCancel()) {
                event.setCancelled(true);
            }
            if (trigger.shouldRemoveItem()) {
                player.getInventory().setItemInMainHand(removeOne(trigger.getItem()));
                player.updateInventory();
            }
        }
    }

    // Targeted Triggers:

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Trigger trigger = new Trigger(event, player, player.getInventory().getItemInMainHand(), TriggerType.PROJECTILE_HIT_BLOCK);
            plugin.getItemManager().executeActions(trigger);
            if (trigger.shouldCancel()) {
                // Not really supported, just remove the projectile here
                event.getEntity().remove();
            }
            if (trigger.shouldRemoveItem()) {
                player.getInventory().setItemInMainHand(removeOne(trigger.getItem()));
                player.updateInventory();
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttackEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        TriggerType triggerType = TriggerType.UNSUPPORTED;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            triggerType =
                    event.getEntity() instanceof Player
                            ? TriggerType.ATTACK_PLAYER
                            : TriggerType.ATTACK_ENTITY;
        } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
            triggerType =
                    event.getEntity() instanceof Player
                            ? TriggerType.PROJECTILE_HIT_PLAYER
                            : TriggerType.PROJECTILE_HIT_ENTITY;
        }

        if(player != null) {
            Trigger trigger = new TargetedTrigger(
                    event,
                    player,
                    event.getEntity(),
                    player.getInventory().getItemInMainHand(),
                    triggerType
            );
            plugin.getItemManager().executeActions(trigger);
            if (trigger.shouldCancel()) {
                event.setCancelled(true);
            }
            if (trigger.shouldRemoveItem()) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    item = null;
                }
                player.getInventory().setItemInMainHand(item);
                player.updateInventory();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        TriggerType triggerType =
                event.getRightClicked() instanceof Player
                        ? TriggerType.RIGHT_CLICK_PLAYER
                        : TriggerType.RIGHT_CLICK_ENTITY;
        Trigger trigger = new TargetedTrigger(
                event,
                event.getPlayer(),
                event.getRightClicked(),
                event.getPlayer().getInventory().getItemInMainHand(),
                triggerType
        );
        plugin.getItemManager().executeActions(trigger);
        if (trigger.shouldCancel()) {
            event.setCancelled(true);
        }
        if (trigger.shouldRemoveItem() && event.getHand() != null) {
            switch (event.getHand()) {
                case HAND:
                    event.getPlayer().getInventory().setItem(
                            event.getPlayer().getInventory().getHeldItemSlot(),
                            removeOne(event.getPlayer().getInventory().getItemInMainHand())
                    );
                    break;
                case OFF_HAND:
                    event.getPlayer().getInventory().setItemInOffHand(
                            removeOne(event.getPlayer().getInventory().getItemInOffHand())
                    );
                    break;
                case HEAD:
                    event.getPlayer().getInventory().setHelmet(
                            removeOne(event.getPlayer().getInventory().getHelmet())
                    );
                    break;
                case CHEST:
                    event.getPlayer().getInventory().setChestplate(
                            removeOne(event.getPlayer().getInventory().getChestplate())
                    );
                    break;
                case LEGS:
                    event.getPlayer().getInventory().setLeggings(
                            removeOne(event.getPlayer().getInventory().getLeggings())
                    );
                    break;
                case FEET:
                    event.getPlayer().getInventory().setBoots(
                            removeOne(event.getPlayer().getInventory().getBoots())
                    );
                    break;
            }
            event.getPlayer().updateInventory();
        }
    }

    private ItemStack removeOne(ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            item = null;
        }
        return item;
    }
}
