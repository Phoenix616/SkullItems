package de.themoep.specialitems.actions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
public class ItemAction {
    private final ItemActionType type;
    private final String value;

    public ItemAction(ItemActionType type) {
        this(type, "");
    }

    public ItemAction(ItemActionType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Get an ItemAction from a string like it is represented in the toString() method
     * @param string The string to get it from
     * @return The ItemAction
     */
    public static ItemAction fromString(String string) {
        ItemAction action;
        String[] splitArgs = string.split(" ");
        ItemActionType actionType = ItemActionType.valueOf(splitArgs[0].toUpperCase());
        if (actionType.requiresValue()) {
            if (splitArgs.length > 1) {
                StringBuilder value = new StringBuilder(splitArgs[1]);
                for (int i = 2; i < splitArgs.length; i++) {
                    value.append(" ").append(splitArgs[i]);
                }
                action = new ItemAction(actionType, value.toString());
            } else {
                throw new IllegalArgumentException("ActionType " + actionType + " requires an additional value! (Add it with a space after the type in the config)");
            }
        } else {
            action = new ItemAction(actionType);
        }
        return action;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getType().toString());
        if (hasValue()) {
            sb.append(' ').append(getValue());
        }
        return sb.toString();
    }

    public ItemActionType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get the value and replace some tags for that specific player
     * @param trigger The trigger that triggered this action
     * @return The value with all variables replaced
     */
    public String getValue(Trigger trigger) {
        String value = getValue();
        if (value.indexOf('%') == -1) {
            // No percentage sign for variables in value string found,
            // just return the normal value without doing all the other stuff
            return value;
        }
        Player player = trigger.getPlayer();
        List<String> repl = new ArrayList<>(Arrays.asList(
                "player", player.getName(),
                "x", Integer.toString(player.getLocation().getBlockX()),
                "y", Integer.toString(player.getLocation().getBlockY()),
                "z", Integer.toString(player.getLocation().getBlockZ()),
                "xexact", Double.toString(player.getLocation().getX()),
                "yexact", Double.toString(player.getLocation().getY()),
                "zexact", Double.toString(player.getLocation().getZ()),
                "xeye", Double.toString(player.getEyeLocation().getBlockX()),
                "yeye", Double.toString(player.getEyeLocation().getBlockY()),
                "zeye", Double.toString(player.getEyeLocation().getBlockZ()),
                "xexacteye", Double.toString(player.getEyeLocation().getX()),
                "yexacteye", Double.toString(player.getEyeLocation().getY()),
                "zexacteye", Double.toString(player.getEyeLocation().getZ()),
                "pitch", Float.toString(player.getEyeLocation().getPitch()),
                "yaw", Float.toString(player.getEyeLocation().getYaw())
        ));
        if (trigger instanceof TargetedTrigger) {
            TargetedTrigger targetedTrigger = (TargetedTrigger) trigger;
            Location targetLocation = targetedTrigger.getTarget().getLocation();
            repl.addAll(Arrays.asList(
                    "targetname", targetedTrigger.getTarget().getName(),
                    "targetx", Integer.toString(targetLocation.getBlockX()),
                    "targety", Integer.toString(targetLocation.getBlockY()),
                    "targetz", Integer.toString(targetLocation.getBlockZ()),
                    "targetxexact", Double.toString(targetLocation.getX()),
                    "targetyexact", Double.toString(targetLocation.getY()),
                    "targetzexact", Double.toString(targetLocation.getZ()),
                    "targetpitch", Float.toString(targetLocation.getPitch()),
                    "targetyaw", Float.toString(targetLocation.getYaw())
            ));
        } else if (value.contains("%target")) {
            Entity target = null;
            Location targetLocation = null;
            String targetName = "block";
            int checkDistance = 32;
            double nearest = checkDistance * checkDistance;
            double directest = 0;
            for (Entity e : player.getNearbyEntities(checkDistance, checkDistance, checkDistance)) {
                Vector toEntity = e.getLocation().toVector().subtract(player.getEyeLocation().toVector());
                double dot = toEntity.normalize().dot(player.getEyeLocation().getDirection());
                if (dot > directest) {
                    double distance = player.getLocation().distanceSquared(e.getLocation());
                    if (distance <= nearest || dot - 0.1 > directest) {
                        if (player.hasLineOfSight(e)) {
                            nearest = distance;
                            directest = dot;
                            target = e;
                        }
                    }
                }
            }
            if (target != null) {
                targetLocation = target.getLocation();
                targetName = target.getName();
            } else {
                Block block = player.getTargetBlock((Set<Material>) null, checkDistance);
                if (block != null) {
                    targetLocation = block.getLocation();
                }
            }

            if (targetLocation != null) {
                repl.addAll(Arrays.asList(
                        "targetname", targetName,
                        "targetx", Integer.toString(targetLocation.getBlockX()),
                        "targety", Integer.toString(targetLocation.getBlockY()),
                        "targetz", Integer.toString(targetLocation.getBlockZ()),
                        "targetxexact", Double.toString(targetLocation.getX()),
                        "targetyexact", Double.toString(targetLocation.getY()),
                        "targetzexact", Double.toString(targetLocation.getZ()),
                        "targetpitch", Float.toString(targetLocation.getPitch()),
                        "targetyaw", Float.toString(targetLocation.getYaw())
                ));
            }
        }
        for (int i = 0; i + 1 < repl.size(); i += 2) {
            value = value.replace("%" + repl.get(i) + "%", repl.get(i+1));
        }
        return value;
    }

    public boolean hasValue() {
        return !value.isEmpty();
    }

    /**
     * Execute an actions for on/with a player
     * @param trigger Information about the trigger that started this action
     * @return The modified trigger
     */
    public Trigger execute(Trigger trigger) {
        Player player = trigger.getPlayer();
        trigger.setCancel(true);
        switch (getType()) {
            case OPEN_CRAFTING:
                player.closeInventory();
                player.openWorkbench(null, true);
                break;
            case OPEN_ENDERCHEST:
                player.closeInventory();
                player.openInventory(player.getEnderChest());
                break;
            case OPEN_ENCHANTING:
                player.closeInventory();
                player.openEnchanting(null, true);
                break;
            case OPEN_ANVIL:
                player.closeInventory();
                player.openInventory(player.getServer().createInventory(null, InventoryType.ANVIL));
                break;
            case CLOSE_INV:
                player.closeInventory();
                break;
            case RUN_COMMAND:
                if (hasValue()) {
                    player.performCommand(getValue(trigger));
                }
                break;
            case CONSOLE_COMMAND:
                if (hasValue()) {
                    player.getServer().dispatchCommand(
                            player.getServer().getConsoleSender(),
                            getValue(trigger)
                    );
                }
                break;
            case MESSAGE:
                if (hasValue()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getValue(trigger)));
                }
                break;
            case REMOVE_ITEM:
                trigger.setRemoveItem(true);
                break;
            case DONT_CANCEL:
            default:
                trigger.setCancel(false);
                break;
        }
        return trigger;
    }
}
