package org.edenmc.kingdoms.economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.items.ItemFunctions;

import java.util.Arrays;
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
                openSatchel((Player) e.getWhoClicked());
            }
        }
    }

    //Stops switching satchel between hands
    @EventHandler
    public static void switchHandEvent(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    //Opens satchel inventory, puts in the gold pieces
    public void openSatchel(Player p) {
        Integer balance = Kingdoms.playerGold.get(p);
        if (balance > 2700) {
            balance = 2700;
        }
        int rowsNeeded = (int) Math.ceil((balance / 50) / 9D);
        int stacksNeeded = (int) Math.floor(balance / 50D);
        int remainder = balance % 50;
        if (rowsNeeded == 0) {
            rowsNeeded = 1;
        }
        Inventory satchel = Bukkit.getServer().createInventory(null, rowsNeeded * 9, "Satchel          " + Kingdoms.playerGold.get(p) + " Gold Pieces");
        ItemStack[] items = new ItemStack[stacksNeeded];
        if (remainder > 0) {
            items = new ItemStack[stacksNeeded + 1];
        }
        for (int i = 0; i < stacksNeeded; i++) {
            items[i] = GoldFunctions.getGoldItem(50);
        }
        if (remainder > 0) {
            items[stacksNeeded] = GoldFunctions.getGoldItem(remainder);
        }
        satchel.setStorageContents(items);
        p.openInventory(satchel);
    }

    public static void giveSatchel(Player p, Integer balance) {
        ItemStack satchel = new ItemStack(Material.getMaterial((String) Kingdoms.itemMap.get("Satchel").get(0)));
        ItemMeta meta = satchel.getItemMeta();
        Integer durability = (Integer) Kingdoms.itemMap.get("Satchel").get(1);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        satchel.setDurability(durability.shortValue());
        meta.setDisplayName("§r" + Kingdoms.itemMap.get("Satchel").get(2));
        String[] lore = {"§b" + balance + " Gold Pieces"};
        meta.setLore(Arrays.asList(lore));
        satchel.setItemMeta(meta);
        p.getInventory().setItemInOffHand(satchel);
    }

    public static void setSatchelBalance(Player p, Integer balance) {
        ItemStack satchel = p.getInventory().getItemInOffHand();
        ItemMeta meta = satchel.getItemMeta();
        String[] lore = {"§b" + balance + " Gold Pieces"};
        meta.setLore(Arrays.asList(lore));
        satchel.setItemMeta(meta);
        p.getInventory().setItemInOffHand(satchel);
    }

    public static boolean hasSatchel(Player p) {
        if (ItemFunctions.isItem(p.getInventory().getItemInOffHand(),"Satchel")) {
            return true;
        }
        return false;
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
        SatchelHandler.giveSatchel(e.getPlayer(), GoldFunctions.getBalance(e.getPlayer()));
    }

    //Give player satchel when closing satchel inventory
    @EventHandler
    public void giveSatchelOnSatchelClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            if (e.getInventory().getName().startsWith("Satchel")) {
                SatchelHandler.giveSatchel(((Player) e.getPlayer()).getPlayer(), GoldFunctions.getBalance((Player) ((Player) e.getPlayer()).getPlayer()));
            }
        }
    }

    //Prevent dropping satchel on death
    @EventHandler
    public void disableSatchelDrop(PlayerDeathEvent e) {
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
            if (!hasSatchel(e.getPlayer())) {
                giveSatchel(e.getPlayer(), GoldFunctions.getBalance(e.getPlayer()));
            }
        }
    }
}
