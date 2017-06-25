package org.edenmc.kingdoms.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.edenmc.kingdoms.Kingdoms;
import org.edenmc.kingdoms.MySQL;

/**
 * Created by Jack on 6/21/2017.
 */
public class GoldFunctions {

    public void payPlayer(Player sender, Player recipient, Integer amount) {
        Integer increasedBalance = Kingdoms.playerGold.get(recipient) + amount;
        Integer decreasedBalance = Kingdoms.playerGold.get(sender) - amount;
        setBalance(recipient, increasedBalance);
        setBalance(sender,decreasedBalance);
    }

    //Gets player balance from MySQL
    public static Integer getBalance(Player p) {
        String string = MySQL.getData("players","uuid","balance",p.getUniqueId().toString());
        if (string == "") {
            setBalance(p,0);
            return 0;
        }
        return Integer.parseInt(string);
    }

    //Sets player balance in MySQL and hashmap
    public static void setBalance(Player p, Integer balance) {
        String[] data = {p.getUniqueId().toString(), String.valueOf(balance)};
        String[] columns = {"uuid","balance"};
        MySQL.enterData("players",columns,data);
        Kingdoms.playerGold.put(p,balance);
        SatchelHandler.setSatchelBalance(p,balance);
    }

    //Checks if ItemStack is Gold Pieces
    public static boolean isGold(ItemStack item) {
        if (item != null && item.getType() == Material.SHEARS) {
            if (item.hasItemMeta() && item.getItemMeta().isUnbreakable()) {
                if (item.getItemMeta().getDisplayName().equals("§rGold Piece")) {
                    return true;
                }
            }
        }
        return false;
    }

    //Gets gold item in specified amount
    public static ItemStack getGoldItem(Integer amount) {

        ItemStack gold = new ItemStack(Material.SHEARS, amount);
        gold.setDurability((short) 2);
        ItemMeta meta = gold.getItemMeta();
        meta.setDisplayName("§rGold Piece");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        gold.setItemMeta(meta);
        return gold;

    }

}
