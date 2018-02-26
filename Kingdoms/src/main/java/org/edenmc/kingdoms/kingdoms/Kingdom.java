package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.MySQL;

import java.util.ArrayList;

/**
 * Created by Jack on 2/11/2018.
 */
public class Kingdom {

    String kingdom;
    String owner;
    ArrayList<String> residents;
    ArrayList<String> wardens;
    ArrayList<String> flags;
    ArrayList<KingdomChunk> chunks;
    Location spawn;


    public Kingdom(String name, String ow, ArrayList<KingdomChunk> ch, ArrayList<String> wr, ArrayList<String> res, ArrayList<String> fl, Location loc) {
        kingdom = name;
        owner = ow;
        chunks = ch;
        wardens = wr;
        residents = res;
        flags = fl;
        spawn = loc;
    }

    public void addResident(Player p) {
        residents.add(p.getUniqueId().toString());
        Kingdoms.getCitizen(p.getName()).setKingdom(kingdom);
        String resString = "";
        for (String res : residents) {
            resString = resString + res + ",";
        }
        resString = resString.substring(0,resString.length() - 1);
        String[] data = {kingdom, resString};
        String[] columns = {"kingdom", "residents"};
        MySQL.enterData("kingdoms", columns, data);
    }

    public void removeResident(String uuid) {
        residents.remove(uuid);
        String resString = "";
        for (String res : residents) {
            resString = resString + res + ",";
        }
        resString = resString.substring(0,resString.length() - 1);
        String[] data = {kingdom, resString};
        String[] columns = {"kingdom", "residents"};
        MySQL.enterData("kingdoms", columns, data);
    }

    public void setOwner(String uuid) {
        owner = uuid;
        String[] data = {kingdom, uuid};
        String[] columns = {"kingdom", "owner"};
        MySQL.enterData("kingdoms", columns, data);
    }

    public void addWarden(String uuid) {
        wardens.add(uuid);
        String warString = "";
        for (String war : wardens) {
            warString = warString + war + ",";
        }
        warString = warString.substring(0,warString.length() - 1);
        String[] data = {kingdom, warString};
        String[] columns = {"kingdom", "wardens"};
        MySQL.enterData("kingdoms", columns, data);

    }

    public void removeWarden(String uuid) {
        wardens.remove(uuid);
        String warString = "";
        for (String war : wardens) {
            warString = warString + war + ",";
        }
        if (warString.length() >= 1) {
            warString = warString.substring(0, warString.length() - 1);
        }
        String[] data = {kingdom, warString};
        String[] columns = {"kingdom", "wardens"};
        MySQL.enterData("kingdoms", columns, data);
    }

    public void delete() {
        Kingdoms.removeKingdom(this);
        for (KingdomChunk c : chunks) {
            Kingdoms.removeChunk(c);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Kingdoms.getCitizen(p.getName()).getKingdom() != null && Kingdoms.getCitizen(p.getName()).getKingdom().equals(kingdom)) {
                Kingdoms.getCitizen(p.getName()).setKingdom("");
            }
        }
        for (String uuid : getResidents()) {
            String[] data = {uuid, ""};
            String[] columns = {"uuid", "kingdom"};
            MySQL.enterData("players", columns, data);
        }
        MySQL.delete("kingdoms", "kingdom", getName());
        MySQL.delete("chunks", "kingdom", getName());
    }

    public void setSpawn(Location loc) {
        spawn = loc;
        String locString = loc.getWorld() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        String[] data = {kingdom, locString};
        String[] columns = {"kingdom", "spawn"};
        MySQL.enterData("kingdoms", columns, data);
    }

    public Location getSpawn() {
        return spawn;
    }

    public ArrayList<KingdomChunk> getChunks() {
        return chunks;
    }

    public String getName() {
        return kingdom;
    }

    public void addChunk(KingdomChunk ch) {
        chunks.add(ch);
    }

    public void removeChunk(KingdomChunk ch) {
        chunks.remove(ch);
    }

    public void addFlag(String flag) {
        if (flags.contains(flag)) {
            return;
        }
        flags.add(flag);
        String flagString = "";
        for (String fl : flags) {
            flagString = flagString + fl + ",";
        }
        flagString = flagString.substring(0,flagString.length() - 1);
        String[] data = {kingdom, flagString};
        String[] columns = {"kingdom", "flags"};
        MySQL.enterData("kingdoms", columns, data);
    }
    public void removeFlag(String flag) {
        if (!flags.contains(flag)) {
            return;
        }
        flags.remove(flag);
        String flagString = "";
        for (String fl : flags) {
            flagString = flagString + fl + ",";
        }
        if (flagString.length() >= 1) {
            flagString = flagString.substring(0, flagString.length() - 1);
        }
        String[] data = {kingdom, flagString};
        String[] columns = {"kingdom", "flags"};
        MySQL.enterData("kingdoms", columns, data);
    }
    public String getOwner() {
        return owner;
    }

    public ArrayList<String> getResidents() {
        return residents;
    }

    public ArrayList<String> getWardens() {
        return wardens;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }
}
