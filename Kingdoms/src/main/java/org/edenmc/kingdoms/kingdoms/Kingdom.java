package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.MySQL;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;

/**
 * Created by Jack on 2/11/2018.
 */
public class Kingdom implements Traversable{

    String kingdom;
    String owner;
    ArrayList<String> residents;
    ArrayList<String> wardens;
    ArrayList<String> flags;
    ArrayList<KingdomChunk> chunks;


    public Kingdom(String name, String ow, ArrayList<KingdomChunk> ch, ArrayList<String> wr, ArrayList<String> res, ArrayList<String> fl) {
        kingdom = name;
        owner = ow;
        chunks = ch;
        wardens = wr;
        residents = res;
        flags = fl;
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

    public void removeWarden(String uuid) {
        wardens.remove(uuid);
        String warString = "";
        for (String war : wardens) {
            warString = warString + war + ",";
        }
        warString = warString.substring(0,warString.length() - 1);
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

    @Override
    public boolean movedOnLand(Citizen c) {
        if (onLand(c.getPlayer().getLocation())) {
            return true;
        }
        return false;
    }

    public boolean onLand(Location loc) {
        if (chunks.contains(loc.getChunk())) {
            return true;
        }
        return false;
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
