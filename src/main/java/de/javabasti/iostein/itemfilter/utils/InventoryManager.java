package de.javabasti.iostein.itemfilter.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private final Map<String, Inventory> hopperInventories = new HashMap<>();

    public void createInventory(String inventoryName, int inventorySize, String inventoryTitle){
        Inventory createdInventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
        hopperInventories.put(inventoryName, createdInventory);
    }

    public Inventory getCreatedInventory(String inventoryName){
        return hopperInventories.get(inventoryName);
    }

    public void setItem(String inventoryName, int inventorySlot, ItemStack item){
        Inventory createdInventory = hopperInventories.get(inventoryName);
        createdInventory.setItem(inventorySlot, item);

    }
}
