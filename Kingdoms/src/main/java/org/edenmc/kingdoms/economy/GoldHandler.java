package org.edenmc.kingdoms.economy;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.HashMap;

/**
 * Created by Jack on 6/20/2017.
 */
public class GoldHandler implements Listener  {
    public static HashMap<Player, Object[]> lastTitle = new HashMap<Player, Object[]>();
    public static HashMap<String, Integer> goldPerMobKill = new HashMap<String,Integer>();



    //Add gold to balance when picked up
    @EventHandler
    public void pickupGold(PlayerPickupItemEvent e) {
        if (GoldFunctions.isGold(e.getItem().getItemStack())) {
            Citizen p = Kingdoms.getCitizen(e.getPlayer().getName());
            Integer amount = e.getItem().getItemStack().getAmount();
            e.setCancelled(true);
            e.getItem().remove();
            p.setBalance(p.getBalance() + amount);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5F,1.0F);
            Integer duration = 30;
            if (lastTitle.containsKey(e.getPlayer()) && (Long) lastTitle.get(e.getPlayer())[0] + (duration * 50) > System.currentTimeMillis()) {
                Integer newAmount = (Integer) lastTitle.get(e.getPlayer())[1] + amount;
                e.getPlayer().sendTitle("", "§b+" + (newAmount) + " Gold Pieces",0,0,duration);
                Object[] titleArray = {System.currentTimeMillis(), newAmount};
                lastTitle.put(e.getPlayer(), titleArray);
            } else {
                e.getPlayer().sendTitle("","§b+" + amount + " Gold Pieces",0,0,duration);
                Object[] titleArray = {System.currentTimeMillis(), amount};
                lastTitle.put(e.getPlayer(), titleArray);

            }
        }
    }


    //Cancels hoppers or minecarts picking up gold nuggets
    @EventHandler
    public void hopperPickupGoldEvent(InventoryPickupItemEvent e) {
        if (GoldFunctions.isGold(e.getItem().getItemStack())) {
            e.setCancelled(true);
        }
    }

    //Prevents gold pieces moving between hoppers/other inventories etc
    @EventHandler
    public void goldMoveInventoryEvent(InventoryMoveItemEvent e) {
        if (GoldFunctions.isGold(e.getItem())) {
            e.setCancelled(true);
        }
    }

    //Prevents player putting gold pieces into their inventory
    @EventHandler(priority= EventPriority.HIGHEST)
    public void goldToInventoryEvent(InventoryClickEvent e) {
        if (GoldFunctions.isGold(e.getCursor())) {
            if (e.getClickedInventory() != null) {
                if (!e.getClickedInventory().getName().startsWith("Satchel") | e.getClick() == ClickType.SHIFT_LEFT | e.getClick() == ClickType.SHIFT_RIGHT) {
                    e.setCancelled(true);
                }
            }
        } else if (GoldFunctions.isGold(e.getCurrentItem())) {
            if (e.getClickedInventory() != null) {
                if (e.getClick() == ClickType.SHIFT_LEFT | e.getClick() == ClickType.SHIFT_RIGHT) {
                    e.setCancelled(true);
                }
            }
        }
    }

    //Prevents player dragging gold pieces in their inventory
    @EventHandler
    public void goldDragToInventoryEvent(InventoryDragEvent e) {
        if (GoldFunctions.isGold(e.getOldCursor())) {
            e.setCancelled(true);
        }
    }

    //Take gold from player's account when dropped, unless drop was caused by closing a satchel
    @EventHandler
    public void goldDropEvent(PlayerDropItemEvent e) {
        if (GoldFunctions.isGold(e.getItemDrop().getItemStack())) {
            Integer amount = e.getItemDrop().getItemStack().getAmount();
            Citizen p = Kingdoms.getCitizen(e.getPlayer().getName());
            Integer newBalance = p.getBalance() - amount;
            p.setBalance(newBalance);
        }
    }

    //Put gold in player's satchel if clicked in furnace
    @EventHandler
    public void goldClickEvent(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.FURNACE) {
            if (GoldFunctions.isGold(e.getCurrentItem())) {
                Player p = (Player) e.getWhoClicked();
                Citizen c = Kingdoms.getCitizen(p.getName());
                c.setBalance(c.getBalance() + e.getCurrentItem().getAmount());
                e.setCurrentItem(new ItemStack(Material.AIR));
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5F,1.0F);
            }
        }
    }

    //Drop gold for mob kill
    @EventHandler
    public void killMob(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) {
            if (e.getDamage() > ((LivingEntity) e.getEntity()).getHealth()) {
                e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), GoldFunctions.getGoldItem(goldPerMobKill.get(e.getEntity().getType().toString())));
            }
        }
    }
}
