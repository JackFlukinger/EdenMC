package org.edenmc.kingdoms.economy;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.edenmc.kingdoms.Kingdoms;

import java.io.IOException;

/**
 * Created by Jack on 6/20/2017.
 */
public class GoldHandler implements Listener {

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
            e.getItem().setItemStack(new ItemStack(Material.AIR, 1));
            GoldFunctions.setBalance(e.getPlayer(), Kingdoms.playerGold.get(e.getPlayer()) + amount);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5F,1.0F);
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
    public void pickupGoldEvent(InventoryPickupItemEvent e) {
        if (GoldFunctions.isGold(e.getItem().getItemStack())) {
            e.setCancelled(true);
        }
    }

    //Prevents player putting gold pieces into their inventory
    @EventHandler
    public void goldToInventoryEvent(InventoryMoveItemEvent e) {

    }
}
