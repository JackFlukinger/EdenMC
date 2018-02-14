package org.edenmc.kingdoms.customitems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.edenmc.kingdoms.Kingdoms;

import java.util.Map;

/**
 * Created by Jack on 2/11/2018.
 */
public class CustomItemListener implements Listener {

    //For swords/bows
    @EventHandler(ignoreCancelled=true)
    public void hitEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            e.setDamage(e.getDamage() + getDamage(e.getDamager()));
            if (!CustomItemMobSpawnUtil.getSpawnerMobs().contains(e.getEntity().getUniqueId())) {
                if (((LivingEntity) e.getEntity()).getHealth() - (e.getDamage()) > 0.0) {
                    onHit((LivingEntity) e.getEntity(), e.getDamager(), 1);
                } else {
                    onHit((LivingEntity) e.getEntity(), e.getDamager(), Kingdoms.getCIConf().getExpPerMobKill((LivingEntity) e.getEntity()));
                }
                Bukkit.broadcastMessage("Damage: " + e.getDamage());
            }
        }
    }

    //For tools
    @EventHandler(ignoreCancelled=true)
    public void blockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (Kingdoms.getCIConf().getAffectedItems().contains(item.getType().toString())) {
            if (Kingdoms.getCIConf().getRareDropBlocks().get(item.getType().toString()).contains(e.getBlock().getType().toString()) | Kingdoms.getCIConf().getRareDropBlocks().get(item.getType().toString()).contains(e.getBlock().getType().toString() + "-" + String.valueOf(e.getBlock().getData()))) {
                if (Kingdoms.getCIConf().getModifierType(item).equals("luck")) {
                    CustomItem ci = new CustomItem(item);
                    int Luck = (int) ((ci.level - 1) * Kingdoms.getCIConf().getMultiplier(item));
                    if (Math.random() * 200 <= Luck) {
                        Bukkit.broadcastMessage("1");
                        ItemStack ItemToDrop = new ItemStack(getDrop(item));
                        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), ItemToDrop);
                    }
                    ci.addEXP(10);
                    item.setItemMeta(ci.create().getItemMeta());
                }
            }
        }
    }

    //Run on hit
    public static void onHit(LivingEntity damagedEntity, Entity damager, int exp) {
        if (damager instanceof Player | damager instanceof Arrow) {
            //If damager is player
            if (damager instanceof Player) {
                //If item entity is hit with is affected by CastleCraftCustomItems
                if (((Player) damager).getInventory().getItemInMainHand().getType() != Material.BOW && Kingdoms.getCIConf().getAffectedItems().contains(((Player) damager).getInventory().getItemInMainHand().getType().toString())) {
                    ItemStack item = ((Player) damager).getInventory().getItemInHand();
                    if (Kingdoms.getCIConf().getModifierType(item).equals("damage")) {
                        CustomItem ci = new CustomItem(item);
                        //If entity will die, give exp
                        ci.addEXP(exp);
                        item.setItemMeta(ci.create().getItemMeta());

                    }
                }
            } else if (damager instanceof Arrow) {
                Player damagerPlayer;
                //If shooter is a player, continue. Otherwise, quit.
                if (((Arrow) damager).getShooter() instanceof Player) {
                    damagerPlayer = (Player) ((Arrow) damager).getShooter();
                } else {
                    return;
                }
                //If item hit with is affected by CastleCraftCustomItems
                if (Kingdoms.getCIConf().getAffectedItems().contains(damagerPlayer.getInventory().getItemInHand().getType().toString())) {
                    ItemStack item = damagerPlayer.getInventory().getItemInMainHand();
                    if (Kingdoms.getCIConf().getModifierType(item).equals("damage")) {
                        CustomItem ci = new CustomItem(item);
                        ci.addEXP(exp);
                        item.setItemMeta(ci.create().getItemMeta());
                    }
                }
            }
        }
    }


    public static Double getDamage(Entity e) {
        if (e instanceof Player | e instanceof Arrow) {
            if (e instanceof Player) {
                if (Kingdoms.getCIConf().getAffectedItems().contains(((Player) e).getInventory().getItemInMainHand().getType().toString())) {
                    return ((Kingdoms.getCIConf().getLevel(((Player) e).getInventory().getItemInMainHand()) - 1) * Kingdoms.getCIConf().getMultiplier(((Player) e).getInventory().getItemInHand()));
                }
            } else if (e instanceof Arrow) {
                if (((Arrow) e).getShooter() instanceof Player) {
                    if (Kingdoms.getCIConf().getAffectedItems().contains(((Player) ((Arrow) e).getShooter()).getItemInHand().getType().toString())) {
                        return ((Kingdoms.getCIConf().getLevel(((Player) ((Arrow) e).getShooter()).getInventory().getItemInHand()) * Kingdoms.getCIConf().getMultiplier(((Player) ((Arrow) e).getShooter()).getInventory().getItemInHand())));
                    }
                }
            }
        }
        return 0.0;
    }

    public static Material getDrop(ItemStack item) {
        Map<Integer, String> rareDrops = Kingdoms.getCIConf().getRareDrops().get(item.getType().toString());
        Material itemToDrop = Material.AIR;
        int randomMax = 0;
        for (int biggestNum : rareDrops.keySet()) {
            if (biggestNum > randomMax) {
                randomMax = biggestNum;
            }
        }
        //This is the random value that determines the drop
        int randomTicket = (int) (Math.random() * randomMax);
        for (int randomDropValue : rareDrops.keySet()) {
            if (randomTicket < randomDropValue) {
                itemToDrop = Material.getMaterial(rareDrops.get(randomDropValue));
                Bukkit.broadcastMessage(itemToDrop.toString());
                break;
            }
        }
        return itemToDrop;
    }

    //Fairly redundant method to make check if item has lore. Makes stuff easier to look at :P
    public static boolean hasLore(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            return true;
        } else {
            return false;
        }
    }
}
