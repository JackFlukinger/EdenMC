package org.edenmc.kingdoms.customitems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack on 2/11/2018.
 */
public class CustomItemConfig {
    private List<String> affectedItems;
    private List<String> types;
    private HashMap<String,List<String>> prefixes;
    private HashMap<String,List<String>> baseNames;
    private HashMap<String,List<String>> suffixes;
    private HashMap<String,List<String>> rareDropBlocks;
    private HashMap<String,Map<Integer,String>> rareDrops;
    private HashMap<String,String> modifiers;
    private HashMap<String,Double> multipliers;
    private HashMap<String,Integer> maxEXPAdd;
    private HashMap<String,Integer> baseEXP;
    private HashMap<String,Integer> expPerMobKill;

    public CustomItemConfig(FileConfiguration config) {
        affectedItems = new ArrayList<String>();
        types = new ArrayList<String>();
        prefixes = new HashMap<String,List<String>>();
        baseNames = new HashMap<String,List<String>>();
        suffixes = new HashMap<String,List<String>>();
        rareDropBlocks = new HashMap<String,List<String>>();
        rareDrops = new HashMap<String,Map<Integer,String>>();
        modifiers = new HashMap<String,String>();
        multipliers = new HashMap<String,Double>();
        maxEXPAdd = new HashMap<String,Integer>();
        baseEXP = new HashMap<String,Integer>();
        expPerMobKill = new HashMap<String,Integer>();
        for (String item : config.getConfigurationSection("ItemsToAffect").getKeys(false)) {
            //Get config values for each item, initialize them for later use by Monsieur Plugin
            affectedItems.add(item);
            prefixes.put(item, config.getStringList("ItemsToAffect." + item + ".Prefixes"));
            baseNames.put(item, config.getStringList("ItemsToAffect." + item + ".Bases"));
            suffixes.put(item, config.getStringList("ItemsToAffect." + item + ".Suffixes"));
            modifiers.put(item, config.getString("ItemsToAffect." + item + ".Modifier"));
            multipliers.put(item, config.getDouble("ItemsToAffect." + item + ".Multiplier"));
            maxEXPAdd.put(item, config.getInt("ItemsToAffect." + item + ".MaxEXPAddPerLevel"));
            baseEXP.put(item, config.getInt("ItemsToAffect." + item + ".BaseEXP"));
            rareDropBlocks.put(item, config.getStringList("ItemsToAffect." + item + ".RareDropBlocks"));
            HashMap<Integer,String> dropMap = new HashMap<Integer,String>();
            List<String> rareDropList = config.getStringList("ItemsToAffect." + item + ".RareDrops");
            for (String dropString : rareDropList) {
                String[] dropValueArray = dropString.split(",");
                dropMap.put(Integer.parseInt(dropValueArray[1]),dropValueArray[0]);
            }
            rareDrops.put(item, dropMap);
        }
        Bukkit.broadcastMessage(rareDropBlocks.toString());
        Bukkit.getServer().broadcastMessage(rareDrops.toString());

        Bukkit.broadcastMessage(affectedItems.toString() + prefixes.toString() + baseNames.toString() + suffixes.toString() + modifiers.toString());
        //Get type list and make get-able from array
        for (String type : config.getStringList("Types")) {
            type = type.replace("&", "§");
            types.add(type);
        }
        for (String mobType : config.getConfigurationSection("EXPPerMobKill").getKeys(false)) {
            expPerMobKill.put(mobType, config.getInt("EXPPerMobKill." + mobType));
        }
    }

    public List<String> getAffectedItems() {
        return affectedItems;
    }
    public List<String> getTypes() {
        return types;
    }

    public HashMap<String, List<String>> getPrefixes() {
        return prefixes;
    }

    public HashMap<String, List<String>> getBaseNames() {
        return baseNames;
    }

    public HashMap<String, List<String>> getSuffixes() {
        return suffixes;
    }

    public HashMap<String, List<String>> getRareDropBlocks() {
        return rareDropBlocks;
    }

    public HashMap<String, Map<Integer, String>> getRareDrops() {
        return rareDrops;
    }

    public HashMap<String, String> getModifiers() {
        return modifiers;
    }

