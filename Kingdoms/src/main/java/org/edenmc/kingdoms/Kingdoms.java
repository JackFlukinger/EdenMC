package org.edenmc.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
import org.edenmc.kingdoms.race.Race;
import org.edenmc.kingdoms.race.RaceGUI;
import org.edenmc.kingdoms.race.RaceHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jack on 6/20/2017.
 */
public class Kingdoms extends JavaPlugin {
    public static int startingGold;
    public static HashMap<String,Race> races = new HashMap<String,Race>();
    public static HashMap<String,ArrayList<Object>> itemMap = new HashMap<String,ArrayList<Object>>();
    public static HashMap<String,String> mySQL = new HashMap<String,String>();
    public static HashMap<String,HashMap<String,String>> tablesToMake = new HashMap<String,HashMap<String,String>>();
    private static HashMap<String,Citizen> citizens = new HashMap<String,Citizen>();
    private File configf, customitemsf;
    private FileConfiguration config, customitems;
    public static CustomItemConfig cIConf;

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
        getServer().getPluginManager().registerEvents(new RaceGUI(), this);
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

    public FileConfiguration getCustomItemConfig() {
        return this.customitems;
    }

    public static CustomItemConfig getCIConf() {
        return cIConf;
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


    public void loadRaces() {
        for (String raceName : getConfig().getConfigurationSection("Races").getKeys(false)) {
            HashMap<Integer, PotionEffect[]> effects = new HashMap<Integer, PotionEffect[]>();
            for (String level : getConfig().getConfigurationSection("Races." + raceName + ".Effects").getKeys(false)) {
                ArrayList<PotionEffect> potArray = new ArrayList<PotionEffect>();
                for (String effect : getConfig().getStringList("Races." + raceName + ".Effects." + level)) {
                    String type = effect.split("\\.")[0];
                    Integer duration = Integer.parseInt(effect.split("\\.")[1]);
                    Integer amplifier = Integer.parseInt(effect.split("\\.")[2]);
                    Boolean particles = Boolean.getBoolean(effect.split("\\.")[3]);
                    PotionEffect finalEffect = new PotionEffect(PotionEffectType.getByName(type),duration,amplifier,false,particles);
                    potArray.add(finalEffect);
                }
                Integer lv = Integer.parseInt(level);
                PotionEffect[] finalArray = new PotionEffect[potArray.size()];
                for (int i = 0; i < potArray.size(); i++) {
                    finalArray[i] = potArray.get(i);
                }
                effects.put(lv, finalArray);


            }
            String material = getConfig().getString("Races." + raceName + ".GUI.Material");
            Race race = new Race(raceName, effects, Material.getMaterial(material));
            races.put(raceName, race);


        }
        System.out.println(races);
    }

    public static Plugin getPlugin() {
        return plugin;
    }


}
