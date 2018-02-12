package org.edenmc.kingdoms.race;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

/**
 * Created by Jack on 2/9/2018.
 */
public class Race {

    private String race;
    private Material material;
    private HashMap<Integer, PotionEffect[]> effects;

    public Race() {
        race = null;
        effects = null;
        material = null;
    }

    public Race(String name, HashMap<Integer, PotionEffect[]> fx, Material guiMat) {
        race = name;
        effects = fx;
        material = guiMat;

    }

    public String getName() {

        return race;
    }

    public Material getMaterial() {
        return material;
    }

    public PotionEffect[] getEffects(int level) {

        return effects.get(level);

    }

}
