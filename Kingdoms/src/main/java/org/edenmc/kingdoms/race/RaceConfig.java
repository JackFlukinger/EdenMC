package org.edenmc.kingdoms.race;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jack on 2/12/2018.
 */
public class RaceConfig {

    private ArrayList<String> races;
    private HashMap<String,HashMap<Integer,ArrayList<PotionEffect>>> effects;
    private HashMap<String, List<String>> lore;
    private HashMap<String,Material> material;
    private HashMap<String, String> color;
    private int[] levelTotals;

    public RaceConfig(FileConfiguration config) {
        races = new ArrayList<String>();
        effects = new HashMap<String,HashMap<Integer,ArrayList<PotionEffect>>>();
        material = new HashMap<String,Material>();
        lore = new HashMap<String,List<String>>();
        color = new HashMap<String,String>();
        levelTotals = new int[]{3965, 5375, 7035};
        for (String race : config.getConfigurationSection("Races").getKeys(false)) {
            races.add(race);
            HashMap<Integer,ArrayList<PotionEffect>> levelPots = new HashMap<Integer,ArrayList<PotionEffect>>();
            for (String level : config.getConfigurationSection("Races." + race + ".Effects").getKeys(false)) {
                ArrayList<PotionEffect> potionArray = new ArrayList<PotionEffect>();
                for (String potion : config.getStringList("Races." + race + ".Effects." + level)) {
                    PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(potion.split("\\.")[0]), Integer.parseInt(potion.split("\\.")[1]), Integer.parseInt(potion.split("\\.")[2]), true, Boolean.getBoolean(potion.split("\\.")[3]));
                    potionArray.add(potionEffect);
                }
                levelPots.put(Integer.parseInt(level), potionArray);
            }
            lore.put(race, config.getStringList("Races." + race + ".GUI.Lore"));
            effects.put(race, levelPots);
            material.put(race, Material.getMaterial(config.getString("Races." + race + ".GUI.Material")));
            color.put(race,config.getString("Races." + race + ".GUI.Color"));
        }
    }

    public ArrayList<PotionEffect> getEffects(String race, Integer level) {
        return effects.get(race).get(level);
    }

    public Material getMaterial(String race) {
        return material.get(race);
    }

    public ArrayList<String> getRaces() {
        return races;
    }

    public List<String> getLore(String race) {
        return lore.get(race);
    }

    public String getColor(String race) {
        return color.get(race);
    }

    public int[] getLevelTotals() {
        return levelTotals;
    }

}
