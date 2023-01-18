package de.javabasti.iostein.itemfilter;

import de.javabasti.iostein.itemfilter.events.HopperInteractEvent;
import de.javabasti.iostein.itemfilter.events.InsideHopperInteractEvent;
import de.javabasti.iostein.itemfilter.utils.InventoryManager;
import de.javabasti.iostein.itemfilter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemFilter extends JavaPlugin {

    private static ItemFilter instance;
    private static Utils utils;
    private static InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        instance = this;
        utils = new Utils();
        inventoryManager = new InventoryManager();
        System.out.println("ItemFilter wurde erfolgreich aktiviert.");
        registerEvents();
    }

    @Override
    public void onDisable() {
        System.out.println("ItemFilter wurde erfolgreich deaktiviert.");

    }

    private void registerEvents(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new HopperInteractEvent(this), this);
        pluginManager.registerEvents(new InsideHopperInteractEvent(this), this);
    }

    public static ItemFilter getInstance() {
        return instance;
    }
    public static Utils getUtils() {return utils;}
    public static InventoryManager getInventoryManager() {return inventoryManager;}
}
