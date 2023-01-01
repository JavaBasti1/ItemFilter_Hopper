package de.javabasti.iostein.itemfilter.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperInventory {

    public static Inventory hopperInv;

    public static void createInventory(Player player){
        hopperInv = Bukkit.createInventory(player, 27, "§8≫ §eTrichter§8-§6Filter");
    }

    public static void setInventoryItems(int slot, ItemStack item){
        hopperInv.setItem(slot, item);
    }
    public static void openInventory(Player player){
        player.openInventory(hopperInv);
    }
    public static Inventory getInventory(){ return hopperInv;}
}
