package org.edenmc.kingdoms.items;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.Kingdoms;

/**
 * Created by Jack on 6/21/2017.
 */
public class ItemFunctions {
    public static boolean isItem(ItemStack test, String item) {
        if (test != null) {
            Material itemMaterial = Material.getMaterial((String) Kingdoms.itemMap.get(item).get(0));
            Short itemData = ((Integer) Kingdoms.itemMap.get(item).get(1)).shortValue();
            if (test.getType() == itemMaterial && test.getDurability() == itemData) {
                return true;
            }
        }
        return false;
    }

    public static void giveItem(Player p, String item, int amount) {
        Material itemMaterial = Material.getMaterial((String) Kingdoms.itemMap.get(item).get(0));
        Integer itemData = (Integer) Kingdoms.itemMap.get(item).get(1);
        String itemName = (String) Kingdoms.itemMap.get(item).get(2);
        ItemStack finalItem = new ItemStack(itemMaterial, amount);
        finalItem.setDurability(itemData.shortValue());
        ItemMeta meta = finalItem.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        meta.setDisplayName("§r" + itemName);
        finalItem.setItemMeta(meta);
        p.getInventory().addItem(finalItem);
    }

    public static ItemStack setMaxStackSize(ItemStack is, int amount){
        try {

            net.minecraft.server.v1_12_R1.ItemStack nmsIS = CraftItemStack.asNMSCopy(is);

            nmsIS.getItem().d(amount);

            return CraftItemStack.asBukkitCopy(nmsIS);

        } catch (Throwable t) { }

        return null;
    }
}