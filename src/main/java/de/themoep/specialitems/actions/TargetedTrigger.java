package de.themoep.specialitems.actions;

import de.themoep.specialitems.SpecialItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
