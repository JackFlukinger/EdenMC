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
    private HashMap<String,ArrayList<PotionEffect>> effects;
    private HashMap<String, List<String>> lore;
    private HashMap<String,Material> material;

    public RaceConfig(FileConfiguration config) {
        races = new ArrayList<String>();
        effects = new HashMap<String,ArrayList<PotionEffect>>();
        material = new HashMap<String,Material>();
        lore = new HashMap<String,List<String>>();
        for (String race : config.getConfigurationSection("Races").getKeys(false)) {
            races.add(race);
            ArrayList<PotionEffect> potionArray = new ArrayList<PotionEffect>();
            for (String potion : config.getStringList("Races." + race + ".Effects")) {
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(potion.split(".")[0]), Integer.parseInt(potion.split(".")[1]),Integer.parseInt(potion.split(".")[2]), true, Boolean.getBoolean(potion.split(".")[3]));
                potionArray.add(potionEffect);
            }
            lore.put(race, config.getStringList("Races." + race + ".GUI.Lore"));
            effects.put(race, potionArray);
            material.put(race, Material.getMaterial(config.getString("Races." + race + ".GUI.Material")));
        }
    }

    public ArrayList<PotionEffect> getEffects(String race) {
        return effects.get(race);
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


}
