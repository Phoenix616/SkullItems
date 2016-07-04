package de.themoep.specialitems;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
public class SpecialItemCommand implements CommandExecutor {
    private final SpecialItems plugin;

    public SpecialItemCommand(SpecialItems plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0])) {
                if (plugin.checkPerm(sender, "specialitems.command.reload")) {
                    plugin.loadConfig();
                    sender.sendMessage(plugin.getTag() + ChatColor.YELLOW + "Config reloaded!");
                }
            } else if ("list".equalsIgnoreCase(args[0])) {
                if (plugin.checkPerm(sender, "specialitems.command.list")) {
                    sender.sendMessage(ChatColor.YELLOW + "Available special items:");
                    if (plugin.getItemManager().getSpecialItems().size() == 0) {
                        sender.sendMessage(ChatColor.RED + "None");
                    } else {
                        for (SpecialItem item : plugin.getItemManager().getSpecialItems()) {
                            sender.sendMessage(
                                    ChatColor.YELLOW + "Name: "
                                            + ChatColor.RESET + item.getId()
                                            + ChatColor.YELLOW + " - Displayname: "
                                            + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', item.getName())
                            );
                        }
                    }
                }
            } else if ("get".equalsIgnoreCase(args[0])) {
                if (plugin.checkPerm(sender, "specialitems.command.get")) {
                    if (args.length > 1) {
                        ItemStack item = plugin.getItemManager().getItem(args[1]);
                        if (item != null) {
                            Player player = null;
                            if (args.length > 2) {
                                if (plugin.checkPerm(sender, "specialitems.command.give.others")) {
                                    player = plugin.getServer().getPlayer(args[2]);
                                    if (player == null || !player.isOnline()) {
                                        sender.sendMessage(plugin.getTag() + ChatColor.RED + "No player with the name "
                                                + ChatColor.YELLOW + args[2] + ChatColor.RED + " found!");
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            } else if (sender instanceof Player) {
                                player = (Player) sender;
                            } else {
                                sender.sendMessage(plugin.getTag() + ChatColor.RED
                                        + "Use /" + label + " get <itemname> <playername> " +
                                        "to give a special item to a player from the console!");
                                return true;
                            }

                            if(player.getInventory().addItem(item).size() > 0) {
                                sender.sendMessage(plugin.getTag() + ChatColor.RED
                                        + "Could not give item as you don't have any space iny our inventory!");
                            } else {
                                sender.sendMessage(plugin.getTag() + ChatColor.YELLOW + "Gave "
                                        + ChatColor.RESET + item.getItemMeta().getDisplayName()
                                        + ChatColor.YELLOW + " to " + ChatColor.RESET + player.getName());
                            }
                        } else {
                            sender.sendMessage(plugin.getTag() + ChatColor.RED + "No item with the name "
                                    + ChatColor.YELLOW + args[1] + ChatColor.YELLOW + " found!");
                        }
                    } else {
                        sender.sendMessage(plugin.getTag() + ChatColor.RED + "Usage: /" + label + " get <itemname>");
                    }
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
