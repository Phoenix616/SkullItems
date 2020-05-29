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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Cancellable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActionSet {

    private Map<TriggerType, List<ItemAction>> actionMap = new HashMap<>();

    public ActionSet(ConfigurationSection actionSection) throws IllegalArgumentException {
        for (String key : actionSection.getKeys(false)) {
            TriggerType triggerType = TriggerType.valueOf(key.toUpperCase());

            List<ItemAction> actionList = new ArrayList<>();
            for (String actionString : actionSection.getStringList(key)) {
                actionList.add(ItemAction.fromString(actionString));
            }
            actionMap.put(triggerType, actionList);
        }
    }

    public ActionSet(String string) {
        for (String actionEntry : string.split("/")) {
            String[] actionEntryParts = actionEntry.split(":");
            if (actionEntryParts.length != 2) {
                throw new IllegalArgumentException("The string " + actionEntry + " does not represent a valid action set string of the format <trigger>:<type>[ <value...>][,<type...]!");
            }
            TriggerType triggerType = TriggerType.valueOf(actionEntryParts[0].toUpperCase());

            List<ItemAction> actionList = new ArrayList<>();
            for (String actionString : actionEntryParts[1].split(",")) {
                actionList.add(ItemAction.fromString(actionString));
            }
            actionMap.put(triggerType, actionList);
        }
    }

    /**
     * Get all the actions a specific trigger will run as a list
     * @param trigger The TriggerType
     * @return A list of ItemActions, empty if none configured
     */
    public List<ItemAction> getActions(TriggerType trigger) {
        List<ItemAction> actions = actionMap.get(trigger);
        actions = actions != null ? actions : new ArrayList<ItemAction>();
        if (actions.size() == 0) {
            for (int i = 0; i < trigger.getParents().length && actions.size() == 0; i++) {
                actions = getActions(trigger.getParents()[i]);
            }
        }
        return actions;
    }

    /**
     * Convert this action set to a string that can be added as a hidden on to an item
     * @return The ActionSet as a string for hidden strings
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<TriggerType, List<ItemAction>> entry : actionMap.entrySet()) {
            sb.append(entry.getKey()).append(':');
            for (ItemAction action : entry.getValue()) {
                sb.append(action.toString()).append(',');
            }
            sb.append('/');
        }

        return sb.toString();
    }

    /**
     * Execute all actions for a specific trigger on/with a player
     * @param trigger The trigger
     * @return Whether or not the event that triggered this should be cancelled, default is <tt>true</tt>
     */
    public Trigger execute(Trigger trigger) {
        for (ItemAction action : getActions(trigger.getType())) {
            action.execute(trigger);
        }
        if (trigger.shouldCancel() && trigger.getEvent() instanceof Cancellable) {
            ((Cancellable) trigger.getEvent()).setCancelled(true);
        }
        return trigger;
    }

    public int size() {
        return actionMap.size();
    }

    public Set<Map.Entry<TriggerType, List<ItemAction>>> entrySet() {
        return actionMap.entrySet();
    }
}
