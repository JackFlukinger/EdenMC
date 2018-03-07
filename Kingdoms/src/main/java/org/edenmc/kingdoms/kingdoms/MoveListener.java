package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.edenmc.kingdoms.Kingdoms;

import java.util.UUID;

/**
 * Created by Jack on 2/19/2018.
 */
public class MoveListener implements Listener {

    @EventHandler
    public static void CheckChunkOnMove(PlayerMoveEvent e) {
        if (e.getFrom().getChunk() != e.getTo().getChunk()) {
            if (Kingdoms.getChunks().keySet().contains(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ())) {
                if (!Kingdoms.getChunks().keySet().contains(e.getFrom().getChunk().getX() + " " + e.getFrom().getChunk().getZ())) {
                    if (Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner() != null && !Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner().equals("")) {
                        String chunkOwner = Bukkit.getOfflinePlayer(UUID.fromString(Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner())).getName();
                        e.getPlayer().sendTitle("§b" + Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getKingdom(), "§3" + chunkOwner, 10, 20, 30);
                    } else {
                        e.getPlayer().sendTitle("§b" + Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getKingdom(), "", 10, 20, 30);
                    }
                } else {
                    if (Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner() != null && !Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner().equals("")) {
                        String chunkOwner = Bukkit.getOfflinePlayer(UUID.fromString(Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner())).getName();
                        e.getPlayer().sendTitle("", "§3" + chunkOwner, 10, 20, 30);
                    }
                }
            } else if (Kingdoms.getChunks().keySet().contains(e.getFrom().getChunk().getX() + " " + e.getFrom().getChunk().getZ())) {
                if (!Kingdoms.getChunks().keySet().contains(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ())) {
                    e.getPlayer().sendTitle("§2Wilderness","", 10, 20, 30);
                }
            }
        }
        if (KingdomCommands.pendingSpawns.contains(e.getPlayer().getName())) {
            KingdomCommands.pendingSpawns.remove(e.getPlayer().getName());
            e.getPlayer().sendMessage("§bTeleport canceled!");
        }
    }

    @EventHandler
    public static void CheckChunkOnTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getChunk() != e.getTo().getChunk()) {
            if (Kingdoms.getChunks().keySet().contains(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ())) {
                if (!Kingdoms.getChunks().keySet().contains(e.getFrom().getChunk().getX() + " " + e.getFrom().getChunk().getZ())) {
                    if (Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner() != null && !Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner().equals("")) {
                        String chunkOwner = Bukkit.getOfflinePlayer(UUID.fromString(Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner())).getName();
                        e.getPlayer().sendTitle("§b" + Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getKingdom(), "§3" + chunkOwner, 10, 20, 30);
                    } else {
                        e.getPlayer().sendTitle("§b" + Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getKingdom(), "", 10, 20, 30);
                    }
                } else {
                    if (Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner() != null && !Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner().equals("")) {
                        String chunkOwner = Bukkit.getOfflinePlayer(UUID.fromString(Kingdoms.getChunks().get(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ()).getOwner())).getName();
                        e.getPlayer().sendTitle("", "§3" + chunkOwner, 10, 20, 30);
                    }
                }
            } else if (Kingdoms.getChunks().keySet().contains(e.getFrom().getChunk().getX() + " " + e.getFrom().getChunk().getZ())) {
                if (!Kingdoms.getChunks().keySet().contains(e.getTo().getChunk().getX() + " " + e.getTo().getChunk().getZ())) {
                    e.getPlayer().sendTitle("§2Wilderness","", 10, 20, 30);
                }
            }
        }
    }
}
