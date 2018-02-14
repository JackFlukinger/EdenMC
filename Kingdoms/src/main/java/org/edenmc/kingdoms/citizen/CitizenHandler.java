package org.edenmc.kingdoms.citizen;


import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.edenmc.kingdoms.Kingdoms;

/**
 * Created by Jack on 2/8/2018.
 */
public class CitizenHandler implements Listener {

    @EventHandler (priority= EventPriority.LOWEST)
    public void addCitizenOnJoin(PlayerJoinEvent e) {
        Citizen c = new Citizen(e.getPlayer());
        Kingdoms.setCitizen(c);
        if (c.getRace() != null) {
            BossBar progressBar = Bukkit.getServer().createBossBar(Kingdoms.getRaceConf().getColor(c.getRace()) + c.getRace() + " Level " + c.getRacelevel(), BarColor.BLUE, BarStyle.SOLID);
            progressBar.setProgress(c.getRaceEXP() * 1.0 / Kingdoms.getRaceConf().getLevelTotals()[c.getRacelevel() - 1]);
            progressBar.addPlayer(c.getPlayer());
            Kingdoms.setProgressBars(c, progressBar);
            c.getPlayer().setPlayerListName(Kingdoms.getRaceConf().getColor(c.getRace()) + c.getPlayer().getName());
            c.applyPotionEffects();
        }
    }

    @EventHandler (priority=EventPriority.HIGH)
    public void removeCitizenOnJoin(PlayerQuitEvent e) {
        Kingdoms.removeCitizen(Kingdoms.getCitizen(e.getPlayer().getName()));
        Kingdoms.removeProgressBar(e.getPlayer().getName());
    }

    @EventHandler
    public void addEXPOnCollect(PlayerExpChangeEvent e) {
        Citizen c = Kingdoms.getCitizen(e.getPlayer().getName());
        c.setRaceEXP(c.getRaceEXP() + e.getAmount());
    }
}
