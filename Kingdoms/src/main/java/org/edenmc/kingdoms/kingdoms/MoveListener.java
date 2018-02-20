package org.edenmc.kingdoms.kingdoms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.edenmc.kingdoms.Kingdoms;

/**
 * Created by Jack on 2/19/2018.
 */
public class MoveListener implements Listener {

    @EventHandler
    public static void CheckChunkOnMove(PlayerMoveEvent e) {
        if (e.getFrom().getChunk() != e.getTo().getChunk()) {
            if (Kingdoms.getChunks().keySet().contains(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ())) {
                if (!Kingdoms.getChunks().keySet().contains(e.getFrom().getChunk().getX() + " " + e.getFrom().getChunk().getZ())) {
                    e.getPlayer().sendTitle("","§bYou have entered " + Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getKingdom(), 10, 20, 30);
                }
            } else if (Kingdoms.getChunks().keySet().contains(e.getFrom().getChunk().getX() + " " + e.getFrom().getChunk().getZ())) {
                if (!Kingdoms.getChunks().keySet().contains(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ())) {
                    e.getPlayer().sendTitle("","§bYou have entered the §2Wilderness", 10, 20, 30);
                }
            }
        }
    }
}
