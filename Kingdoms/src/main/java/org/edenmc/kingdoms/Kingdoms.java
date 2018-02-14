package org.edenmc.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.edenmc.kingdoms.citizen.Citizen;
import org.edenmc.kingdoms.citizen.CitizenHandler;
import org.edenmc.kingdoms.customitems.CraftListener;
import org.edenmc.kingdoms.customitems.CustomItemConfig;
import org.edenmc.kingdoms.customitems.CustomItemListener;
import org.edenmc.kingdoms.customitems.CustomItemMobSpawnUtil;
import org.edenmc.kingdoms.economy.GoldCommands;
import org.edenmc.kingdoms.economy.GoldHandler;
import org.edenmc.kingdoms.economy.SatchelHandler;
import org.edenmc.kingdoms.items.ItemCommands;
import org.edenmc.kingdoms.race.RaceConfig;
import org.edenmc.kingdoms.race.RaceHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jack on 6/20/2017.
 */
public class Kingdoms extends JavaPlugin {
    public static int startingGold;
    public static HashMap<String,ArrayList<Object>> itemMap = new HashMap<String,ArrayList<Object>>();
    public static HashMap<String,String> mySQL = new HashMap<String,String>();
    public static HashMap<String,HashMap<String,String>> tablesToMake = new HashMap<String,HashMap<String,String>>();
    private static HashMap<String,Citizen> citizens = new HashMap<String,Citizen>();
    private static HashMap<String,BossBar> progressBars = new HashMap<String,BossBar>();
    private File configf, customitemsf;
    private FileConfiguration config, customitems;
    private static CustomItemConfig cIConf;
    private static RaceConfig raceConf;

    public static void main(String[] args) {

    }

    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        createFiles();
        loadMysql();
        registerEvents();
        registerCommands();
        loadItems();
        loadCustomItems();
        loadRaces();
        loadPlayers();
    }

    @Override
    public void onDisable() {
        MySQL.terminate();
    }

    //Register events
    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new GoldHandler(), this);
        getServer().getPluginManager().registerEvents(new SatchelHandler(), this);
        getServer().getPluginManager().registerEvents(new CitizenHandler(), this);
        getServer().getPluginManager().registerEvents(new RaceHandler(), this);
        getServer().getPluginManager().registerEvents(new CustomItemListener(), this);
        getServer().getPluginManager().registerEvents(new CraftListener(), this);
        getServer().getPluginManager().registerEvents(new CustomItemMobSpawnUtil(), this);


    }

    //Register commands
    public void registerCommands() {
        getCommand("items").setExecutor(new ItemCommands());
        getCommand("gold").setExecutor(new GoldCommands());

    }

    private void createFiles() {

        configf = new File(getDataFolder(), "config.yml");
        customitemsf = new File(getDataFolder(), "customitems.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!customitemsf.exists()) {
            customitemsf.getParentFile().mkdirs();
            saveResource("customitems.yml", false);
        }

        config = new YamlConfiguration();
        customitems = new YamlConfiguration();
        try {
            config.load(configf);
            customitems.load(customitemsf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //Load stuff from config into memory
    public void loadItems() {
        for (String item : getConfig().getConfigurationSection("Items").getKeys(false)) {
            ArrayList<Object> attributes = new ArrayList<Object>();
            attributes.add(getConfig().getString("Items." + item + "." + "Item"));
            attributes.add(getConfig().getInt("Items." + item + "." + "Data"));
            attributes.add(getConfig().getString("Items." + item + "." + "Name"));
            itemMap.put(item,attributes);
        }
        startingGold = getConfig().getInt("StartingGold");
    }


    //Load MySQL settings into memory
    public void loadMysql() {
        for (String field : getConfig().getConfigurationSection("MySQL").getKeys(false)) {
            mySQL.put(field,getConfig().getString("MySQL." + field));
        }
        MySQL.connect();

    }

    //Load currently online players into citizens hashmap
    public void loadPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Citizen c = new Citizen(p);
            citizens.put(p.getName(),c);
        }
    }

    public void loadCustomItems() {

        cIConf = new CustomItemConfig(customitems);
    }

    public static CustomItemConfig getCIConf() {
        return cIConf;
    }

    public static RaceConfig getRaceConf() {
        return raceConf;
    }

    public static void setCitizen(Citizen c) {
        citizens.put(c.getName(), c);
    }

    public static void removeCitizen(Citizen c) {
        citizens.remove(c.getName());
    }

    public static Citizen getCitizen(String name) {
        return citizens.get(name);
    }

    public static void setProgressBars(Citizen c, BossBar progBar) {
        progressBars.put(c.getName(), progBar);
    }

    public static BossBar getProgressBar(Citizen c) {
        return progressBars.get(c.getName());
    }

    public static void removeProgressBar(String name) {
        progressBars.remove(name);
    }

    public void loadRaces() {
        raceConf = new RaceConfig(config);
    }

    public static Plugin getPlugin() {
        return plugin;
    }


}
