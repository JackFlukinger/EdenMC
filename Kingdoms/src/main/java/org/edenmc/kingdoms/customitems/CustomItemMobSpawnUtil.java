package org.edenmc.kingdoms.customitems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jack on 2/11/2018.
 */
public class CustomItemMobSpawnUtil implements Listener {

    //List of UUIDs of mobs spawned  from spawners-prevents exp from spawner mobs. Handy dandy!
    private static List<UUID> spawnerMobs = new ArrayList<UUID>();

    @EventHandler(ignoreCancelled=true)
    public void onCreatureSpawn(CreatureSpawnEvent e){
        if(e.getSpawnReason()== CreatureSpawnEvent.SpawnReason.SPAWNER){
            spawnerMobs.add(e.getEntity().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnedMobDeath(EntityDeathEvent e) {
        if (spawnerMobs.contains(e.getEntity().getUniqueId())) {
            spawnerMobs.remove(e.getEntity().getUniqueId());
        }
    }

    public static List<UUID> getSpawnerMobs() {
        return spawnerMobs;
    }
}
