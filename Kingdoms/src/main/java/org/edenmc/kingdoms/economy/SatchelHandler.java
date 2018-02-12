package org.edenmc.kingdoms.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;
import org.edenmc.kingdoms.items.ItemFunctions;

import java.util.List;

/**
 * Created by Jack on 6/21/2017.
 */
public class SatchelHandler implements Listener {

    //Prevent clicking satchel in any way to move it
    @EventHandler
    public void satchelClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (ItemFunctions.isItem(e.getCurrentItem(), "Satchel")) {
                e.setCancelled(true);
                Citizen p = Kingdoms.getCitizen(e.getWhoClicked().getName());
                p.openSatchel(p);
            }
        }
    }

    //Stops switching satchel between hands
    @EventHandler
    public static void switchHandEvent(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }


    //Prevent moving satchel between inventories
    @EventHandler
    public void satchelMove(InventoryMoveItemEvent e) {
        if (ItemFunctions.isItem(e.getItem(),"Satchel")) {
            e.setCancelled(true);
        }
    }

    //Prevent dropping satchel somehow
    @EventHandler
    public void satchelDrop(PlayerDropItemEvent e) {
        if (ItemFunctions.isItem(e.getItemDrop().getItemStack(), "Satchel")) {
            e.setCancelled(true);
        }
    }

    //Give player satchel on respawn
    @EventHandler
    public void giveSatchelOnRespawn(PlayerRespawnEvent e) {
        Citizen p = Kingdoms.getCitizen(e.getPlayer().getName());
        p.giveSatchel();
    }

    //Give player satchel when closing satchel inventory
    @EventHandler
    public void giveSatchelOnSatchelClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            if (e.getInventory().getName().startsWith("Satchel")) {
                Citizen p = Kingdoms.getCitizen(e.getPlayer().getName());
                p.giveSatchel();
            }
            if (GoldFunctions.isGold(e.getPlayer().getItemOnCursor())) {
                e.getPlayer().setItemOnCursor(null);
            }
        }
    }

    //Prevent dropping satchel on death
    @EventHandler
    public void disableSatchelDropOnDeath(PlayerDeathEvent e) {
        List<ItemStack> list = e.getDrops();
        ItemStack Satchel = null;
        for (ItemStack item : list) {
            if (item.getType() == Material.getMaterial((String) Kingdoms.itemMap.get("Satchel").get(0)) && item.hasItemMeta() && item.getItemMeta().isUnbreakable() && item.getDurability() == (Integer) Kingdoms.itemMap.get("Satchel").get(1)) {
                Satchel = item;
            }
        }
        list.remove(Satchel);
    }

    //If a player has joined before and doesn't have a satchel for whatever reason, give them one
    @EventHandler
    public void giveSatchelOnJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPlayedBefore()) {
            Citizen p = Kingdoms.getCitizen(e.getPlayer().getName());
            if (!p.hasSatchel()) {
                p.giveSatchel();
            }
        }
    }

    //Disable dragging satchel into inventory
    @EventHandler
    public void stopSatchelDragEvent(InventoryDragEvent e) {
        if (ItemFunctions.isItem(e.getCursor(), "Satchel")) {
            e.setCancelled(true);
        }
    }
}
