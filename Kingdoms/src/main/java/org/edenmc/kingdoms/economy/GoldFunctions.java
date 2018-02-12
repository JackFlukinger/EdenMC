package org.edenmc.kingdoms.economy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.items.ItemFunctions;

/**
 * Created by Jack on 6/21/2017.
 */
public class GoldFunctions {

    //Checks if ItemStack is Gold Pieces
    public static boolean isGold(ItemStack item) {
        if (item != null && item.getType() == Material.QUARTZ) {
            if (item.hasItemMeta() && item.getItemMeta().isUnbreakable()) {
                if (item.getItemMeta().getDisplayName().equals("§rGold Piece")) {
                    return true;
                }
            }
        }
        return false;
    }

    //Gets gold item in specified amount
    public static ItemStack getGoldItem(Integer amount) {

        ItemStack gold = new ItemStack(Material.QUARTZ, amount);
        ItemMeta meta = gold.getItemMeta();
        meta.setDisplayName("§rGold Piece");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        gold.setItemMeta(meta);
        ItemFunctions.setMaxStackSize(gold,50);
        return gold;

    }

}
