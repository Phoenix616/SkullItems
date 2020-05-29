package de.themoep.specialitems.actions;

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
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class TargetedTrigger extends Trigger {
    private TriggerTarget target;

    public TargetedTrigger(Event event, Player player, Entity target, ItemStack item, TriggerType type) {
        super(event, player, item, type);
        this.target = new TriggerTarget(target);
    }

    public TargetedTrigger(Event event, Player player, Entity target, SpecialItem item, TriggerType type) {
        super(event, player, item, type);
        this.target = new TriggerTarget(target);
    }

    public TargetedTrigger(Event event, Player player, Block target, ItemStack item, TriggerType type) {
        super(event, player, item, type);
        this.target = new TriggerTarget(target);
    }

    public TriggerTarget getTarget() {
        return target;
    }

    public class TriggerTarget {
        private String name;
        private Location location;
        private Location eyeLocation;

        public TriggerTarget(Entity target) {
            if (target.getType() == EntityType.PLAYER) {
                name = target.getName();
            } else {
                name = target.getType() + ":" + target.getName();
            }
            location = target.getLocation();
            eyeLocation = target instanceof LivingEntity ? ((LivingEntity) target).getEyeLocation() : location;
        }

        public TriggerTarget(Block target) {
            name = "BLOCK:" + target.getType().toString();
            location = target.getLocation();
            eyeLocation = location;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        public Location getEyeLocation() {
            return eyeLocation;
        }
    }
}
