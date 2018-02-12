package org.edenmc.kingdoms.race;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

import java.util.ArrayList;

/**
 * Created by Jack on 2/9/2018.
 */
public class RaceGUI implements Listener {

    int slots;
    private ArrayList<String> races;
    private Inventory gui;

    public RaceGUI() {

        races = new ArrayList<String>();
        for (String race : Kingdoms.races.keySet()) {
            races.add(race);
        }

        slots = Kingdoms.races.keySet().size();
        slots = (int) Math.ceil(slots / 9.0) * 9;
        gui = Bukkit.createInventory(null, slots, "Race Selection");
        System.out.println(races);
        for (String race : races) {
            ItemStack item = new ItemStack(Kingdoms.races.get(race).getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(race);
            item.setItemMeta(meta);
            gui.addItem(item);
        }

    }

    public void open(Player p) {
        p.openInventory(gui);
    }

    @EventHandler
    public void guiClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName().equals("Race Selection")) {
            String raceChosen = e.getCursor().getItemMeta().getDisplayName();
            Citizen p = Kingdoms.getCitizen(e.getWhoClicked().getName());
            p.setRace(raceChosen);
            e.setCancelled(true);

        }
    }
}
