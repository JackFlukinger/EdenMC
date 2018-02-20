package org.edenmc.kingdoms.kingdoms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

/**
 * Created by Jack on 2/19/2018.
 */
public class ChunkCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("chunk")) {
            Player p = (Player) sender;
            Citizen c = Kingdoms.getCitizen(p.getName());
        }
        return true;
    }
}
