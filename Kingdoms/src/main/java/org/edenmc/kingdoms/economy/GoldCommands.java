package org.edenmc.kingdoms.economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

/**
 * Created by Jack on 6/24/2017.
 */
public class GoldCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Citizen p = null;
        if (sender instanceof Player) {
            p = Kingdoms.getCitizen(sender.getName());
        }
        if (cmd.getName().equalsIgnoreCase("gold")) {
            if (args.length == 3) {
                if (args[0].equals("set") && p.getPlayer().hasPermission("gold.set")) {
                    if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                        Player recipient = Bukkit.getServer().getPlayerExact(args[1]);
                        Citizen recip = Kingdoms.getCitizen(recipient.getName());
                        try {
                            Integer newBal = Integer.parseInt(args[2]);
                            recip.setBalance(newBal);
                        } catch (NumberFormatException e) {
                            p.getPlayer().sendMessage("§bNot a valid integer");
                            e.printStackTrace();
                        }
                    } else {
                        p.getPlayer().sendMessage("§b" + args[1] + " is not a valid player");
                    }
                } else if (args[0].equals("give") && sender instanceof ConsoleCommandSender) {
                    if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                        Player recipient = Bukkit.getServer().getPlayerExact(args[1]);
                        Citizen recip = Kingdoms.getCitizen(recipient.getName());
                        try {
                            Integer newBal = recip.getBalance() + Integer.parseInt(args[2]);
                            recip.setBalance(newBal);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§bNot a valid integer");
                            e.printStackTrace();
                        }
                    } else {
                        p.getPlayer().sendMessage("§b" + args[1] + " is not a valid player");
                    }
                } else if (args[0].equals("set")){
                    p.getPlayer().sendMessage("§b/gold [set] [player] [amount]");
                }
            } else if (args.length == 1) {
                if (Bukkit.getServer().getPlayer(args[0]) != null) {
                    Citizen check = Kingdoms.getCitizen(Bukkit.getServer().getPlayer(args[0]).getName());
                    p.getPlayer().sendMessage("§bBalance: " + check.getBalance() + " Gold Pieces");
                    return true;
                }
                p.getPlayer().sendMessage("§b/gold [player]");
                return true;
            } else if (args.length == 0) {
                Citizen check = p;
                p.getPlayer().sendMessage("§bBalance: " + check.getBalance() + " Gold Pieces");
                return true;
            }
            return true;
        }
        return false;
    }

}
