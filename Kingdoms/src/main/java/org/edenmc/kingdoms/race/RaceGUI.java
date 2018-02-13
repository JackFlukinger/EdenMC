package org.edenmc.kingdoms.race;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.Kingdoms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2/9/2018.
 */
public class RaceGUI {

    int slots;
    private ArrayList<String> races;
    private Inventory gui;

    public RaceGUI() {

        races = Kingdoms.getRaceConf().getRaces();
        slots = races.size();
        slots = (int) Math.ceil(slots / 9.0) * 9;
        gui = Bukkit.createInventory(null, slots, "           Race Selection       ");
        System.out.println(races);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta glassM = glass.getItemMeta();
        glassM.setDisplayName(" ");
        glass.setItemMeta(glassM);
        items.add(glass);
        for (String race : races) {
            ItemStack item = new ItemStack(Kingdoms.getRaceConf().getMaterial(race));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(race);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<String> lore = new ArrayList<String>();
            lore = Kingdoms.getRaceConf().getLore(race);
            meta.setLore(lore);
            item.setItemMeta(meta);
            items.add(item);
            items.add(glass);
        }
        ItemStack[] itemArray = new ItemStack[items.size()];
        itemArray = items.toArray(itemArray);
        gui.setContents(itemArray);
    }

    public void open(Player p) {
        p.openInventory(gui);

    }

}
