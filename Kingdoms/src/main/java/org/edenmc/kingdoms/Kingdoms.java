package org.edenmc.kingdoms;

import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.edenmc.kingdoms.citizen.Citizen;
import org.edenmc.kingdoms.citizen.CitizenHandler;
import org.edenmc.kingdoms.customitems.CraftListener;
import org.edenmc.kingdoms.customitems.CustomItemConfig;
import org.edenmc.kingdoms.customitems.CustomItemListener;
import org.edenmc.kingdoms.customitems.CustomItemMobSpawnUtil;
import org.edenmc.kingdoms.economy.GoldCommands;
import org.edenmc.kingdoms.economy.GoldFunctions;
import org.edenmc.kingdoms.economy.GoldHandler;
import org.edenmc.kingdoms.economy.SatchelHandler;
import org.edenmc.kingdoms.items.ItemCommands;
import org.edenmc.kingdoms.kingdoms.*;
import org.edenmc.kingdoms.race.RaceCommands;
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
    public static int switchRaceCooldown;
    public static HashMap<String,Integer> kingdomPrices = new HashMap<String,Integer>();
    public static HashMap<String,ArrayList<Object>> itemMap = new HashMap<String,ArrayList<Object>>();
    public static HashMap<String,String> mySQL = new HashMap<String,String>();
    public static HashMap<String,HashMap<String,String>> tablesToMake = new HashMap<String,HashMap<String,String>>();
    private static HashMap<String,Citizen> citizens = new HashMap<String,Citizen>();
    private static HashMap<String,BossBar> progressBars = new HashMap<String,BossBar>();
    private static HashMap<String,Kingdom> kingdoms = new HashMap<String,Kingdom>();
    private static HashMap<String, KingdomChunk> chunks = new HashMap<String, KingdomChunk>();
    private File configf, customitemsf;
    private static File cooldownsf;
    private FileConfiguration config, customitems;
    private static FileConfiguration cooldowns;
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
        registerRecipes();
        loadRandomConfig();
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
        getServer().getPluginManager().registerEvents(new KingdomListener(), this);

    }

    //Register recipes
    public void registerRecipes() {
        getServer().addRecipe(new FurnaceRecipe(GoldFunctions.getGoldItem(3), Material.GOLD_INGOT));
    }

    //Register commands
    public void registerCommands() {
        getCommand("items").setExecutor(new ItemCommands());
        getCommand("gold").setExecutor(new GoldCommands());
        getCommand("kingdom").setExecutor(new KingdomCommands());
        getCommand("accept").setExecutor(new KingdomCommands());
        getCommand("deny").setExecutor(new KingdomCommands());
        getCommand("chunk").setExecutor(new ChunkCommands());
        getCommand("switchrace").setExecutor(new RaceCommands());


    }

    private void createFiles() {

        configf = new File(getDataFolder(), "config.yml");
        customitemsf = new File(getDataFolder(), "customitems.yml");
        cooldownsf = new File(getDataFolder(), "cooldowns.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!customitemsf.exists()) {
            customitemsf.getParentFile().mkdirs();
            saveResource("customitems.yml", false);
        }
        if (!cooldownsf.exists()) {
            cooldownsf.getParentFile().mkdirs();
            saveResource("cooldowns.yml", false);
        }

        config = new YamlConfiguration();
        customitems = new YamlConfiguration();
        cooldowns = new YamlConfiguration();
        try {
            config.load(configf);
            customitems.load(customitemsf);
            cooldowns.load(cooldownsf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getCooldowns() {
        return cooldowns;
    }

    public static void saveCooldowns() {
        try {
            cooldowns.save(cooldownsf);
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
            itemMap.put(item, attributes);
        }
        kingdomPrices.put("create", getConfig().getInt("KingdomPrices.Create"));
        kingdomPrices.put("claim", getConfig().getInt("KingdomPrices.Claim"));
        for (String mobType : config.getConfigurationSection("GoldPerMobKill").getKeys(false)) {
            GoldHandler.goldPerMobKill.put(mobType, config.getInt("GoldPerMobKill." + mobType));
        }
    }

    //Load random config values
    public void loadRandomConfig() {
        startingGold = getConfig().getInt("StartingGold");
        switchRaceCooldown = getConfig().getInt("SwitchRaceCooldown");
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
            ArrayList<String> members = new ArrayList<String>();
            for (String flag : MySQL.getData("chunks", "chunk", "flags", row).split(",")) {
                flags.add(flag);
            }
            for (String member : MySQL.getData("chunks", "chunk", "members", row).split(",")) {
                members.add(member);
            }
            String owner = MySQL.getData("chunks", "chunk", "owner", row);
            if (owner == null) {
                owner = "";
            }
            KingdomChunk ch = new KingdomChunk(chunk, kingdom, owner, members, flags);
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
        String memString = "";
        for (String mem : ck.getMembers()) {
            memString = memString + mem + ",";
        }
        if (memString.length() > 0) {
            memString.substring(0, memString.length() - 1);
        }
        String[] data = {ck.getChunk().getX() + " " + ck.getChunk().getZ(), ck.getKingdom(), ck.getWorld().getName(), memString, flagString};
        String[] columns = {"chunk", "kingdom", "world", "members", "flags"};
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
            String locString = MySQL.getData("kingdoms", "kingdom", "spawn", kingdom);
            Location loc = new Location(Bukkit.getWorld(locString.split(",")[0]), Integer.parseInt(locString.split(",")[1]), Integer.parseInt(locString.split(",")[2]), Integer.parseInt(locString.split(",")[3]));
            Kingdom k = new Kingdom(kingdom, owner, chs, wardens, residents, flags, loc);
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
