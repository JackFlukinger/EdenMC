package org.edenmc.kingdoms.economy;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.edenmc.kingdoms.Kingdoms;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Jack on 6/20/2017.
 */
public class GoldHandler implements Listener  {
    public static HashMap<Player, Object[]> lastTitle = new HashMap<Player, Object[]>();


    //Give player starting gold as defined in config
    @EventHandler
    public void startingGold(PlayerJoinEvent e) throws IOException {
        if (!e.getPlayer().hasPlayedBefore()) {
            SatchelHandler.giveSatchel(e.getPlayer(), Kingdoms.startingGold);
            GoldFunctions.setBalance(e.getPlayer(),Kingdoms.startingGold);
        } else {
            Kingdoms.playerGold.put(e.getPlayer(), GoldFunctions.getBalance(e.getPlayer()));
            e.getPlayer().sendMessage("Balance: " + GoldFunctions.getBalance(e.getPlayer()));
        }
    }

    //Add gold to balance when picked up
    @EventHandler
    public void pickupGold(PlayerPickupItemEvent e) {
        if (GoldFunctions.isGold(e.getItem().getItemStack())) {
            Integer amount = e.getItem().getItemStack().getAmount();
            e.setCancelled(true);
            e.getItem().remove();
            GoldFunctions.setBalance(e.getPlayer(), Kingdoms.playerGold.get(e.getPlayer()) + amount);
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

    //Remove player from HashMap when they leave server
    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        if (Kingdoms.playerGold.containsKey(e.getPlayer())) {
            Kingdoms.playerGold.remove(e.getPlayer());
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
            Player p = e.getPlayer();
            Integer newBalance = Kingdoms.playerGold.get(p) - amount;
            GoldFunctions.setBalance(p, newBalance);
        }
    }
}
