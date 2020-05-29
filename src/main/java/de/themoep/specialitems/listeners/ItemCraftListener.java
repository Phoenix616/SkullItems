package de.themoep.specialitems.listeners;

import de.themoep.specialitems.SpecialItem;
import de.themoep.specialitems.SpecialItems;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

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
public class ItemCraftListener implements Listener {
    private final SpecialItems plugin;

    public ItemCraftListener(SpecialItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemCraft(CraftItemEvent event) {
        if (plugin.getConfig().getBoolean("permission.craft")) {
            SpecialItem item = plugin.getItemManager().getSpecialItem(event.getRecipe().getResult());
            if (item != null
                    && !plugin.checkPerm(event.getWhoClicked(), "specialitems.craft." + item.getId(), "craft")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && plugin.getConfig().getBoolean("permission.craft")) {
            SpecialItem item = plugin.getItemManager().getSpecialItem(event.getRecipe().getResult());
            if (item != null) {
                for (HumanEntity viewer : event.getViewers()) {
                    if (!viewer.hasPermission( "specialitems.craft." + item.getId())) {
                        event.getInventory().setResult(null);
                        break;
                    }
                }
            }
        }
    }
}
