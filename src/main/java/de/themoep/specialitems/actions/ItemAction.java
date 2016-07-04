package de.themoep.specialitems.actions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

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
class ItemAction {
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

    public boolean hasValue() {
        return !value.isEmpty();
    }

    /**
     * Execute an actions for on/with a player
     * @param player The player who triggered this special item
     * @return Whether or not the event that triggered this should be cancelled, default is <tt>true</tt>
     */
    public boolean execute(Player player) {
        boolean cancel = true;
        switch (getType()) {
            case DONT_CANCEL:
                cancel = false;
                break;
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
            case RUN_COMMAND:
                if (hasValue()) {
                    player.performCommand(getValue().replace("%player%", player.getName()));
                }
                break;
            case CONSOLE_COMMAND:
                if (hasValue()) {
                    player.getServer().dispatchCommand(
                            player.getServer().getConsoleSender(),
                            getValue().replace("%player%", player.getName())
                    );
                }
                break;
            case MESSAGE:
                if (hasValue()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            getValue().replace("%player%", player.getName())
                    ));
                }
        }
        return cancel;
    }
}
