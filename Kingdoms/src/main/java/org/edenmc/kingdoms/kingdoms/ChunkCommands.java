package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.UUID;

/**
 * Created by Jack on 2/19/2018.
 */
public class ChunkCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("chunk")) {
            Player p = (Player) sender;
            Citizen c = Kingdoms.getCitizen(p.getName());
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("claim")) {
                    if (c.getKingdom() == null | c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be in a kingdom to claim a chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bThis chunk is not in your kingdom!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ()).getKingdom().equals(c.getKingdom())) {
                        p.sendMessage("§bThis chunk is not in your kingdom!");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    if (!ch.getFlags().contains("claimable")) {
                        p.sendMessage("§bThis chunk is not claimable!");
                        return true;
                    }
                    ch.setOwner(p.getUniqueId().toString());
                    ch.removeFlag("claimable");
                    ch.removeFlag("locked");
                    p.sendMessage("§bYou successfully claimed this chunk!");
                    return true;
                } else if (args[0].equalsIgnoreCase("unclaim")) {
                    if (c.getKingdom() == null | c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be in a kingdom to unclaim a chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bThis chunk is not in your kingdom!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ()).getKingdom().equals(c.getKingdom())) {
                        p.sendMessage("§bThis chunk is not in your kingdom!");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    if (!ch.getOwner().equalsIgnoreCase(p.getUniqueId().toString()) && !Kingdoms.getKingdom(ch.getKingdom()).getOwner().equalsIgnoreCase(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    ch.setOwner("");
                    ch.clearMembers();
                    ch.addFlag("claimable");
                    ch.addFlag("locked");
                    p.sendMessage("§bYou have unclaimed this chunk!");
                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bThis chunk is not in a kingdom!");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    String memberString = "";
                    for (String uuid : ch.getMembers()) {
                        if (uuid != null && !uuid.equals("")) {
                            memberString = memberString + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";
                        }
                    }
                    if (memberString.length() > 2) {
                        memberString = memberString.substring(0, memberString.length() - 2);
                    }
                    String flagString = "";
                    for (String flag : ch.getFlags()) {
                        if (flag != null && !flag.equals("")) {
                            flagString = flagString + flag + ", ";
                        }
                    }
                    if (flagString.length() > 2) {
                        flagString = flagString.substring(0, flagString.length() - 2);
                    }
                    p.sendMessage("§3-----------------------------------");
                    p.sendMessage("§9Kingdom: §b" + ch.getKingdom());
                    if (ch.getOwner() != null && !ch.getOwner().equals("")) {
                        p.sendMessage("§9Owner: §b" + Bukkit.getOfflinePlayer(UUID.fromString(ch.getOwner())).getName());
                    } else {
                        p.sendMessage("§9Owner:");
                    }
                    p.sendMessage("§9Members: §b" + memberString);
                    p.sendMessage("§9Flags: §b" + flagString);
                    p.sendMessage("§3-----------------------------------");
                    return true;
                }
            } else if (args.length > 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ()).getKingdom().equals(c.getKingdom())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    if (!ch.getOwner().equalsIgnoreCase(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (args.length < 2) {
                        p.sendMessage("§bPlease specify a player to add to your chunk.");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase(p.getName())) {
                        p.sendMessage("§bYou cannot add yourself as a member!");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) == null) {
                        p.sendMessage("§bInvalid player.");
                        return true;
                    }
                    Player added = Bukkit.getPlayer(args[1]);
                    ch.addMember(added.getUniqueId().toString());
                    added.sendMessage("§bYou have been added to " + p.getName() + "'s chunk!");
                    p.sendMessage("§bYou have added " + added.getName() + " to your chunk! They now have permission to build and use blocks in your chunk.");
                    return true;
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ()).getKingdom().equals(c.getKingdom())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    if (!ch.getOwner().equalsIgnoreCase(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (args.length < 2) {
                        p.sendMessage("§bPlease specify a player to remove from your chunk.");
                        return true;
                    }
                    for (String uuid : ch.getMembers()) {
                        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName().equals(args[1])) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player removed = Bukkit.getPlayer(args[1]);
                                removed.sendMessage("§bYou have been removed from " + p.getName() + "'s chunk!");
                            }
                            ch.removeMember(uuid);
                            p.sendMessage("§bYou have removed " + args[1] + " from your chunk! They no longer have permission to build and use blocks in your chunk.");
                            return true;
                        }
                    }
                    p.sendMessage("§bInvalid player.");
                    return true;
                } else if (args[0].equalsIgnoreCase("flag")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (!Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ()).getKingdom().equals(c.getKingdom())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    if (!ch.getOwner().equalsIgnoreCase(p.getUniqueId().toString()) && !Kingdoms.getKingdom(ch.getKingdom()).getOwner().equalsIgnoreCase(p.getUniqueId().toString()) && !Kingdoms.getKingdom(ch.getKingdom()).getWardens().contains(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou do not own this chunk!");
                        return true;
                    }
                    if (args.length < 3) {
                        p.sendMessage("§b/chunk flag [add/remove] [claimable/locked]");
                        return true;
                    }
                    if (!args[1].equals("add") && !args[1].equals("remove")) {
                        p.sendMessage("§b/chunk flag [add/remove] [claimable/locked]");
                        return true;
                    }
                    if (args[1].equals("add") && (args[2].equals("locked") | args[2].equals("claimable"))) {
                        if (args[2].equals("locked")) {
                            if (ch.getOwner() != null && !ch.getOwner().equals("")) {
                                p.sendMessage("§bOnly unclaimed chunks can be locked!");
                                return true;
                            }
                            if (!Kingdoms.getKingdom(ch.getKingdom()).getOwner().equalsIgnoreCase(p.getUniqueId().toString()) && !Kingdoms.getKingdom(ch.getKingdom()).getWardens().contains(p.getUniqueId().toString())) {
                                p.sendMessage("§bOnly the owner or a warden of the kingdom can lock chunks!");
                                return true;
                            }
                        }
                        if (args[2].equals("claimable")) {
                            if (ch.getOwner() != null && !ch.getOwner().equals("")) {
                                p.sendMessage("§bOnly unclaimed chunks can be claimable!");
                                return true;
                            }
                        }
                        ch.addFlag(args[2]);
                        p.sendMessage("§bFlag added.");
                        return true;
                    } else if (args[1].equals("remove") && (args[2].equals("locked") | args[2].equals("claimable"))) {
                        if (args[2].equals("locked") && !Kingdoms.getKingdom(ch.getKingdom()).getOwner().equalsIgnoreCase(p.getUniqueId().toString()) && !Kingdoms.getKingdom(ch.getKingdom()).getWardens().contains(p.getUniqueId().toString())) {
                            p.sendMessage("§bOnly the owner or a warden of the kingdom can unlock chunks!");
                            return true;
                        }
                        ch.removeFlag(args[2]);
                        p.sendMessage("§bFlag removed.");
                        return true;
                    }
                    p.sendMessage("§b/chunk flag [add/remove] [claimable/locked]");
                    return true;
                }
            }
            p.sendMessage("§b/chunk claim §9- Claims a claimable chunk");
            p.sendMessage("§b/chunk unclaim §9- Unclaims a chunk you own");
            p.sendMessage("§b/chunk add [player] §9- Adds a member to a chunk you own");
            p.sendMessage("§b/chunk remove [player] §9- Removes a member from a chunk you own");
            p.sendMessage("§b/chunk flag [add/remove] [claimable/locked] §9- Add or remove a flag");
            p.sendMessage("§b/chunk info §9- Prints info about the current chunk");

        }
        return true;
    }
}
