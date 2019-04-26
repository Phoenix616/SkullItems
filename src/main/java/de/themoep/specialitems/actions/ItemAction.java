package de.themoep.specialitems.actions;

import de.themoep.specialitems.SpecialItems;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    public ItemAction(ItemActionType type) throws IllegalArgumentException {
        this(type, "");
    }

    /**
     *  Create a new ItemAction
     * @param type The type of this action
     * @param value The optional value of this action
     * @throws IllegalArgumentException Invalid ItemAction config
     */
    public ItemAction(ItemActionType type, String value) throws IllegalArgumentException {
        this.type = type;
        this.value = value;
        if (getType().requiresValue() && !hasValue()) {
            throw new IllegalArgumentException("ActionType " + getType() + " requires an additional value! (Add it with a space after the type in the config)");
        }
        String[] values = getValue().split(" ");
        if (getType() == ItemActionType.LAUNCH_PROJECTILE) {
            try {
                Class clazz = Class.forName("org.bukkit.entity." + values[0]);
                if (!Projectile.class.isAssignableFrom(clazz)) {
                    throw new IllegalArgumentException("Error while loading action with type " + getType() + "! The string " + values[0] + " is not a valid projectile class name! (Value: " + getValue() + ")");
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Error while loading action with type " + getType() + "! The string " + values[0] + " is not a valid projectile class name! (Value: " + getValue() + ")");
            }
            if (values.length > 1) {
                try {
                    Double.parseDouble(values[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error while loading action with type " + getType() + "! The string " + values[1] + " is not a valid double! (Value: " + getValue() + ")");
                }
            }
        } else if (getType() == ItemActionType.EFFECT) {
            int i = 0;
            if (values.length < 2) {
                throw new IllegalArgumentException("Error while loading action with type " + getType() + "! The not enough value parts! " + values.length + ", needs at least 2 (Value: " + getValue() + ")");
            }
            PotionEffectType potionType = PotionEffectType.getByName(values[i]);
            if (potionType == null) {
                i++;
                potionType = PotionEffectType.getByName(values[i]);
            }
            if (potionType == null) {
                throw new IllegalArgumentException("Error while loading action with type " + getType() + "! Neither " + values[0] + " nor " + values[1] + " are potion effects! (Value: " + getValue() + ")");
            }
            if (values.length > i + 1) {
                try {
                    Integer.parseInt(values[i + 1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error while loading action with type " + getType() + "!" + values[i + 1] + " is not a valid duration integer! (Value: " + getValue() + ")");
                }
            }
            if (values.length > i + 2) {
                try {
                    Integer.parseInt(values[i + 2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error while loading action with type " + getType() + "!" + values[i + 2] + " is not a valid amplifier integer! (Value: " + getValue() + ")");
                }
            }
        }
    }

    /**
     * Get an ItemAction from a string like it is represented in the toString() method
     * @param string The string to get it from
     * @throws IllegalArgumentException Invalid ItemAction config
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
                "trigger", trigger.getType().toString(),
                "player", player.getName(),
                "world", player.getLocation().getWorld().getName(),
                "x", String.valueOf(player.getLocation().getBlockX()),
                "y", String.valueOf(player.getLocation().getBlockY()),
                "z", String.valueOf(player.getLocation().getBlockZ()),
                "x.exact", String.valueOf(player.getLocation().getX()),
                "y.exact", String.valueOf(player.getLocation().getY()),
                "z.exact", String.valueOf(player.getLocation().getZ()),
                "eye.x", String.valueOf(player.getEyeLocation().getBlockX()),
                "eye.y", String.valueOf(player.getEyeLocation().getBlockY()),
                "eye.z", String.valueOf(player.getEyeLocation().getBlockZ()),
                "eye.x.exact", String.valueOf(player.getEyeLocation().getX()),
                "eye.y.exact", String.valueOf(player.getEyeLocation().getY()),
                "eye.z.exact", String.valueOf(player.getEyeLocation().getZ()),
                "pitch", String.valueOf(player.getEyeLocation().getPitch()),
                "yaw", String.valueOf(player.getEyeLocation().getYaw())
        ));
        if (trigger instanceof TargetedTrigger) {
            TargetedTrigger targetedTrigger = (TargetedTrigger) trigger;
            Location targetLocation = targetedTrigger.getTarget().getLocation();
            Location targetEyeLocation = targetedTrigger.getTarget().getEyeLocation();
            repl.addAll(Arrays.asList(
                    "target.name", targetedTrigger.getTarget().getName(),
                    "target.world", targetedTrigger.getTarget().getLocation().getWorld().getName(),
                    "target.x", String.valueOf(targetLocation.getBlockX()),
                    "target.y", String.valueOf(targetLocation.getBlockY()),
                    "target.z", String.valueOf(targetLocation.getBlockZ()),
                    "target.x.exact", String.valueOf(targetLocation.getX()),
                    "target.y.exact", String.valueOf(targetLocation.getY()),
                    "target.z.exact", String.valueOf(targetLocation.getZ()),
                    "target.eye.x", String.valueOf(targetEyeLocation.getBlockX()),
                    "target.eye.y", String.valueOf(targetEyeLocation.getBlockY()),
                    "target.eye.z", String.valueOf(targetEyeLocation.getBlockZ()),
                    "target.eye.x.exact", String.valueOf(targetEyeLocation.getX()),
                    "target.eye.y.exact", String.valueOf(targetEyeLocation.getY()),
                    "target.eye.z.exact", String.valueOf(targetEyeLocation.getZ()),
                    "target.pitch", String.valueOf(targetEyeLocation.getPitch()),
                    "target.yaw", String.valueOf(targetEyeLocation.getYaw())
            ));
        } else if (value.contains("%target.")) {
            Entity target = null;
            Location targetLocation = null;
            String targetName = "BLOCK";
            int checkDistance = 64;
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
                targetName = target instanceof Player ? target.getName() : target.getType() + ":" + target.getName();
            } else {
                Block block = player.getTargetBlock((Set<Material>) null, checkDistance);
                if (block != null && block.getType() != Material.AIR) {
                    targetLocation = block.getLocation();
                    targetName = "BLOCK:" + block.getType();
                }
            }

            if (targetLocation != null) {
                repl.addAll(Arrays.asList(
                        "target.name", targetName,
                        "target.world", targetLocation.getWorld().getName(),
                        "target.x", String.valueOf(targetLocation.getBlockX()),
                        "target.y", String.valueOf(targetLocation.getBlockY()),
                        "target.z", String.valueOf(targetLocation.getBlockZ()),
                        "target.x.exact", String.valueOf(targetLocation.getX()),
                        "target.y.exact", String.valueOf(targetLocation.getY()),
                        "target.z.exact", String.valueOf(targetLocation.getZ()),
                        "target.pitch", String.valueOf(targetLocation.getPitch()),
                        "target.yaw", String.valueOf(targetLocation.getYaw())
                ));
            }
        }
        for (int i = 0; i + 1 < repl.size(); i += 2) {
            value = value.replace("%" + repl.get(i) + "%", repl.get(i+1));
        }
        return value;
    }

    public boolean hasValue() {
        return value != null && !value.isEmpty();
    }

    /**
     * Execute an actions for on/with a player
     * @param trigger Information about the trigger that started this action
     * @return The modified trigger
     */
    public Trigger execute(Trigger trigger) throws IllegalArgumentException {
        Player player = trigger.getPlayer();
        trigger.setCancel(true);
        String[] values = new String[0];
        if (hasValue()) {
            values = getValue(trigger).split(" ");
        }
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
            case CLEAR_EFFECTS:
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                break;
            case EFFECT:
                int i = 0;
                if (values.length < 2) {
                    trigger.getPlayer().sendMessage(ChatColor.RED + "The item's effect is misconfigured! (" + values.length + ", needs to be at least 2)Please contact an administrator!");
                    break;
                }
                PotionEffectType potionType = PotionEffectType.getByName(values[i]);
                if (potionType == null) {
                    player = trigger.getPlayer().getServer().getPlayer(values[i]);
                    if (player == null) {
                        trigger.getPlayer().sendMessage(ChatColor.RED + "No target found! (" + values[i] + ")");
                        break;
                    }
                    i++;
                    potionType = PotionEffectType.getByName(values[i]);
                }
                if (potionType == null) {
                    trigger.getPlayer().sendMessage(ChatColor.RED + "The item's effect is misconfigured! Neiter " + values[0] + " nor " + values[1] + " are potion effects! Please contact an administrator!");
                    break;
                }
                int duration = 30;
                if (values.length > i + 1) {
                    try {
                        duration = Integer.parseInt(values[i + 1]);
                    } catch (NumberFormatException e) {
                        trigger.getPlayer().sendMessage(ChatColor.RED + "The item's effect is misconfigured! " + values[i + 1] + " is not a valid duration integer! Please contact an administrator!");
                        break;
                    }
                }
                int amplifier = 0;
                if (values.length > i + 2) {
                    try {
                        amplifier = Integer.parseInt(values[i + 2]);
                    } catch (NumberFormatException e) {
                        trigger.getPlayer().sendMessage(ChatColor.RED + "The item's effect is misconfigured! " + values[i + 1] + " is not a valid amplifier integer! Please contact an administrator!");
                        break;
                    }
                }
                boolean ambient = false;
                if (values.length > i + 3) {
                    ambient = Boolean.parseBoolean(values[i + 3]);
                }
                boolean particles = true;
                if (values.length > i + 4) {
                    particles = Boolean.parseBoolean(values[i + 4]);
                }
                player.addPotionEffect(new PotionEffect(potionType, duration * 20, amplifier, ambient, particles));
                break;
            case LAUNCH_PROJECTILE:
                try {
                    Class projectile = Class.forName("org.bukkit.entity." + values[0]);
                    Vector dir = player.getEyeLocation().getDirection();
                    if (values.length > 1) {
                        dir.multiply(Double.parseDouble(values[1]));
                    }
                    player.launchProjectile(projectile, dir);
                } catch (ClassNotFoundException e) {
                    player.sendMessage(ChatColor.RED + "Internal error while trying to launch projectile due to wrong projectile class name! Please contact an administrator!");
                    e.printStackTrace();
                }
                break;
            case RUN_COMMAND:
                player.performCommand(getValue(trigger));
                break;
            case SUDO_COMMAND:
                PermissionAttachment permAtt = player.addAttachment(
                        SpecialItems.getProvidingPlugin(SpecialItems.class),
                        "*", true
                );
                boolean isOp = player.isOp();
                if (!isOp) {
                    player.setOp(true);
                }
                try {
                    player.performCommand(getValue(trigger));
                } finally {
                    if (!isOp) {
                        player.setOp(false);
                    }
                    permAtt.remove();
                }
                break;
            case CONSOLE_COMMAND:
                player.getServer().dispatchCommand(
                        player.getServer().getConsoleSender(),
                        getValue(trigger)
                );
                break;
            case MESSAGE:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getValue(trigger)));
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
