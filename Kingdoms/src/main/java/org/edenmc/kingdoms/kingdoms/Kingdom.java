package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Location;
import org.edenmc.kingdoms.MySQL;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jack on 2/11/2018.
 */
public class Kingdom implements Traversable{

    String kingdom;
    String owner;
    ArrayList<UUID> residents;
    ArrayList<KingdomChunk> chunks;


    public Kingdom(String name) {
        kingdom = name;
        owner = loadOwner();
        chunks = loadChunks();
    }

    private String loadOwner() {
        String string = MySQL.getData("kingdoms","kingdom","owner",kingdom);
        return string;
    }

    private ArrayList<KingdomChunk> loadChunks() {

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

    public String getOwner() {
        return owner;
    }

    public ArrayList<UUID> getResidents() {
        return residents;
    }

}
