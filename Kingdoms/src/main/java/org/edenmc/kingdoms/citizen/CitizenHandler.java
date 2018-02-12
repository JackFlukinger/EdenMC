package org.edenmc.kingdoms.citizen;


import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.edenmc.kingdoms.Kingdoms;

/**
 * Created by Jack on 2/8/2018.
 */
public class CitizenHandler implements Listener {
    @EventHandler (priority= EventPriority.LOWEST)
    public void addCitizenOnJoin(PlayerJoinEvent e) {
        Citizen c = new Citizen(e.getPlayer());
        Kingdoms.setCitizen(c);

    }

    @EventHandler (priority=EventPriority.HIGH)
    public void removeCitizenOnJoin(PlayerQuitEvent e) {
        Kingdoms.removeCitizen(Kingdoms.getCitizen(e.getPlayer().getName()));
    }
}
