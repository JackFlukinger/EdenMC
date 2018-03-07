package org.edenmc.kingdoms.race;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.citizen.Citizen;

/**
 * Created by Jack on 2/9/2018.
 */
public class RaceHandler implements Listener {

    @EventHandler
    public void guiClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName().equals("           Race Selection       ")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && !e.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) {
                Citizen p = Kingdoms.getCitizen(e.getWhoClicked().getName());
                String oldRace = "";
                if (p.getRace() != null) {
                    oldRace = p.getRace();
                    for (PotionEffect pot : Kingdoms.getRaceConf().getEffects(oldRace,p.getRacelevel())) {
                        p.getPlayer().removePotionEffect(pot.getType());
                    }
                }
                String raceChosen = e.getCurrentItem().getItemMeta().getDisplayName();
                p.setRace(raceChosen);
                p.getPlayer().closeInventory();
                if (raceChosen.startsWith("A") | raceChosen.startsWith("E") | raceChosen.startsWith("I") | raceChosen.startsWith("O") | raceChosen.startsWith("U")) {
                    p.getPlayer().sendTitle(Kingdoms.getRaceConf().getColor(raceChosen) + raceChosen, "§bYou are now an", 30, 100, 30);
                    p.applyPotionEffects();
                    p.getPlayer().setPlayerListName(Kingdoms.getRaceConf().getColor(p.getRace()) + p.getPlayer().getName());
                } else {
                    p.getPlayer().sendTitle(Kingdoms.getRaceConf().getColor(raceChosen) + raceChosen, "§bYou are now a", 30, 100, 30);
                    p.applyPotionEffects();
                    p.getPlayer().setPlayerListName(Kingdoms.getRaceConf().getColor(p.getRace()) + p.getPlayer().getName());
                }
                ConsoleCommandSender sender = Bukkit.getConsoleSender();
                Bukkit.dispatchCommand(sender, "rtp 0 10000 -x 0 -z 0 -w world -p " + p.getName());
            }
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void stopGUIDrag(InventoryDragEvent e) {
        if (e.getInventory() != null && e.getInventory().getName().equals("           Race Selection       ")) {
            if (e.getCursor() != null && e.getCursor().hasItemMeta() && !e.getCursor().getItemMeta().getDisplayName().equals(" ")) {
                Citizen p = Kingdoms.getCitizen(e.getWhoClicked().getName());
                String oldRace = "";
                if (p.getRace() != null) {
                    oldRace = p.getRace();
                    for (PotionEffect pot : Kingdoms.getRaceConf().getEffects(oldRace,p.getRacelevel())) {
                        p.getPlayer().removePotionEffect(pot.getType());
                    }
                }
                String raceChosen = e.getCursor().getItemMeta().getDisplayName();
                p.setRace(raceChosen);
                p.getPlayer().closeInventory();
                if (raceChosen.startsWith("A") | raceChosen.startsWith("E") | raceChosen.startsWith("I") | raceChosen.startsWith("O") | raceChosen.startsWith("U")) {
                    p.getPlayer().sendTitle(Kingdoms.getRaceConf().getColor(raceChosen) + raceChosen, "§bYou are now an", 30, 100, 30);
                    p.applyPotionEffects();
                    p.getPlayer().setPlayerListName(Kingdoms.getRaceConf().getColor(p.getRace()) + p.getPlayer().getName());
                } else {
                    p.getPlayer().sendTitle(Kingdoms.getRaceConf().getColor(raceChosen) + raceChosen, "§bYou are now a", 30, 100, 30);
                    p.applyPotionEffects();
                    p.getPlayer().setPlayerListName(Kingdoms.getRaceConf().getColor(p.getRace()) + p.getPlayer().getName());

                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void preventClose(InventoryCloseEvent e) {
        if (e.getInventory() != null && e.getInventory().getName().equals("           Race Selection       ") && Kingdoms.getCitizen(e.getPlayer().getName()).getRace() == null) {
            Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
                @Override
                public void run(){
                    RaceGUI gui = new RaceGUI();
                    gui.open((Player) e.getPlayer());
                }
            }, 1L);

        }
    }

    @EventHandler
    public void preventMilkDrinkClearRaceEffects(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            Citizen c = Kingdoms.getCitizen(e.getPlayer().getName());
            Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
                @Override
                public void run(){
                    c.applyPotionEffects();
                }
            }, 1L);
        }
    }
}
