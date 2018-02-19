package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Created by Jack on 2/14/2018.
 */
public class KingdomChunk {
    Chunk chunk;
    World world;
    String kingdom;
    String owner;
    ArrayList<String> flags = new ArrayList<String>();

    public KingdomChunk(Chunk ck, String kg, String ow, ArrayList<String> fl) {
        world = ck.getWorld();
        chunk = ck;
        kingdom = kg;
        owner = ow;
        flags = fl;

    }

    public String getKingdom() {
        return kingdom;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
