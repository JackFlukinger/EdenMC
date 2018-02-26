package org.edenmc.kingdoms.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jack on 2/25/2018.
 */
public class KingdomListener implements Listener {
    static ArrayList<String> cooldown = new ArrayList<String>();


    @EventHandler
    public static void blockBreakEvent(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Citizen c = Kingdoms.getCitizen(p.getName());
        if (!p.hasPermission("kingdoms.*") && Kingdoms.getChunks().containsKey(e.getBlock().getLocation().getChunk().getX() + " " + e.getBlock().getLocation().getChunk().getZ())) {
            KingdomChunk ch = Kingdoms.getChunks().get(e.getBlock().getLocation().getChunk().getX() + " " + e.getBlock().getLocation().getChunk().getZ());
            if (ch.getOwner() == null | ch.getOwner().equals("")) {
                if (ch.getFlags().contains("locked") && !Kingdoms.getKingdom(ch.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                    e.setCancelled(true);
                    return;
                }
                if (ch.getKingdom().equals(c.getKingdom())) {
                    return;
                }
            } else if (!ch.getMembers().contains(p.getUniqueId().toString()) && !ch.getOwner().equals(p.getUniqueId().toString())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public static void interactEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Citizen c = Kingdoms.getCitizen(p.getName());
        if (!p.hasPermission("kingdoms.*") && e.getClickedBlock() != null && Kingdoms.getChunks().containsKey(e.getClickedBlock().getLocation().getChunk().getX() + " " + e.getClickedBlock().getLocation().getChunk().getZ())) {
            KingdomChunk ch = Kingdoms.getChunks().get(e.getClickedBlock().getLocation().getChunk().getX() + " " + e.getClickedBlock().getLocation().getChunk().getZ());
            if (ch.getOwner() == null | ch.getOwner().equals("")) {
                if (ch.getFlags().contains("locked") && !Kingdoms.getKingdom(ch.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                    e.setCancelled(true);
                    return;
                }
                if (ch.getKingdom().equals(c.getKingdom())) {
                    return;
                }
            }
            if (!ch.getMembers().contains(p.getUniqueId().toString()) && !ch.getOwner().equals(p.getUniqueId().toString())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public static void blockBurn(BlockBurnEvent e) {
        if (Kingdoms.getChunks().containsKey(e.getBlock().getChunk().getX() + " " + e.getBlock().getChunk().getZ())) {
            if (e.getIgnitingBlock() != null) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public static void blockIgnite(BlockIgniteEvent e) {
        if (Kingdoms.getChunks().containsKey(e.getBlock().getChunk().getX() + " " + e.getBlock().getChunk().getZ())) {
            KingdomChunk ch = Kingdoms.getChunks().get(e.getBlock().getChunk().getX() + " " + e.getBlock().getChunk().getZ());
            if (e.getBlock().getType() == Material.TNT) {
                e.setCancelled(true);
                return;
            }
            if (e.getIgnitingBlock() != null) {
                e.setCancelled(true);
                return;
            }
            if (e.getPlayer() != null) {
                Player p = e.getPlayer();
                if (Kingdoms.getKingdom(ch.getKingdom()).getOwner().equals(p.getUniqueId().toString())) {
                    return;
                }
                if (ch.getFlags().contains("locked")) {
                    e.setCancelled(true);
                }
                if ((ch.getOwner() == null | !ch.getOwner().equals(p.getUniqueId().toString())) && !ch.getMembers().contains(p.getUniqueId().toString())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public static void entityExplode(EntityExplodeEvent e) {
        if (e.getEntityType().equals(EntityType.PRIMED_TNT) | e.getEntityType().equals(EntityType.CREEPER) | e.getEntityType().equals(EntityType.FIREBALL) | e.getEntityType().equals(EntityType.SMALL_FIREBALL) | e.getEntityType().equals(EntityType.MINECART_TNT) | e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
            if (Kingdoms.getChunks().containsKey(e.getLocation().getChunk().getX() + " " + e.getLocation().getChunk().getZ())) {
                e.setCancelled(true);
            } else {
                for (Block b : e.blockList()) {
                    if (Kingdoms.getChunks().containsKey(b.getChunk().getX() + " " + b.getChunk().getZ())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
        List<Block> blocks = e.blockList();
        if (!e.isCancelled()) {
            if (e.getEntityType().equals(EntityType.PRIMED_TNT) | e.getEntityType().equals(EntityType.CREEPER) | e.getEntityType().equals(EntityType.FIREBALL) | e.getEntityType().equals(EntityType.SMALL_FIREBALL) | e.getEntityType().equals(EntityType.MINECART_TNT) | e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
                for (final Block b : blocks) {
                    if (b.getType() == Material.DIRT | b.getType() == Material.SAND | b.getType() == Material.MYCEL | b.getType() == Material.GRASS | b.getType() == Material.ICE | b.getType() == Material.SNOW_BLOCK) {
                        final Material type = b.getType();
                        long LOWER_RANGE = 400; //assign lower range value
                        long UPPER_RANGE = 1200; //assign upper range value
                        Random random = new Random();
                        long randomValue = LOWER_RANGE + (long)(random.nextDouble()*(UPPER_RANGE - LOWER_RANGE));
                        Bukkit.getServer().getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable() {
                            public void run() {
                                b.setType(type);
                            }
                        }, randomValue);
                    }
                }
            }
        }
    }


    @EventHandler
    public static void entityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                if (e.getDamager() instanceof Player) {
                    if (Kingdoms.getKingdom(ch.getKingdom()).getFlags().contains("pvp")) {
                        return;
                    }
                    e.setCancelled(true);
                    return;
                } else if (e.getDamager() instanceof Arrow) {
                    if (((Arrow) e.getDamager()).getShooter() instanceof Player) {
                        if (Kingdoms.getKingdom(ch.getKingdom()).getFlags().contains("pvp")) {
                            return;
                        }
                        e.setCancelled(true);
                        return;
                    } else if (((Arrow) e.getDamager()).getShooter() instanceof Entity) {
                        if (Kingdoms.getKingdom(ch.getKingdom()).getFlags().contains("pve")) {
                            return;
                        }
                        e.setCancelled(true);
                        return;
                    }
                } else if (e.getDamager() instanceof LivingEntity) {
                    if (Kingdoms.getKingdom(ch.getKingdom()).getFlags().contains("pve")) {
                        return;
                    }
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public static void targetEvent(EntityTargetEvent e) {
        if (e.getEntity() instanceof Monster) {
            if (e.getTarget() instanceof Player) {
                Player p = (Player) e.getTarget();
                if (Kingdoms.getChunks().containsKey(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ())) {
                    KingdomChunk ch = Kingdoms.getChunks().get(p.getLocation().getChunk().getX() + " " + p.getLocation().getChunk().getZ());
                    if (Kingdoms.getKingdom(ch.getKingdom()).getFlags().contains("pve")) {
                        return;
                    }
                    e.setTarget(null);
                    return;
                }
            }
        }
    }



    @EventHandler
    public static void mobSpawnEvent(EntitySpawnEvent e) {
        if (e.getEntity() instanceof Monster) {
            if (Kingdoms.getChunks().containsKey(e.getLocation().getChunk().getX() + " " + e.getLocation().getChunk().getZ())) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
