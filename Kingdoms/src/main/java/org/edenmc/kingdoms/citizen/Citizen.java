package org.edenmc.kingdoms.citizen;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.MySQL;
import org.edenmc.kingdoms.economy.GoldFunctions;
import org.edenmc.kingdoms.items.ItemFunctions;
import org.edenmc.kingdoms.race.RaceGUI;

import java.util.Arrays;

/**
 * Created by Jack on 2/8/2018.
 */
public class Citizen {

    private String name;
    private String race;
    private int raceLevel;
    private int raceEXP;
    private String kingdom;
    private int balance;
    private int rows;
    private int stacks;

    public Citizen() {
        race = null;
        raceLevel = 1;
        raceEXP = 0;
        name = "";
        balance = 0;


    }

    public Citizen(Player p) {
        name = p.getName();
        race = loadRace(p);
        raceLevel = loadRaceLevel(p);
        raceEXP = loadRaceEXP(p);
        balance = loadBalance(p);

    }

    public void applyPotionEffects() {
        if (race != null) {
            for (PotionEffect p : Kingdoms.getRaceConf().getEffects(race, raceLevel)) {
                Bukkit.broadcastMessage(p.toString());
                getPlayer().addPotionEffect(p,true);
            }
        }
    }

    public void chooseRace() {
        Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
            @Override
            public void run(){
                RaceGUI gui = new RaceGUI();
                gui.open(getPlayer());
            }
        }, 20L);
    }

    public String getName() { return name; }


    private int loadBalance(Player p) {
        String string = MySQL.getData("players","uuid","balance",p.getUniqueId().toString());
        if (string == null || string.equals("")) {
            setBalance(0);
            return 0;

        }

        return Integer.parseInt(string);
    }

    private String loadRace(Player p) {
        String string = MySQL.getData("players","uuid","race",p.getUniqueId().toString());
        if (string == null || string.equals("")) {
            chooseRace();
            return null;

        }

        return string;

    }

    private int loadRaceEXP(Player p) {
        String exp = MySQL.getData("players","uuid","raceexp",p.getUniqueId().toString());
        if (exp == null | exp == "") {
            String[] data = {getPlayer().getUniqueId().toString(), "0"};
            String[] columns = {"uuid","raceexp"};
            MySQL.enterData("players",columns,data);
            exp = "0";
        }
        return Integer.parseInt(exp);
    }


    private int loadRaceLevel(Player p) {
        String level = MySQL.getData("players","uuid","racelevel",p.getUniqueId().toString());
        if (level == null | level == "") {
            String[] data = {getPlayer().getUniqueId().toString(), "1"};
            String[] columns = {"uuid","racelevel"};
            MySQL.enterData("players",columns,data);
            level = "1";
        }
        return Integer.parseInt(level);
    }

    public void setRaceLevel(int lv) {
        String[] data = {getPlayer().getUniqueId().toString(), String.valueOf(lv)};
        String[] columns = {"uuid","racelevel"};
        MySQL.enterData("players",columns,data);
        raceLevel = lv;
    }


    public void setRace(String nr) {
        if (race != null && race.equals(nr)) {
            return;
        }
        race = nr;
        String[] data = {getPlayer().getUniqueId().toString(), nr};
        String[] columns = {"uuid", "race"};
        MySQL.enterData("players", columns, data);
        setRaceEXP(0);
        setRaceLevel(1);
        return;

    }

    public void setBalance(int b) {

        balance = b;
        String[] data = {getPlayer().getUniqueId().toString(), String.valueOf(balance)};
        String[] columns = {"uuid","balance"};
        MySQL.enterData("players",columns,data);
        ItemStack satchel = getPlayer().getInventory().getItemInOffHand();
        ItemMeta meta = satchel.getItemMeta();
        String[] lore = {"§b" + balance + " Gold Pieces"};
        meta.setLore(Arrays.asList(lore));
        satchel.setItemMeta(meta);
        getPlayer().getInventory().setItemInOffHand(satchel);
        updateSatchel(this);

    }

    public void payPlayer(Citizen recipient, int amount) {
        Integer increasedBalance = recipient.getBalance() + amount;
        Integer decreasedBalance = getBalance() - amount;
        recipient.setBalance(increasedBalance);
        setBalance(decreasedBalance);

    }

    public String getRace() {return race;}

    public int getRacelevel() {
        return raceLevel;
    }

    public int getRaceEXP() {
        return raceEXP;
    }

    public void setRaceEXP(int exp) {
        if (exp < Kingdoms.getRaceConf().getLevelTotals()[raceLevel - 1]) {
            raceEXP = exp;
            String[] data = {getPlayer().getUniqueId().toString(), String.valueOf(exp)};
            String[] columns = {"uuid","raceexp"};
            MySQL.enterData("players",columns,data);
            if (Kingdoms.getProgressBar(this) != null) {
                BossBar bar = Kingdoms.getProgressBar(this);
                bar.setProgress(getRaceEXP() * 1.0 / Kingdoms.getRaceConf().getLevelTotals()[getRacelevel() - 1]);
            } else {
                BossBar progressBar = Bukkit.getServer().createBossBar(Kingdoms.getRaceConf().getColor(getRace()) + getRace() + " Level " + getRacelevel(), BarColor.BLUE, BarStyle.SOLID);
                progressBar.setProgress(getRaceEXP() * 1.0 / Kingdoms.getRaceConf().getLevelTotals()[getRacelevel() - 1]);
                progressBar.addPlayer(getPlayer());
                Kingdoms.setProgressBars(this, progressBar);
            }
        } else if (raceLevel < 3){
            raceEXP = exp - Kingdoms.getRaceConf().getLevelTotals()[raceLevel -1];
            String[] data = {getPlayer().getUniqueId().toString(), String.valueOf(raceEXP)};
            String[] columns = {"uuid","raceexp"};
            MySQL.enterData("players",columns,data);
            setRaceLevel(raceLevel + 1);
            BossBar bar = Kingdoms.getProgressBar(this);
            bar.setTitle(Kingdoms.getRaceConf().getColor(getRace()) + getRace() + " Level " + getRacelevel());
            bar.setProgress(getRaceEXP() * 1.0 / Kingdoms.getRaceConf().getLevelTotals()[getRacelevel() - 1]);
            applyPotionEffects();
        }
    }

    public int getBalance() {return balance;}

    public Player getPlayer() {return Bukkit.getPlayer(name);}

    public void giveSatchel() {

        ItemStack satchel = new ItemStack(Material.getMaterial((String) Kingdoms.itemMap.get("Satchel").get(0)));
        ItemMeta meta = satchel.getItemMeta();
        Integer durability = (Integer) Kingdoms.itemMap.get("Satchel").get(1);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        satchel.setDurability(durability.shortValue());
        meta.setDisplayName("§rSatchel");
        String[] lore = {"§b" + balance + " Gold Pieces"};
        meta.setLore(Arrays.asList(lore));
        satchel.setItemMeta(meta);
        Bukkit.getScheduler().runTaskLater(Kingdoms.getPlugin(), new Runnable(){
            @Override
            public void run(){
                getPlayer().getInventory().setItemInOffHand(satchel);
            }
        }, 1L);

    }

    public void openSatchel(Citizen viewer) {
        rows = 6;
        stacks = getStacks(balance);
        Inventory satchel = Bukkit.getServer().createInventory(null, rows * 9, "Satchel          " + viewer.getBalance() + " Gold Pieces");
        if (viewer.getPlayer() != getPlayer()) {
            satchel = Bukkit.getServer().createInventory(null, rows * 9, getPlayer().getName() + "'s Satchel - " + getBalance() + " Gold Pieces");

        }
        ItemStack[] items = new ItemStack[stacks];
        for (int i = 0; i < stacks - 1; i++) {
            items[i] = GoldFunctions.getGoldItem(50);
        }
        items[stacks - 1] = GoldFunctions.getGoldItem(balance % 50);
        satchel.setMaxStackSize(50);
        satchel.setStorageContents(items);
        viewer.getPlayer().openInventory(satchel);

    }

    public boolean hasSatchel() {
        if (ItemFunctions.isItem(getPlayer().getInventory().getItemInOffHand(),"Satchel")) {
            return true;
        }
        return false;
    }

    public void updateSatchel(Citizen viewer) {
        if (viewer.getPlayer().getOpenInventory().getTopInventory().getName().startsWith("Satchel")) {
            rows = 6;
            stacks = getStacks(balance);
            Inventory satchel = Bukkit.getServer().createInventory(null, rows * 9, "Satchel          " + getBalance() + " Gold Pieces");
            if (viewer.getPlayer() != getPlayer()) {
                satchel = Bukkit.getServer().createInventory(null, rows * 9, getPlayer().getName() + "'s Satchel - " + getBalance() + " Gold Pieces");

            }
            ItemStack[] items = new ItemStack[stacks];
            for (int i = 0; i < stacks - 1; i++) {
                items[i] = GoldFunctions.getGoldItem(50);
            }
            items[stacks - 1] = GoldFunctions.getGoldItem(balance % 50);
            satchel.setMaxStackSize(50);
            satchel.setStorageContents(items);
            viewer.getPlayer().openInventory(satchel);
        }
    }


    private int getStacks(int balance) {
        int stack = (balance / 50) + 1;

        return stack;

    }
}
