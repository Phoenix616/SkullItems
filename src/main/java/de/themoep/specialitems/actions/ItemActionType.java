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
enum ItemActionType {
    OPEN_ENDERCHEST,
    OPEN_CRAFTING,
    OPEN_ENCHANTING,
    OPEN_ANVIL,
    RUN_COMMAND(true),
    CONSOLE_COMMAND(true),
    MESSAGE(true),
    DONT_CANCEL;

    private final boolean requiresValue;

    ItemActionType(boolean requiresValue) {
        this.requiresValue = requiresValue;
    }

    ItemActionType() {
        this.requiresValue = false;
    }

    public boolean requiresValue() {
        return requiresValue;
    }
}
