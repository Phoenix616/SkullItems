package de.themoep.specialitems.listeners;

/*
 * SpecialItems
 * Copyright (c) 2020 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.themoep.specialitems.SpecialItem;
import de.themoep.specialitems.SpecialItems;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

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
