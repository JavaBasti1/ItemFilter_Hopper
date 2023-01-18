package de.javabasti.iostein.itemfilter.events;

import de.javabasti.iostein.itemfilter.ItemFilter;
import de.javabasti.iostein.itemfilter.utils.InventoryManager;
import de.javabasti.iostein.itemfilter.utils.ItemBuilder;
import de.javabasti.iostein.itemfilter.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class HopperInteractEvent implements Listener {

    ItemFilter plugin;

    public HopperInteractEvent(ItemFilter instance) {
        plugin = instance;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Utils utils = ItemFilter.getUtils();
        InventoryManager inventoryManager = ItemFilter.getInventoryManager();

        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking()) {
            if (Objects.equals(Objects.requireNonNull(event.getClickedBlock()).getType(), Material.HOPPER)) {
                event.setCancelled(true);

                /*

                PersistentData des Hoppers umändern, um den State sowohl als auch die Items in dem Block zu speichern
                Inventar zum ändern des Hopper-Filter-Modi erstellen und konfigurieren

                */

                Utils.ClickEvent click = new Utils.ClickEvent();
                click.location = event.getClickedBlock().getLocation();
                click.time = System.currentTimeMillis();
                utils.lastClickedBlock.put(player.getUniqueId().toString(), click);

                TileState state = (TileState) event.getClickedBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                List<NamespacedKey> HopperModeItemsState = List.of(utils.hopperForbiddenState, utils.hopperAllowedState);
                List<NamespacedKey> ForbiddenItemKeys = List.of(utils.hopperForbiddenItem1, utils.hopperForbiddenItem2, utils.hopperForbiddenItem3, utils.hopperForbiddenItem4, utils.hopperForbiddenItem5);
                List<NamespacedKey> AllowedItemKeys = List.of(utils.hopperAllowedItem1, utils.hopperAllowedItem2, utils.hopperAllowedItem3, utils.hopperAllowedItem4, utils.hopperAllowedItem5);

                for (NamespacedKey key : HopperModeItemsState) {
                    if (!container.has(key, PersistentDataType.STRING)) {
                        container.set(key, PersistentDataType.STRING, "§cDeaktiviert");
                    }
                }

                for (NamespacedKey key : ForbiddenItemKeys) {
                    if (!container.has(key, PersistentDataType.STRING)) {
                        container.set(key, PersistentDataType.STRING, "AIR");
                    }
                    if (!player.hasPermission("Slot.Alpha") && key.equals(utils.hopperForbiddenItem4)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                    if (!player.hasPermission("Slot.Pro") && key.equals(utils.hopperForbiddenItem5)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                }

                for (NamespacedKey key : AllowedItemKeys) {
                    if (!container.has(key, PersistentDataType.STRING)) {
                        container.set(key, PersistentDataType.STRING, "AIR");
                    }
                    if (!player.hasPermission("Slot.Alpha") && key.equals(utils.hopperAllowedItem4)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                    if (!player.hasPermission("Slot.Pro") && key.equals(utils.hopperAllowedItem5)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                }

                state.update();

                inventoryManager.createInventory("FilterInventory", 27, "§8≫ §eTrichter§8-§6Filter");

                ItemStack glass = (new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "§a ")).build();
                for (int i = 0; i < inventoryManager.getCreatedInventory("FilterInventory").getSize(); i++) {
                    if (inventoryManager.getCreatedInventory("FilterInventory").getItem(i) == null) {
                        inventoryManager.setItem("FilterInventory",  i, glass);
                    }
                }


                String stringForbiddenState = container.get(utils.hopperForbiddenState, PersistentDataType.STRING);
                String stringAllowedState = container.get(utils.hopperAllowedState, PersistentDataType.STRING);

                List<String> ForbiddenItemsLore = new ArrayList<>();
                ForbiddenItemsLore.add("§7Filtere hier nach Items, welche §cnicht §7eingesammelt werden sollen.");
                ForbiddenItemsLore.add("§7Alle anderen Items werden eingesammelt.");
                ForbiddenItemsLore.add("§a ");
                ForbiddenItemsLore.add("§7State: " + stringForbiddenState);
                ForbiddenItemsLore.add("§e ");
                if (Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§cDeaktiviert")) {
                    ForbiddenItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");
                }else{
                    ForbiddenItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");
                }

                ItemStack ForbiddenItemsItem;
                if (Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§aAktiviert")) {
                    ForbiddenItemsItem = (new ItemBuilder(Material.TNT_MINECART, 1,
                            "§cVerbotene Items").glow().lore(ForbiddenItemsLore)).build();
                } else {
                    ForbiddenItemsItem = (new ItemBuilder(Material.TNT_MINECART,
                            1,
                            "§cVerbotene Items").lore(ForbiddenItemsLore)).build();
                }

                List<String> AllowedItemsLore = new ArrayList<>();
                AllowedItemsLore.add("§7Filtere hier Items, die §aeingesammelt §7werden sollen.");
                AllowedItemsLore.add("§7Alle anderen Items werden in dieser Option nicht eingesammelt.");
                AllowedItemsLore.add("§a ");
                AllowedItemsLore.add("§7State: " + stringAllowedState);
                AllowedItemsLore.add("§e ");
                if (Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§cDeaktiviert")) {
                    AllowedItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");
                }else {
                    AllowedItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");
                }

                ItemStack AllowedItemsItem;
                if (Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§aAktiviert")) {
                    AllowedItemsItem = (new ItemBuilder(Material.CHEST_MINECART,
                            1,
                            "§aErlaubte Items").glow().lore(AllowedItemsLore).glow()).build();
                } else {
                    AllowedItemsItem = (new ItemBuilder(Material.CHEST_MINECART,
                            1,
                            "§aErlaubte Items").lore(AllowedItemsLore)).build();
                }

                ItemStack abschaltenItem = (new ItemBuilder(Material.BARRIER,
                        1,
                        "§cAbschalten")).build();

                inventoryManager.setItem("FilterInventory", 10, ForbiddenItemsItem);
                inventoryManager.setItem("FilterInventory", 12, AllowedItemsItem);
                inventoryManager.setItem("FilterInventory", 16, abschaltenItem);

                player.openInventory(inventoryManager.getCreatedInventory("FilterInventory"));
            }
        }
    }

    @EventHandler
    public void onHopperEvent(InventoryMoveItemEvent event) {

        Utils utils = ItemFilter.getUtils();
        // Inventory sourceInventory = event.getSource();
        Inventory destinationInventory = event.getDestination();

        /*

                Items die zu dem jeweiligen Hopper kommen filtern und/oder nicht weiterleiten

        */

        if (destinationInventory.getHolder() instanceof Hopper) {
            TileState state = (TileState) destinationInventory.getHolder();
            PersistentDataContainer container = state.getPersistentDataContainer();

            String hopperAllowedItem1 = container.get(utils.hopperAllowedItem1, PersistentDataType.STRING);
            String hopperAllowedItem2 = container.get(utils.hopperAllowedItem2, PersistentDataType.STRING);
            String hopperAllowedItem3 = container.get(utils.hopperAllowedItem3, PersistentDataType.STRING);
            String hopperAllowedItem4 = container.get(utils.hopperAllowedItem4, PersistentDataType.STRING);
            String hopperAllowedItem5 = container.get(utils.hopperAllowedItem5, PersistentDataType.STRING);

            String hopperForbiddenItem1 = container.get(utils.hopperForbiddenItem1, PersistentDataType.STRING);
            String hopperForbiddenItem2 = container.get(utils.hopperForbiddenItem2, PersistentDataType.STRING);
            String hopperForbiddenItem3 = container.get(utils.hopperForbiddenItem3, PersistentDataType.STRING);
            String hopperForbiddenItem4 = container.get(utils.hopperForbiddenItem4, PersistentDataType.STRING);
            String hopperForbiddenItem5 = container.get(utils.hopperForbiddenItem5, PersistentDataType.STRING);

            if (Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§cDeaktiviert")
                    && Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§aAktiviert")) {
                if (Objects.equals(event.getItem().getType().toString(), hopperForbiddenItem1) ||
                        Objects.equals(event.getItem().getType().toString(), hopperForbiddenItem2) ||
                        Objects.equals(event.getItem().getType().toString(), hopperForbiddenItem3) ||
                        Objects.equals(event.getItem().getType().toString(), hopperForbiddenItem4) ||
                        Objects.equals(event.getItem().getType().toString(), hopperForbiddenItem5)) {
                    event.setCancelled(true);
                }
            } else if (Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§aAktiviert")
                    && Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§cDeaktiviert")) {
                if (!(Objects.equals(event.getItem().getType().toString(), hopperAllowedItem1) ||
                        Objects.equals(event.getItem().getType().toString(), hopperAllowedItem2) ||
                        Objects.equals(event.getItem().getType().toString(), hopperAllowedItem3) ||
                        Objects.equals(event.getItem().getType().toString(), hopperAllowedItem4) ||
                        Objects.equals(event.getItem().getType().toString(), hopperAllowedItem5))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Utils utils = ItemFilter.getUtils();
        if (utils.isInventoryClosedAfterClick()) {
            utils.setInventoryClosedAfterClick(false);
        } else {
            long current = System.currentTimeMillis();
            HumanEntity player = event.getPlayer();
            Utils.ClickEvent click = utils.lastClickedBlock.get(player.getUniqueId().toString());
            if (click != null && current - click.time >= 25L)
                utils.lastClickedBlock.remove(player.getUniqueId().toString());
        }
    }
}
