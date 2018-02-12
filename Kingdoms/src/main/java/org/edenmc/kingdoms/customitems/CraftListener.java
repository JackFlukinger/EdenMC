package org.edenmc.kingdoms.customitems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.edenmc.kingdoms.Kingdoms;

/**
 * Created by Jack on 2/11/2018.
 */
public class CraftListener implements Listener {
    @EventHandler(ignoreCancelled=true)
    public void CustomCraftEvent(CraftItemEvent e) {
        //Make sure the event is not cancelled
        if (e.getRecipe() instanceof ShapedRecipe) {
            ItemStack item = e.getRecipe().getResult();
            if (Kingdoms.getCIConf().isItemInConfig(item)) {
                CustomItem ci = new CustomItem(item);
                e.setCurrentItem(ci.create());
            }
        }
    }

    //Fixes items from before plugin installation
    @EventHandler(ignoreCancelled=true)
    public void CustomCheckEvent(InventoryClickEvent e) {
        ItemStack item = e.getCursor();
        if (Kingdoms.getCIConf().isItemInConfig(item)) {
            CustomItem ci = new CustomItem(item);
            ItemStack newItem = ci.create();
            e.getCursor().setItemMeta(newItem.getItemMeta());

        }
    }

}
