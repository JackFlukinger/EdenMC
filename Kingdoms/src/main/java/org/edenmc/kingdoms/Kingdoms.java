package org.edenmc.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.edenmc.kingdoms.economy.*;
import org.edenmc.kingdoms.items.ItemCommands;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jack on 6/20/2017.
 */
public class Kingdoms extends JavaPlugin {
    public static int startingGold;
    public static HashMap<String,ArrayList<Object>> itemMap = new HashMap<String,ArrayList<Object>>();
    public static HashMap<String,String> mySQL = new HashMap<String,String>();
    public static HashMap<Player,Integer> playerGold = new HashMap<Player,Integer>();
    public static HashMap<String,HashMap<String,String>> tablesToMake = new HashMap<String,HashMap<String,String>>();

    public static void main(String[] args) {

    }

    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        registerEvents();
        registerCommands();
        loadItems();
        loadMysql();
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
    }

    //Register commands
    public void registerCommands() {
        getCommand("items").setExecutor(new ItemCommands());
        getCommand("gold").setExecutor(new GoldCommands());

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

    //Load players if plugin reloaded while server still online
    public void loadPlayers() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            playerGold.put(p, GoldFunctions.getBalance(p));
        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }

}
