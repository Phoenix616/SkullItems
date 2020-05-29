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
public enum TriggerType {
    // Hand triggers:
    HAND,
    CLICK_AIR (HAND),
    CLICK_BLOCK (HAND),
    RIGHT_CLICK_HAND(HAND),
    RIGHT_CLICK_AIR(RIGHT_CLICK_HAND, CLICK_AIR),
    RIGHT_CLICK_BLOCK(RIGHT_CLICK_HAND, CLICK_BLOCK),
    LEFT_CLICK_HAND(HAND),
    LEFT_CLICK_AIR(LEFT_CLICK_HAND, CLICK_AIR),
    LEFT_CLICK_BLOCK(LEFT_CLICK_HAND, CLICK_BLOCK),
    MIDDLE_CLICK_HAND(HAND),
    MIDDLE_CLICK_AIR(MIDDLE_CLICK_HAND, CLICK_AIR),
    MIDDLE_CLICK_BLOCK(MIDDLE_CLICK_HAND, CLICK_BLOCK),

    // Inventory triggers:
    INVENTORY,
    RIGHT_CLICK_INV(INVENTORY),
    LEFT_CLICK_INV(INVENTORY),
    MIDDLE_CLICK_INV(INVENTORY),
    SHIFT_CLICK_INV(INVENTORY),
    SHIFT_RIGHT_CLICK_INV(RIGHT_CLICK_INV),
    SHIFT_LEFT_CLICK_INV(LEFT_CLICK_INV),
    DOUBLE_CLICK_INV(INVENTORY),
    DROP_INV(INVENTORY),
    CONTROL_DROP_INV(DROP_INV),
    LEFT_BORDER_INV(INVENTORY),
    RIGHT_BORDER_INV(INVENTORY),
    NUMBER_KEY_INV(INVENTORY),
    NUMBER_KEY_1_INV(NUMBER_KEY_INV),
    NUMBER_KEY_2_INV(NUMBER_KEY_INV),
    NUMBER_KEY_3_INV(NUMBER_KEY_INV),
    NUMBER_KEY_4_INV(NUMBER_KEY_INV),
    NUMBER_KEY_5_INV(NUMBER_KEY_INV),
    NUMBER_KEY_6_INV(NUMBER_KEY_INV),
    NUMBER_KEY_7_INV(NUMBER_KEY_INV),
    NUMBER_KEY_8_INV(NUMBER_KEY_INV),
    NUMBER_KEY_9_INV(NUMBER_KEY_INV),

    // Entity interaction:
    ATTACK_ENTITY,
    ATTACK_PLAYER(ATTACK_ENTITY),
    RIGHT_CLICK_ENTITY,
    RIGHT_CLICK_PLAYER(RIGHT_CLICK_ENTITY),
    PROJECTILE_HIT,
    PROJECTILE_HIT_PLAYER(PROJECTILE_HIT, ATTACK_PLAYER),
    PROJECTILE_HIT_ENTITY(PROJECTILE_HIT, ATTACK_ENTITY),
    PROJECTILE_HIT_BLOCK(PROJECTILE_HIT),

    // Other:
    SHOOT_PROJECTILE,
    CONSUME,
    DROP,
    CRAFT,
    BLOCK_PLACE,

    UNSUPPORTED,;

    private final TriggerType[] parents;

    TriggerType(TriggerType... parents) {
        this.parents = parents;
    }

    public TriggerType[] getParents() {
        return parents;
    }

    public boolean isChildOf(TriggerType parent) {
        for (TriggerType type : parents) {
            if (type == parent || type.isChildOf(parent)) {
                return true;
            }
        }
        return false;
    }
}
