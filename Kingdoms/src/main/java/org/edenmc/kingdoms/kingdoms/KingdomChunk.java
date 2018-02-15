package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
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
    ArrayList<String> flags = new ArrayList<String>();

    public KingdomChunk(String ck) {
        world = loadWorld();
        chunk = world.getChunkAt(Integer.parseInt(ck.split(" ")[0]), Integer.parseInt(ck.split(" ")[1]));
        kingdom = loadKingdom();
        flags = loadFlags();

    }

    private World loadWorld() {
        return Bukkit.getServer().getWorld(MySQL.getData("chunks", "chunk", "world", String.valueOf(chunk.getX()) + " " + String.valueOf(chunk.getZ())));
    }

    private String loadKingdom() {
        return MySQL.getData("chunks", "chunk", "kingdom", String.valueOf(chunk.getX()) + " " + String.valueOf(chunk.getZ()));
    }

    private ArrayList<String> loadFlags() {
        String flagString = MySQL.getData("chunks", "chunk", "flags", String.valueOf(chunk.getX()) +" " + String.valueOf(chunk.getZ()));
        ArrayList<String> flagArray = new ArrayList<String>();
        for (String flag : flagString.split(",")) {
            flagArray.add(flag);
        }
        return flagArray;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
