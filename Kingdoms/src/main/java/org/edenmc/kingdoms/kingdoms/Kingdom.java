package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jack on 2/11/2018.
 */
public class Kingdom implements Traversable{

    String name;
    String owner;
    ArrayList<UUID> residents;
    ArrayList<Chunk> chunks;


    public Kingdom() {


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

    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<UUID> getResidents() {
        return residents;
    }

}
