package de.themoep.skullitems.listeners;

import de.themoep.skullitems.SkullItem;
import de.themoep.skullitems.SkullItems;
import de.themoep.skullitems.actions.ActionTrigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
public class ItemInteractListener implements Listener {
    private final SkullItems plugin;

    public ItemInteractListener(SkullItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        try {
            SkullItem item = plugin.getItemManager().getSkullItem(event.getItem());
            if (item == null) {
                return;
            }
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    plugin.getItemManager().executeActions(
                            event.getPlayer(),
                            item,
                            ActionTrigger.RIGHT_CLICK_IN_HAND
                    );
                    break;
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    plugin.getItemManager().executeActions(
                            event.getPlayer(),
                            item,
                            ActionTrigger.LEFT_CLICK_IN_HAND
                    );
                    break;
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, event.getPlayer().getName() + " has an invalid item! " + e.getMessage());
        }

        event.setCancelled(true);
    }
}
