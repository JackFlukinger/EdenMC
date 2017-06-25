package org.edenmc.kingdoms.items;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;

import java.util.Set;

/**
 * Created by Jack on 6/21/2017.
 */
public class ItemCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("items") && p.hasPermission("items.*")) {
            if (args.length >=1 && args[0].equalsIgnoreCase("give")) {
                if (args.length >=2 && Bukkit.getPlayer(args[1]) != null) {
                    if (args.length >= 3 && Kingdoms.itemMap.containsKey(args[2])) {
                        int amount = 1;
                        try {
                            if (args.length == 4 && Integer.parseInt(args[3]) <= 64 && Integer.parseInt(args[3]) >= 1) {
                                amount = Integer.parseInt(args[3]);
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage("Amount should be an integer");
                        }
                        ItemFunctions.giveItem(Bukkit.getPlayer(args[1]),args[2],amount);
                        return true;
                    }
                    return true;
                } else if (args.length >= 2 && Kingdoms.itemMap.containsKey(args[1])) {
                    int amount = 1;
                    try {
                        if (args.length == 3 && Integer.parseInt(args[2]) <= 64 && Integer.parseInt(args[2]) >= 1) {
                            amount = Integer.parseInt(args[2]);
                        }
                    } catch (NumberFormatException e) {
                        p.sendMessage("Amount should be an integer");
                    }
                    ItemFunctions.giveItem(p,args[1],amount);
                    return true;
                }
                p.sendMessage("/items give <player> [item] <amount>");
                return true;

            } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                Set<String> items = Kingdoms.itemMap.keySet();
                p.sendMessage(items.toString());
                return true;
            }
            p.sendMessage("/items [give/list]");
        }
        return true;
    }

}
