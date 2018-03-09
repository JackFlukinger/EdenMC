package org.edenmc.kingdoms.customitems;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.Kingdoms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2/11/2018.
 */
public class CustomItem {
    String displayName;
    String type;
    ItemMeta meta;
    List<String> lore;
    double multiplier;
    int level;
    int baseEXP;
    int maxEXPAdd;
    int EXP;
    ItemStack item;
    String modifierType;
    String modifier;


    public CustomItem(ItemStack i) {
            meta = i.getItemMeta();
            displayName = loadName(i);
            lore = meta.getLore();
            multiplier = loadMultiplier(i);
            level = loadLevel(i);
            baseEXP = loadBaseEXP(i);
            maxEXPAdd = loadMaxEXPAdd(i);
            EXP = loadEXP(i);
            modifierType = loadModifierType(i);
            modifier = loadModifier(i);
            item = i;
            type = loadType();
            lore = updateLore();
    }

    private String loadName(ItemStack i) {
        if (Kingdoms.getCIConf().getAffectedItems().contains(i.getType().toString())) {
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                return i.getItemMeta().getDisplayName();
            }
            int numPrefixes = Kingdoms.getCIConf().getPrefixes().get(i.getType().toString()).size();
            String prefix = Kingdoms.getCIConf().getPrefixes().get(i.getType().toString()).get((int) (Math.random() * numPrefixes));
            int numBases = Kingdoms.getCIConf().getBaseNames().get(i.getType().toString()).size();
            String base = Kingdoms.getCIConf().getBaseNames().get(i.getType().toString()).get((int) (Math.random() * numBases));
            int numSuffixes = Kingdoms.getCIConf().getSuffixes().get(i.getType().toString()).size();
            String suffix = Kingdoms.getCIConf().getSuffixes().get(i.getType().toString()).get((int) (Math.random() * numSuffixes));
            if (suffix.equals("")) {
                return prefix + " " + base;
            }

            if (base.equals("")) {
                return prefix + " " + suffix;
            }
            return prefix + " " + base + " " + suffix;
        }
        return i.getItemMeta().getDisplayName();
    }

    public String loadType() {
        if (level < 5) {
            return Kingdoms.getCIConf().getTypes().get(0);
        } else if (level < 15) {
            return Kingdoms.getCIConf().getTypes().get(1);
        } else if (level < 25) {
            return Kingdoms.getCIConf().getTypes().get(2);
        } else if (level < 50) {
            return Kingdoms.getCIConf().getTypes().get(3);
        } else if (level < 75) {
            return Kingdoms.getCIConf().getTypes().get(4);
        } else {
            return Kingdoms.getCIConf().getTypes().get(5);
        }
    }

    public List<String> updateLore() {
        String typeS = type;
        int maxEXP = baseEXP + (maxEXPAdd * (level - 1));
        String levelEXP = "&aLevel: &3" + level + " &4| &aEXP: &3" + EXP + "&a/&3" + maxEXP;
        levelEXP = levelEXP.replace("&","§");
        String mod = modifier;
        List<String> newLore = new ArrayList<String>();
        newLore.add(0,typeS);
        newLore.add(1,levelEXP);
        newLore.add(2,mod);
        return newLore;
    }

    public int loadEXP(ItemStack i) {
        if (Kingdoms.getCIConf().getAffectedItems().contains(i.getType().toString())) {
            if (i.hasItemMeta() && i.getItemMeta().hasLore()) {
                List<String> l = i.getItemMeta().getLore();
                for (String line : l) {
                    if (line.contains("EXP: ")) {
                        String nl = ChatColor.stripColor(line);
                        return Integer.parseInt(nl.split(" ")[4].split("/")[0]);
                    }
                }
            }
        }
        return 0;
    }

    public String loadModifierType(ItemStack item) {
        return Kingdoms.getCIConf().getModifiers().get(item.getType().toString());
    }

    //Return actual modifier text for use in lore
    public String loadModifier(ItemStack item) {
        String type = modifierType;
        String modifierText = new String();
        switch (type) {
            case "damage":
                DecimalFormat df = new DecimalFormat("0.0");
                String newDamage = df.format((level - 1) * multiplier);
                modifierText = "§5+" + newDamage + " Damage";
                return modifierText;
            case "luck":
                int newLuck = (int) ((level - 1) * multiplier);
                modifierText = "§5" + String.valueOf(newLuck) + " Luck";
                return modifierText;
            case "dodge":
                int newDodge = (int) ((level - 1) * multiplier);
                modifierText = "§5" + String.valueOf(newDodge) + "% Dodge";
                return modifierText;
        }
        return modifierText;
    }
    private int loadLevel(ItemStack i) {
        if (Kingdoms.getCIConf().getAffectedItems().contains(i.getType().toString())) {
            if (i.hasItemMeta() && i.getItemMeta().hasLore()) {
                List<String> l = i.getItemMeta().getLore();
                for (String line : l) {
                    if (line.contains("Level: ")) {
                        String nl = ChatColor.stripColor(line);
                        return Integer.parseInt(nl.split(" ")[1]);
                    }
                }
            }
        }
        return 1;
    }

    private int loadBaseEXP(ItemStack i) {
        if (Kingdoms.getCIConf().getBaseEXP().containsKey(i.getType().toString())) {
            return Kingdoms.getCIConf().getBaseEXP().get(i.getType().toString());
        }
        return 0;
    }

    private int loadMaxEXPAdd(ItemStack i) {
        if (Kingdoms.getCIConf().getMaxEXPAdd().containsKey(i.getType().toString())) {
            return Kingdoms.getCIConf().getMaxEXPAdd().get(i.getType().toString());
        }
        return 0;
    }

    private Double loadMultiplier(ItemStack i) {
        if (Kingdoms.getCIConf().getMultipliers().containsKey(i.getType().toString())) {
            return Kingdoms.getCIConf().getMultipliers().get(i.getType().toString());
        } else {
            return 0.0;
        }
    }

    public void addEXP(int exp) {
        int maxEXP = baseEXP + ((level - 1) * maxEXPAdd);
        if (EXP + exp < maxEXP) {
            EXP = EXP + exp;
            lore = updateLore();
            meta.setLore(lore);
            item.setItemMeta(meta);
        } else {
            EXP = exp - (maxEXP - EXP);
            level++;
            modifier = loadModifier(item);
            type = loadType();
            lore = updateLore();
        }

    }


    //Create and return the new item
    public ItemStack create() {
        ItemStack newItem = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        meta.setDisplayName(displayName);
        newItem.setItemMeta(meta);
        return newItem;
    }
}
