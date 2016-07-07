package de.themoep.specialitems.actions;

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
public enum TriggerType {
    // Hand triggers:
    RIGHT_CLICK_HAND,
    LEFT_CLICK_HAND,
    MIDDLE_CLICK_HAND,

    // Inventory triggers:
    RIGHT_CLICK_INV,
    SHIFT_RIGHT_CLICK_INV,
    LEFT_CLICK_INV,
    SHIFT_LEFT_CLICK_INV,
    MIDDLE_CLICK_INV,
    DOUBLE_CLICK_INV,
    DROP_INV,
    CONTROL_DROP_INV,
    LEFT_BORDER_INV,
    RIGHT_BORDER_INV,
    NUMBER_KEY_1_INV,
    NUMBER_KEY_2_INV,
    NUMBER_KEY_3_INV,
    NUMBER_KEY_4_INV,
    NUMBER_KEY_5_INV,
    NUMBER_KEY_6_INV,
    NUMBER_KEY_7_INV,
    NUMBER_KEY_8_INV,
    NUMBER_KEY_9_INV,

    // Entity interaction:
    ATTACK_ENTITY,
    ATTACK_PLAYER,
    RIGHT_CLICK_PLAYER,
    RIGHT_CLICK_ENTITY,

    // Other:
    SHOOT_PROJECTILE,
    CONSUME,
    DROP,
    CRAFT,

    UNSUPPORTED,
}
