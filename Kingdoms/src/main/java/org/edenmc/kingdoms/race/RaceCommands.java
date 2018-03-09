package org.edenmc.kingdoms.race;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

/**
 * Created by Jack on 3/8/2018.
 */
public class RaceCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = Bukkit.getPlayer(sender.getName());
            Citizen c = Kingdoms.getCitizen(sender.getName());
            if (Kingdoms.getCooldowns().isConfigurationSection("Race Cooldowns") && Kingdoms.getCooldowns().getConfigurationSection("Race Cooldowns").getKeys(false).contains(p.getUniqueId().toString()) && System.currentTimeMillis() - Kingdoms.getCooldowns().getLong("Race Cooldowns." + p.getUniqueId().toString()) < (Kingdoms.switchRaceCooldown * 1000)) {
                p.sendMessage("§bYou already switched races within the past 24 hours!");
                return true;
            }
            RaceGUI gui = new RaceGUI();
            gui.open(p);
            return true;
        }
        return true;
    }
}