    public HashMap<String, Double> getMultipliers() {
        return multipliers;
    }

    public HashMap<String, Integer> getMaxEXPAdd() {
        return maxEXPAdd;
    }

    public HashMap<String, Integer> getBaseEXP() {
        return baseEXP;
    }

    public Integer getExpPerMobKill(LivingEntity e) {
        return expPerMobKill.get(e.getType().toString());
    }

    public boolean isItemInConfig(ItemStack i) {
        for (String testItemMaterial : affectedItems) {
            Material testMaterial = Material.getMaterial(testItemMaterial);
            if (testMaterial != null && testMaterial == i.getType()) {
                return true;
            }
        }
        return false;
    }

    public String getNameVariant(ItemStack i) {
        //Check if should rename (if does not have display name already)
        if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
            return i.getItemMeta().getDisplayName();
        } else {
            String itemKey = i.getType().toString();
            String prefix = new String();
            String baseName = new String();
            String suffix = new String();
            boolean isPrefix;
            boolean isBaseName;
            boolean isSuffix;
            List<String> possiblePrefixes = new ArrayList<String>();
            List<String> possibleBaseNames = new ArrayList<String>();
            List<String> possibleSuffixes = new ArrayList<String>();
            if (prefixes.containsKey(itemKey) && !prefixes.get(itemKey).isEmpty()) {
                possiblePrefixes = prefixes.get(itemKey);
                isPrefix = true;
            } else {
                isPrefix = false;
            }
            if (baseNames.containsKey(itemKey) && !baseNames.get(itemKey).isEmpty()) {
                possibleBaseNames = baseNames.get(itemKey);
                isBaseName = true;
            } else {
                isBaseName = false;
            }
            if (suffixes.containsKey(itemKey) && !suffixes.get(itemKey).isEmpty()) {
                possibleSuffixes = suffixes.get(itemKey);
                isSuffix = true;
            } else {
                isSuffix = false;
            }

            //Chooses random value of each
            if (isPrefix) {
                prefix = possiblePrefixes.get((int) (Math.random() * (possiblePrefixes.size())));
            }
            if (isBaseName) {
                baseName = possibleBaseNames.get((int) (Math.random() * (possibleBaseNames.size())));
            }
            if (isSuffix) {
                suffix = possibleSuffixes.get((int) (Math.random() * (possibleSuffixes.size())));
            }
            String newName = "§f" + prefix + " " + baseName + " " + suffix;
            return newName.trim();
        }
    }

    public String getModifierType(ItemStack item) {
        return modifiers.get(item.getType().toString());
    }

    //Return actual modifier text for use in lore
    public String getModifier(ItemStack item, boolean LevelUp) {
        String type = getModifierType(item);
        String modifierText = new String();
        switch (type) {
            case "damage":
                DecimalFormat df = new DecimalFormat("0.0");
                String newDamage;
                if (LevelUp) {
                    newDamage = df.format(((getLevel(item)) * getMultiplier(item)));
                } else {
                    newDamage = df.format(((getLevel(item) - 1) * getMultiplier(item)));
                }
                modifierText = "§5+" + newDamage + " Damage";
                return modifierText;
            case "luck":
                int newLuck;
                if (LevelUp) {
                    newLuck = (int) (getLevel(item) * getMultiplier(item));
                } else {
                    newLuck = (int) ((getLevel(item) - 1) * getMultiplier(item));
                }
                modifierText = "§5" + String.valueOf(newLuck) + " Luck";
                return modifierText;
        }
        return modifierText;
    }
    public int getLevel(ItemStack i) {
        if (i.hasItemMeta() && i.getItemMeta().hasLore()) {
            List<String> l = i.getItemMeta().getLore();
            for (String line : l) {
                line = ChatColor.stripColor(line);
                if (line.contains("Level:")) {
                    String[] levelPossible = line.split(" ");
                    if (levelPossible.length >= 2) {
                        return Integer.parseInt(levelPossible[1]);
                    }
                }
            }
        }
        return 0;
    }

    public Double getMultiplier(ItemStack i) {
        if (multipliers.containsKey(i.getType().toString())) {
            return multipliers.get(i.getType().toString());
        } else {
            return 0.0;
        }
    }
}
