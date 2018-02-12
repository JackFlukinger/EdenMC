package org.edenmc.kingdoms.economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

/**
 * Created by Jack on 6/24/2017.
 */
public class GoldCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Citizen p = Kingdoms.getCitizen(sender.getName());
        if (cmd.getName().equalsIgnoreCase("gold") && p.getPlayer().hasPermission("gold.*")) {
            if (args.length == 3) {
                if (args[0].equals("set") && p.getPlayer().hasPermission("gold.set")) {
                    if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                        Player recipient = Bukkit.getServer().getPlayerExact(args[1]);
                        Citizen recip = Kingdoms.getCitizen(recipient.getName());
                        try {
                            Integer newBal = Integer.parseInt(args[2]);
                            recip.setBalance(newBal);
                        } catch (NumberFormatException e) {
                            p.getPlayer().sendMessage("Not a valid integer");
                            e.printStackTrace();
                        }
                    } else {
                        p.getPlayer().sendMessage(args[1] + " is not a valid player");
                    }
                }
            } else if (args.length == 1) {
                if (Bukkit.getServer().getPlayerExact(args[0]) != null) {
                    Citizen check = Kingdoms.getCitizen(args[0]);
                    p.getPlayer().sendMessage("Balance: " + check.getBalance());
                }
            } else {
                p.getPlayer().sendMessage("/gold [set] [username] [amount]");
            }
            return true;
        }
        return false;
    }

}
