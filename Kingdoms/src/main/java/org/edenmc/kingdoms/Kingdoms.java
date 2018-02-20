package org.edenmc.kingdoms;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.edenmc.kingdoms.kingdoms.Kingdom;
import org.edenmc.kingdoms.kingdoms.KingdomChunk;
import org.edenmc.kingdoms.kingdoms.KingdomCommands;
import org.edenmc.kingdoms.kingdoms.MoveListener;
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
    private static HashMap<String,Kingdom> kingdoms = new HashMap<String,Kingdom>();
    private static HashMap<String, KingdomChunk> chunks = new HashMap<String, KingdomChunk>();
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
        loadChunks();
        loadKingdoms();
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
        getServer().getPluginManager().registerEvents(new MoveListener(), this);



    }

    //Register commands
    public void registerCommands() {
        getCommand("items").setExecutor(new ItemCommands());
        getCommand("gold").setExecutor(new GoldCommands());
        getCommand("kingdom").setExecutor(new KingdomCommands());
        getCommand("accept").setExecutor(new KingdomCommands());
        getCommand("deny").setExecutor(new KingdomCommands());
        getCommand("chunk").setExecutor(new KingdomCommands());


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

    public void loadChunks() {
        for (String row : MySQL.getAllRows("chunks", "chunk")) {
            World world = Bukkit.getWorld(MySQL.getData("chunks", "chunk", "world", row));
            Chunk chunk = world.getChunkAt(Integer.parseInt(row.split(" ")[0]), Integer.parseInt(row.split(" ")[1]));
            String kingdom = MySQL.getData("chunks", "chunk", "kingdom", row);
            ArrayList<String> flags = new ArrayList<String>();
            for (String flag : MySQL.getData("chunks", "chunk", "flags", row).split(",")) {
                flags.add(flag);
            }
            String owner = MySQL.getData("chunks", "chunk", "owner", row);
            KingdomChunk ch = new KingdomChunk(chunk, kingdom, owner, flags);
            chunks.put(ch.getChunk().getX() + " " + ch.getChunk().getZ(), ch);
        }
    }

    public static void addChunk(KingdomChunk ck) {
        chunks.put(ck.getChunk().getX() + " " + ck.getChunk().getZ(), ck);
        String flagString = "";
        for (String flag : ck.getFlags()) {
            flagString = flagString + flag + ",";
        }
        if (flagString.length() > 0) {
            flagString.substring(0, flagString.length() - 1);
        }
        String[] data = {ck.getChunk().getX() + " " + ck.getChunk().getZ(), ck.getKingdom(), ck.getWorld().getName(), flagString};
        String[] columns = {"chunk", "kingdom", "world", "flags"};
        MySQL.enterData("chunks", columns, data);
    }

    public static void removeChunk(KingdomChunk ch) {
        chunks.remove(ch.getChunk().getX() + " " + ch.getChunk().getZ());
        MySQL.delete("chunks","chunk",ch.getChunk().getX() + " " + ch.getChunk().getZ());
    }



    public static HashMap<String, KingdomChunk> getChunks() {
        return chunks;
    }

    public void loadKingdoms() {
        for (String kingdom : MySQL.getAllRows("kingdoms", "kingdom")) {
            ArrayList<KingdomChunk> chs = new ArrayList<KingdomChunk>();
            for (KingdomChunk ch : chunks.values()) {
                if (ch.getKingdom().equals(kingdom)) {
                    chs.add(ch);
                }
            }
            ArrayList<String> residents = new ArrayList<String>();
            for (String res : MySQL.getData("kingdoms", "kingdom", "residents", kingdom).split(",")) {
                if (res != null && !res.equals("")) {
                    residents.add(res);
                }
            }
            ArrayList<String> wardens = new ArrayList<String>();
            for (String war : MySQL.getData("kingdoms", "kingdom", "wardens", kingdom).split(",")) {
                if (war != null && !war.equals("")) {
                    wardens.add(war);
                }
            }
            String owner = MySQL.getData("kingdoms", "kingdom", "owner", kingdom);
            ArrayList<String> flags = new ArrayList<String>();
            for (String flag : MySQL.getData("kingdoms", "kingdom", "flags", kingdom).split(",")) {
                if (flag != null && !flag.equals("")) {
                    flags.add(flag);
                }
            }
            Kingdom k = new Kingdom(kingdom, owner, chs, wardens, residents, flags);
            setKingdom(k);
        }
    }


    public static boolean isKingdomNear(Chunk ch) {
        for (KingdomChunk chunk : chunks.values()) {
            if (new Location(ch.getWorld(), ch.getX(), 0, ch.getZ()).distance(new Location(chunk.getChunk().getWorld(), chunk.getChunk().getX(), 0, chunk.getChunk().getZ())) < 15) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNextToKingdom(Chunk ch) {
        if (getChunks().keySet().contains((ch.getX() + 1) + " " + ch.getZ()) | getChunks().keySet().contains((ch.getX() - 1) + " " + ch.getZ()) | getChunks().keySet().contains(ch.getX() + " " + (ch.getZ() + 1)) | getChunks().keySet().contains(ch.getX() + " " + (ch.getZ() - 1))) {
            return true;
        }
        return false;
    }

    public static boolean isNextToWilderness(Chunk ch) {
        if (!getChunks().keySet().contains((ch.getX() + 1) + " " + ch.getZ()) | !getChunks().keySet().contains((ch.getX() - 1) + " " + ch.getZ()) | !getChunks().keySet().contains(ch.getX() + " " + (ch.getZ() + 1)) | !getChunks().keySet().contains(ch.getX() + " " + (ch.getZ() - 1))) {
            return true;
        }
        return false;
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

    public static Kingdom getKingdom(String name) { return kingdoms.get(name);}

    public static ArrayList<String> getKingdoms() {
        ArrayList<String> kingdomList = new ArrayList<String>();
        for (String k : kingdoms.keySet()) {
            kingdomList.add(k);
        }
        return kingdomList;
    }

    public static void setKingdom(Kingdom k) { kingdoms.put(k.getName(), k);}

    public static void removeKingdom(Kingdom k) {kingdoms.remove(k.getName());}

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
