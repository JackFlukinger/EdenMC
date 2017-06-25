package org.edenmc.kingdoms.economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;

/**
 * Created by Jack on 6/24/2017.
 */
public class GoldCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("gold") && p.hasPermission("gold.*")) {
            if (args.length == 3) {
                if (args[0].equals("set") && p.hasPermission("gold.set")) {
                    if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                        Player recipient = Bukkit.getServer().getPlayerExact(args[1]);
                        try {
                            Integer newBal = Integer.parseInt(args[2]);
                            GoldFunctions.setBalance(recipient, newBal);
                        } catch (NumberFormatException e) {
                            p.sendMessage("Not a valid integer");
                            e.printStackTrace();
                        }
                    } else {
                        p.sendMessage(args[1] + " is not a valid player");
                    }
                }
            } else if (args.length == 1) {
                if (Bukkit.getServer().getPlayerExact(args[0]) != null) {
                    p.sendMessage("Balance: " + Kingdoms.playerGold.get(Bukkit.getServer().getPlayerExact(args[0])));
                }
            } else {
                p.sendMessage("/gold [set] [username] [amount]");
            }
            return true;
        }
        return false;
    }

}
