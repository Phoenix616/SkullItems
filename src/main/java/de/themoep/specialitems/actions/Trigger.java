package de.themoep.specialitems.actions;

import de.themoep.specialitems.SpecialItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
public class Trigger {
    private final Event event;
    private final Player player;
    private final ItemStack item;
    private final TriggerType type;
    private boolean cancel = false;
    private boolean removeItem = false;

    private SpecialItem specialItem = null;

    /**
     * This object stores information about the trigger that will trigger actions
     * @param event The event that created this trigger
     * @param player The player for which the actions should be run
     * @param item The item for which the actions should be executed for
     * @param type The type of this trigger
     */
    public Trigger(Event event, Player player, ItemStack item, TriggerType type) {
        this.event = event;
        this.player = player;
        this.item = item;
        this.type = type;
    }

    public Trigger(Event event, Player player, SpecialItem item, TriggerType type) {
        this(event, player, item.getItem(), type);
        this.specialItem = item;
    }

    public Event getEvent() {
        return event;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public TriggerType getType() {
        return type;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean shouldCancel() {
        return cancel;
    }

    public void setRemoveItem(boolean removeItem) {
        this.removeItem = removeItem;
    }

    public boolean shouldRemoveItem() {
        return removeItem;
    }

    public void setSpecialItem(SpecialItem specialItem) {
        this.specialItem = specialItem;
    }

    public SpecialItem getSpecialItem() {
        return specialItem;
    }

    public boolean hasSpecialItem() {
        return specialItem != null;
    }
}
