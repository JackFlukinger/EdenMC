package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.MySQL;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;

/**
 * Created by Jack on 2/18/2018.
 */
public class KingdomCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("kingdom") ) {
            Player p = (Player) sender;
            Citizen c = new Citizen(p);
            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length > 1) {
                        if (c.getKingdom() != null) {
                            p.sendMessage("§bYou are already a resident of a Kingdom!");
                            return true;
                        }
                        if (Kingdoms.isKingdomNear(p.getLocation().getChunk())) {
                            p.sendMessage("§bA kingdom already exists within 15 chunks.");
                            return true;
                        }
                        if (Kingdoms.getKingdom(args[1]) != null) {
                            p.sendMessage("§bA Kingdom already exists with that name.");
                            return true;
                        }
                        if (args[1].length() > 35) {
                            p.sendMessage("§bThat name is too long!");
                            return true;
                        }
                        KingdomChunk ch = new KingdomChunk(p.getLocation().getChunk(), args[1], "", new ArrayList<String>());
                        Kingdoms.addChunk(ch);
                        ArrayList<KingdomChunk> chunks = new ArrayList<KingdomChunk>();
                        chunks.add(ch);
                        ArrayList<String> res = new ArrayList<String>();
                        res.add(p.getUniqueId().toString());
                        ArrayList<String> war = new ArrayList<String>();
                        c.setKingdom(args[1]);
                        Kingdom k = new Kingdom(args[1], p.getUniqueId().toString(), chunks, war, res, new ArrayList<String>());
                        Kingdoms.setKingdom(k);
                        String[] data = {args[1], p.getUniqueId().toString(), "", p.getUniqueId().toString(), ""};
                        String[] columns = {"kingdom", "owner", "wardens", "residents", "flags"};
                        MySQL.enterData("kingdoms", columns, data);
                        Bukkit.broadcastMessage("§b" + p.getDisplayName() + " §bhas create the Kingdom of " + args[1] + "!");
                    } else {
                        p.sendMessage("§b/kingdom create [name]");
                    }
                } else if (args[0].equalsIgnoreCase("expand")) {
                    if (c.getKingdom() == null) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to expand.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to expand.");
                        return true;
                    }
                    if (!Kingdoms.isNextToKingdom(p.getLocation().getChunk())) {
                        p.sendMessage("§bYou must be adjacent to your kingdom to expand it.");
                        return true;
                    }
                    KingdomChunk ch = new KingdomChunk(p.getLocation().getChunk(), c.getKingdom(), "", new ArrayList<String>());
                    Kingdoms.addChunk(ch);
                    Kingdoms.getKingdom(c.getKingdom()).addChunk(ch);
                    p.sendMessage("§bKingdom expanded");

                } else if (args[0].equalsIgnoreCase("help")) {
                    p.sendMessage("§b/kingdom create [name] §c- Creates a new kingdom called [name]");
                    p.sendMessage("§b/kingdom expand §c- Expands kingdom to current chunk");
                    p.sendMessage("§b/kingdom claim [name] §c- Claims chunk");

                }
            }
        }
        return true;
    }

}
