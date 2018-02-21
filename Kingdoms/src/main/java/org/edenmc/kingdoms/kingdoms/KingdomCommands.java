package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.MySQL;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Jack on 2/18/2018.
 */
public class KingdomCommands implements CommandExecutor {
    static HashMap<String, String> pendingInvites = new HashMap<String,String>();
    static HashMap<String, String> pendingLeaves = new HashMap<String, String>();
    static HashMap<String, String> pendingDeletes = new HashMap<String, String>();
    static HashMap<String, String> pendingStepDowns = new HashMap<String,String>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("kingdom") ) {
            Player p = (Player) sender;
            Citizen c = Kingdoms.getCitizen(p.getName());
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length > 1) {
                        if (c.getKingdom() != null && !c.getKingdom().equals("")) {
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
                } else if (args[0].equalsIgnoreCase("claim")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to claim land.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to claim land.");
                        return true;
                    }
                    if (Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bThis chunk is already the property of your kingdom.");
                        return true;
                    }
                    if (!Kingdoms.isNextToKingdom(p.getLocation().getChunk())) {
                        p.sendMessage("§bYou must be adjacent to your kingdom to claim a chunk.");
                        return true;
                    }
                    KingdomChunk ch = new KingdomChunk(p.getLocation().getChunk(), c.getKingdom(), "", new ArrayList<String>());
                    Kingdoms.addChunk(ch);
                    Kingdoms.getKingdom(c.getKingdom()).addChunk(ch);
                    p.sendMessage("§bChunk claimed!");
                } else if (args[0].equalsIgnoreCase("unclaim")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to unclaim land.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to unclaim land.");
                        return true;
                    }
                    if (!Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                        p.sendMessage("§bThis chunk is not the property of your kingdom.");
                        return true;
                    }
                    if (!Kingdoms.isNextToWilderness(p.getLocation().getChunk())) {
                        p.sendMessage("§bYou must unclaim land on the border of your kingdom.");
                        return true;
                    }
                    if (Kingdoms.getKingdom(c.getKingdom()).getChunks().size() < 2) {
                        p.sendMessage("§bYou must have at least one claimed chunk.");
                        return true;
                    }
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    Kingdoms.removeChunk(ch);
                    Kingdoms.getKingdom(c.getKingdom()).removeChunk(ch);
                    p.sendMessage("§bChunk unclaimed!");
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to add a player.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to add a player.");
                        return true;
                    }
                    if (args.length < 2) {
                        p.sendMessage("§bPlease specify a player to invite to your kingdom.");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) == null) {
                        p.sendMessage("§bInvalid player.");
                        return true;
                    }
                    Player invited = Bukkit.getPlayer(args[1]);
                    Citizen cit = Kingdoms.getCitizen(invited.getName());
                    if (cit.getKingdom() != null && !c.getKingdom().equals("")) {
                        p.sendMessage("§bPlayer is already in a kingdom.");
                        return true;
                    }
                    invited.sendMessage("§bYou have been invited to join the Kingdom of " + c.getKingdom() + "!");
                    invited.sendMessage("§bType /accept to accept the invitation.");
                    invited.sendMessage("§bType /deny to decline the invitation.");
                    pendingInvites.put(invited.getName(),c.getKingdom());
                    Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            pendingInvites.remove(invited.getName());
                        }
                    }, 1200L);
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to remove a player.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner or warden of a kingdom to remove a player.");
                        return true;
                    }
                    if (args.length < 2) {
                        p.sendMessage("§bPlease specify a player to remove from the kingdom.");
                        return true;
                    }
                    if (args[1].equals(p.getName())) {
                        p.sendMessage("§bYou cannot remove yourself from the kingdom. Use /kingdom leave instead.");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        if (Bukkit.getOfflinePlayer(UUID.fromString(Kingdoms.getKingdom(c.getKingdom()).getOwner())).getName().equals(args[1])) {
                            p.sendMessage("§bYou cannot remove the owner of your kingdom.");
                            return true;
                        }
                        Player removed = Bukkit.getPlayer(args[1]);
                        Citizen cit = Kingdoms.getCitizen(removed.getName());
                        cit.setKingdom("");
                        Kingdoms.getKingdom(c.getKingdom()).removeResident(removed.getUniqueId().toString());
                        p.sendMessage("§b" + removed.getName() + " §bhas been removed from the kingdom.");
                        return true;
                    } else {
                        for (String uuid : Kingdoms.getKingdom(c.getKingdom()).getResidents()) {
                            if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName().equals(args[1])) {
                                Kingdoms.getKingdom(c.getKingdom()).removeResident(uuid);
                                p.sendMessage("§b" + args[1] + " §bhas been removed from the kingdom.");
                                return true;
                            }
                        }
                        p.sendMessage("§bInvalid player.");
                    }

                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be a resident of a kingdom to leave.");
                        return true;
                    }
                    if (Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bOwners cannot leave their own kingdoms! Either /kingdom promote a warden or /kingdom delete.");
                        return true;
                    }
                    p.sendMessage("§bDo you want to leave your kingdom?");
                    p.sendMessage("§bYou have 15 seconds to /accept or /deny!");
                    pendingLeaves.put(p.getName(),c.getKingdom());
                    Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            pendingLeaves.remove(p.getName());
                        }
                    }, 300L);

                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner of a kingdom to delete it.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner of a kingdom to delete it.");
                        return true;
                    }
                    p.sendMessage("§bAre you sure you want to irreversibly delete your kingdom?");
                    p.sendMessage("§bYou have 15 seconds to /accept or /deny!");
                    pendingDeletes.put(p.getName(),c.getKingdom());
                    Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            pendingDeletes.remove(p.getName());
                        }
                    }, 300L);
                } else if (args[0].equalsIgnoreCase("promote")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner of a kingdom to promote a player.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner of a kingdom to promote a player.");
                        return true;
                    }
                    if (args.length != 2) {
                        p.sendMessage("§b/kingdom promote [player]");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) == null) {
                        p.sendMessage("§bPlayer must be online to be promoted.");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]).getName().equals(p.getName())) {
                        p.sendMessage("§bYou cannot promote yourself!");
                        return true;
                    }
                    Player promoted = Bukkit.getPlayer(args[1]);
                    Citizen cit = Kingdoms.getCitizen(promoted.getName());
                    if (!cit.getKingdom().equals(c.getKingdom())) {
                        p.sendMessage("§bPlayer must be in your kingdom.");
                        return true;
                    }
                    String cRank = "Warden";
                    if (Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        cRank = "Owner";
                    }
                    String citRank = "Resident";
                    if (Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        citRank = "Owner";
                    } else if (Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString())) {
                        citRank = "Warden";
                    }
                    if (cRank.equals("Owner") && citRank.equals("Warden")) {
                        p.sendMessage("§bDo you want to step down as Owner of your Kingdom?");
                        p.sendMessage("§bYou have 15 seconds to /accept or /deny!");
                        pendingStepDowns.put(p.getName(), promoted.getName());
                        Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
                            @Override
                            public void run(){
                                pendingStepDowns.remove(p.getName());
                            }
                        }, 300L);
                        return true;
                    } else if (cRank.equals("Owner") && citRank.equals("Resident")) {
                        Kingdoms.getKingdom(c.getKingdom()).addWarden(promoted.getUniqueId().toString());
                        promoted.sendMessage("§bYou have been promoted to Warden!");
                        p.sendMessage("§bYou have promoted " + promoted.getName() + " to Warden!");
                    }

                } else if (args[0].equalsIgnoreCase("demote")) {
                    if (c.getKingdom() == null || c.getKingdom().equals("")) {
                        p.sendMessage("§bYou must be the owner of a kingdom to demote a player.");
                        return true;
                    }
                    if (!Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                        p.sendMessage("§bYou must be the owner of a kingdom to demote a player.");
                        return true;
                    }
                    if (args.length != 2) {
                        p.sendMessage("§b/kingdom demote [player]");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player demoted = Bukkit.getPlayer(args[1]);
                        Citizen cit = Kingdoms.getCitizen(demoted.getName());
                        if (Bukkit.getPlayer(args[1]).getName().equals(p.getName())) {
                            p.sendMessage("§bYou cannot demote yourself!");
                            return true;
                        }
                        if (!cit.getKingdom().equals(c.getKingdom())) {
                            p.sendMessage("§bPlayer must be in your kingdom.");
                            return true;
                        }
                        if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(demoted.getUniqueId().toString())) {
                            p.sendMessage("§bCannot demote a resident.");
                            return true;
                        }
                        Kingdoms.getKingdom(c.getKingdom()).removeWarden(demoted.getUniqueId().toString());
                        p.sendMessage("§bSuccessfully demoted " + demoted.getName());
                        demoted.sendMessage("§bYou were demoted.");
                    } else {
                        for (String uuid : Kingdoms.getKingdom(c.getKingdom()).getWardens()) {
                            if (args[1].equals(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName())) {
                                OfflinePlayer demoted = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                Kingdoms.getKingdom(c.getKingdom()).removeWarden(demoted.getUniqueId().toString());
                                p.sendMessage("§bSuccessfully demoted " + demoted.getName());
                                return true;
                            }
                        }
                        p.sendMessage("§b" + args[1] + " is not a Warden in your kingdom.");
                        return true;
                    }
                    //Todo Implement /kingdom flag
                }  else if (args[0].equalsIgnoreCase("flag")) {
                    if (args.length < 3) {
                        p.sendMessage("§b/kingdom flag [add/remove] [pvp/pve/open]");
                        return true;
                    }
                    if (!args[1].equals("add") && !args[1].equals("remove")) {
                        p.sendMessage("§b/kingdom flag [add/remove] [pvp/pve/open]");
                        return true;
                    }
                    if (args[1].equals("add") && (args[2].equals("pvp") | args[2].equals("pve") | args[2].equals("open"))) {
                        if (c.getKingdom() == null || c.getKingdom().equals("")) {
                            p.sendMessage("§bYou must be the owner or warden of a kingdom to add a flag.");
                            return true;
                        }
                        if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                            p.sendMessage("§bYou must be the owner or warden of a kingdom to add a flag.");
                            return true;
                        }
                        Kingdoms.getKingdom(c.getKingdom()).addFlag(args[2]);
                        p.sendMessage("§bFlag added.");
                    } else if (args[1].equals("remove") && (args[2].equals("pvp") | args[2].equals("pve") | args[2].equals("open"))) {
                        if (c.getKingdom() == null || c.getKingdom().equals("")) {
                            p.sendMessage("§bYou must be the owner or warden of a kingdom to remove a flag.");
                            return true;
                        }
                        if (!Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId().toString()) && !Kingdoms.getKingdom(c.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                            p.sendMessage("§bYou must be the owner or warden of a kingdom to remove a flag.");
                            return true;
                        }
                        Kingdoms.getKingdom(c.getKingdom()).removeFlag(args[2]);
                        p.sendMessage("§bFlag removed.");
                    } else {
                        p.sendMessage("§b/kingdom flag [add/remove] [pvp/pve/open]");
                        return true;
                    }

                } else if (args[0].equalsIgnoreCase("info")) {
                    if (args.length < 2) {
                        if (c.getKingdom() == null || c.getKingdom().equals("")) {
                            p.sendMessage("§b/kingdom info [kingdom]");
                            return true;
                        }
                        Kingdom k = Kingdoms.getKingdom(c.getKingdom());
                        String wardenString = "";
                        for (String uuid : k.getWardens()) {
                            wardenString = wardenString + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";
                        }
                        if (wardenString.length() > 2) {
                            wardenString = wardenString.substring(0, wardenString.length() - 2);
                        }
                        String residentString = "";
                        for (String uuid : k.getResidents()) {
                            if (!k.getWardens().contains(uuid) && !k.getOwner().equals(uuid)) {
                                residentString = residentString + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";
                            }
                        }
                        if (residentString.length() > 2) {
                            residentString = residentString.substring(0, residentString.length() - 2);
                        }
                        String flagString = "";
                        for (String flag : k.getFlags()) {
                            flagString = flagString + flag + ", ";
                        }
                        if (flagString.length() > 2) {
                            flagString = flagString.substring(0, flagString.length() - 2);
                        }
                        p.sendMessage("§3-----------------------------------");
                        p.sendMessage("§9Kingdom: §b" + k.getName());
                        p.sendMessage("§9Owner: §b" + Bukkit.getOfflinePlayer(UUID.fromString(k.getOwner())).getName());
                        p.sendMessage("§9Wardens: §b" + wardenString);
                        p.sendMessage("§9Residents: §b" + residentString);
                        p.sendMessage("§9Flags: §b" + flagString);
                        p.sendMessage("§9Chunks: §b" + k.getChunks().size());
                        p.sendMessage("§3-----------------------------------");
                        return true;
                    } else {
                        if (Kingdoms.getKingdom(args[1]) == null) {
                            p.sendMessage("§b" + args[1] + " is not a valid kingdom.");
                            return true;
                        }
                        Kingdom k = Kingdoms.getKingdom(args[1]);
                        String wardenString = "";
                        for (String uuid : k.getWardens()) {
                            wardenString = wardenString + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";
                        }
                        if (wardenString.length() > 2) {
                            wardenString = wardenString.substring(0, wardenString.length() - 2);
                        }
                        String residentString = "";
                        for (String uuid : k.getResidents()) {
                            if (!k.getWardens().contains(uuid) && !k.getOwner().equals(uuid)) {
                                 residentString = residentString + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";
                            }
                        }
                        if (residentString.length() > 2) {
                            residentString = residentString.substring(0, residentString.length() - 2);
                        }
                        String flagString = "";
                        for (String flag : k.getFlags()) {
                            flagString = flagString + flag + ", ";
                        }
                        if (flagString.length() > 2) {
                            flagString = flagString.substring(0, flagString.length() - 2);
                        }
                        p.sendMessage("§3-----------------------------------");
                        p.sendMessage("§9Kingdom: §b" + k.getName());
                        p.sendMessage("§9Owner: §b" + Bukkit.getOfflinePlayer(UUID.fromString(k.getOwner())).getName());
                        p.sendMessage("§9Wardens: §b" + wardenString);
                        p.sendMessage("§9Residents: §b" + residentString);
                        p.sendMessage("§9Flags: §b" + flagString);
                        p.sendMessage("§9Chunks: §b" + k.getChunks().size());
                        p.sendMessage("§3-----------------------------------");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    String kingdomString = "";
                    for (String k : Kingdoms.getKingdoms()) {
                        kingdomString = kingdomString + k + ", ";
                    }
                    if (kingdomString.length() > 2) {
                        kingdomString = kingdomString.substring(0, kingdomString.length() - 2);
                    }
                    p.sendMessage("§9Kingdoms: §b" + kingdomString);
                    return true;
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    p.sendMessage("§b/kingdom create [name] §9- Creates a new kingdom called [name]");
                    p.sendMessage("§b/kingdom claim §9- Claim current chunk");
                    p.sendMessage("§b/kingdom add [player] §9- Invite [player] to your kingdom");
                    p.sendMessage("§b/kingdom remove [player] §9- Remove [player] from your kingdom");
                    p.sendMessage("§b/kingdom promote [resident] §9- Promote resident to warden");
                    p.sendMessage("§b/kingdom demote [warden] §9- Demote a warden");
                    p.sendMessage("§b/kingdom leave §9- Leave your kingdom");
                    p.sendMessage("§b/kingdom delete §9- Delete your kingdom");
                    p.sendMessage("§b/kingdom info [kingdom] §9- Print information about [kingdom]");
                    p.sendMessage("§b/kingdom list §9- Print a list of all kingdoms");


                }
            } else {
                p.sendMessage("§b/kingdom create [name] §9- Creates a new kingdom called [name]");
                p.sendMessage("§b/kingdom claim §9- Claim current chunk");
                p.sendMessage("§b/kingdom add [player] §9- Invite [player] to your kingdom");
                p.sendMessage("§b/kingdom remove [player] §9- Remove [player] from your kingdom");
                p.sendMessage("§b/kingdom promote [resident] §9- Promote resident to warden");
                p.sendMessage("§b/kingdom demote [warden] §9- Demote a warden");
                p.sendMessage("§b/kingdom leave §9- Leave your kingdom");
                p.sendMessage("§b/kingdom delete §9- Delete your kingdom");
                p.sendMessage("§b/kingdom info [kingdom] §9- Print information about [kingdom]");
                p.sendMessage("§b/kingdom list §9- Print a list of all kingdoms");


            }
        } else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("accept") ) {
            Player p = (Player) sender;
            Citizen c = Kingdoms.getCitizen(p.getName());
            if (pendingStepDowns.containsKey(p.getName())) {
                if (Bukkit.getPlayer(pendingStepDowns.get(p.getName())) != null) {
                    Player promoted = Bukkit.getPlayer(pendingStepDowns.get(p.getName()));
                    Kingdom k = Kingdoms.getKingdom(c.getKingdom());
                    if (k.getOwner().equals(p.getUniqueId().toString()) && k.getWardens().contains(promoted.getUniqueId().toString())) {
                        k.setOwner(promoted.getUniqueId().toString());
                        k.addWarden(p.getUniqueId().toString());
                        k.removeWarden(promoted.getUniqueId().toString());
                        promoted.sendMessage("§bYou have been promoted to Owner of " + c.getKingdom());
                        p.sendMessage("§bYou have been demoted to Warden.");
                        return true;
                    }
                } else {
                    OfflinePlayer promoted = Bukkit.getPlayer(pendingStepDowns.get(p.getName()));
                    Kingdom k = Kingdoms.getKingdom(c.getKingdom());
                    if (k.getOwner().equals(p.getUniqueId().toString()) && k.getWardens().contains(promoted.getUniqueId().toString())) {
                        k.setOwner(promoted.getUniqueId().toString());
                        k.addWarden(p.getUniqueId().toString());
                        k.removeWarden(promoted.getUniqueId().toString());
                        p.sendMessage("§bYou have been demoted to Warden.");
                        return true;
                    }
                }
            } else if (pendingDeletes.containsKey(p.getName())) {
                Kingdoms.getKingdom(c.getKingdom()).delete();
                c.setKingdom("");
                p.sendMessage("§bYou have deleted your kingdom.");
                pendingDeletes.remove(p.getName());
                return true;
            } else if (pendingLeaves.containsKey(p.getName())) {
                Kingdoms.getKingdom(c.getKingdom()).removeResident(p.getUniqueId().toString());
                if (Kingdoms.getKingdom(c.getKingdom()).getWardens().contains(p.getUniqueId())) {
                    Kingdoms.getKingdom(c.getKingdom()).removeWarden(p.getUniqueId().toString());
                }
                c.setKingdom("");
                for (String uuid : Kingdoms.getKingdom(pendingLeaves.get(p.getName())).getResidents()) {
                    if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
                        Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§b" + p.getName() + " has left the kingdom.");
                    }
                }
                p.sendMessage("§bYou have left the Kingdom of " + pendingLeaves.get(p.getName()) + "!");
                pendingLeaves.remove(p.getName());
            } else if (pendingInvites.containsKey(p.getName())) {
                c.setKingdom(pendingInvites.get(p.getName()));
                Kingdoms.getKingdom(pendingInvites.get(p.getName())).addResident(p);
                p.sendMessage("§bYou have joined the Kingdom of " + pendingInvites.get(p.getName()) + "!");
                for (String uuid : Kingdoms.getKingdom(pendingInvites.get(p.getName())).getWardens()) {
                    if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
                        Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§b" + p.getName() + " has joined " + pendingInvites.get(p.getName()) + "!");
                    }
                }
                if (Bukkit.getPlayer(UUID.fromString(Kingdoms.getKingdom(pendingInvites.get(p.getName())).getOwner())) != null) {
                    Bukkit.getPlayer(UUID.fromString(Kingdoms.getKingdom(pendingInvites.get(p.getName())).getOwner())).sendMessage("§b" + p.getName() + " has joined " + pendingInvites.get(p.getName()) + "!");
                }
                pendingInvites.remove(p.getName());
            } else {
                p.sendMessage("§bNo pending invites.");
            }
        } else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("deny") ) {
            Player p = (Player) sender;
            Citizen c = Kingdoms.getCitizen(p.getName());
            if (pendingDeletes.containsKey(p.getName())) {
                p.sendMessage("§bYou have chosen not to delete your kingdom.");
                pendingDeletes.remove(p.getName());
            } else if (pendingLeaves.containsKey(p.getName())) {
                p.sendMessage("§bYou have chosen not to leave the kingdom.");
                pendingLeaves.remove(p.getName());
            } else if (pendingInvites.containsKey(p.getName())) {
                p.sendMessage("§bYou have declined the invitation to join " + pendingInvites.get(p.getName()) + "!");
                for (String uuid : Kingdoms.getKingdom(pendingInvites.get(p.getName())).getWardens()) {
                    if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
                        Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§b" + p.getName() + " has declined the invitation.");
                    }
                }
                if (Bukkit.getPlayer(UUID.fromString(Kingdoms.getKingdom(pendingInvites.get(p.getName())).getOwner())) != null) {
                    Bukkit.getPlayer(UUID.fromString(Kingdoms.getKingdom(pendingInvites.get(p.getName())).getOwner())).sendMessage("§b" + p.getName() + " has declined the invitation.");
                }
                pendingInvites.remove(p.getName());
            } else {
                p.sendMessage("§bNo pending invites.");
            }
        }
        return true;
    }

}
