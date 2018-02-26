package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.edenmc.kingdoms.MySQL;

import java.util.ArrayList;

/**
 * Created by Jack on 2/14/2018.
 */
public class KingdomChunk {
    Chunk chunk;
    World world;
    String kingdom;
    String owner;
    ArrayList<String> members;
    ArrayList<String> flags = new ArrayList<String>();

    public KingdomChunk(Chunk ck, String kg, String ow, ArrayList<String> mems, ArrayList<String> fl) {
        world = ck.getWorld();
        chunk = ck;
        kingdom = kg;
        owner = ow;
        members = mems;
        flags = fl;

    }

    public String getKingdom() {
        return kingdom;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void addMember(String member) {
        if (members.contains(member)) {
            return;
        }
        members.add(member);
        String memString = "";
        for (String m : members) {
            memString = memString + m + ",";
        }
        memString = memString.substring(0,memString.length() - 1);
        String[] data = {chunk.getX() + " " + chunk.getZ(), memString};
        String[] columns = {"chunk", "members"};
        MySQL.enterData("chunks", columns, data);
    }

    public void removeMember(String member) {
        if (!members.contains(member)) {
            return;
        }
        members.remove(member);
        String memString = "";
        for (String m : members) {
            memString = memString + m + ",";
        }
        if (memString.length() >= 1) {
            memString = memString.substring(0, memString.length() - 1);
        }
        String[] data = {chunk.getX() + " " + chunk.getZ(), memString};
        String[] columns = {"chunk", "members"};
        MySQL.enterData("chunks", columns, data);
    }

    public void clearMembers() {
        members.clear();
        String memString = "";
        String[] data = {chunk.getX() + " " + chunk.getZ(), memString};
        String[] columns = {"chunk", "members"};
        MySQL.enterData("chunks", columns, data);
    }

    public ArrayList<String> getFlags() {
        return flags;
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
        String[] data = {chunk.getX() + " " + chunk.getZ(), flagString};
        String[] columns = {"chunk", "flags"};
        MySQL.enterData("chunks", columns, data);
    }

    public void setOwner(String uuid) {
        owner = uuid;
        String[] data = {chunk.getX() + " " + chunk.getZ(), uuid};
        String[] columns = {"chunk", "owner"};
        MySQL.enterData("chunks", columns, data);
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
        String[] data = {chunk.getX() + " " + chunk.getZ(), flagString};
        String[] columns = {"chunk", "flags"};
        MySQL.enterData("chunks", columns, data);
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
