package de.javabasti.iostein.itemfilter.events;

import de.javabasti.iostein.itemfilter.ItemFilter;
import de.javabasti.iostein.itemfilter.utils.HopperInventory;
import de.javabasti.iostein.itemfilter.utils.ItemBuilder;
import org.bukkit.Location;
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
import java.util.concurrent.ConcurrentHashMap;

public class HopperInteractEvent implements Listener {

    ItemFilter plugin;

    public HopperInteractEvent(ItemFilter instance) {
        plugin = instance;
    }
    public static boolean inventoryClosedAfterClick = false;

    public static final Map<String, ClickEvent> lastClickedBlock = new ConcurrentHashMap<>();


    // Event zum speichern der Location & der Zeit des angeklickten Blockes für weitere Aktionen
    public static class ClickEvent {
        public long time;

        public Location location;

        private ClickEvent() {
        }
    }

    NamespacedKey hopperVerbotenStatus = new NamespacedKey(ItemFilter.getInstance(), "hopperVerbotenStatus");
    NamespacedKey hopperErlaubtStatus = new NamespacedKey(ItemFilter.getInstance(), "hopperErlaubtStatus");

    NamespacedKey hopperErlaubtesItem1 = new NamespacedKey(ItemFilter.getInstance(), "hopperErlaubtesItem1");
    NamespacedKey hopperErlaubtesItem2 = new NamespacedKey(ItemFilter.getInstance(), "hopperErlaubtesItem2");
    NamespacedKey hopperErlaubtesItem3 = new NamespacedKey(ItemFilter.getInstance(), "hopperErlaubtesItem3");
    NamespacedKey hopperErlaubtesItem4 = new NamespacedKey(ItemFilter.getInstance(), "hopperErlaubtesItem4");
    NamespacedKey hopperErlaubtesItem5 = new NamespacedKey(ItemFilter.getInstance(), "hopperErlaubtesItem5");

    NamespacedKey hopperVerbotenesItem1 = new NamespacedKey(ItemFilter.getInstance(), "hopperVerbotenesItem1");
    NamespacedKey hopperVerbotenesItem2 = new NamespacedKey(ItemFilter.getInstance(), "hopperVerbotenesItem2");
    NamespacedKey hopperVerbotenesItem3 = new NamespacedKey(ItemFilter.getInstance(), "hopperVerbotenesItem3");
    NamespacedKey hopperVerbotenesItem4 = new NamespacedKey(ItemFilter.getInstance(), "hopperVerbotenesItem4");
    NamespacedKey hopperVerbotenesItem5 = new NamespacedKey(ItemFilter.getInstance(), "hopperVerbotenesItem5");


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking()) {
            if (Objects.equals(Objects.requireNonNull(event.getClickedBlock()).getType(), Material.HOPPER)) {
                event.setCancelled(true);

                /*

                PersistentData des Hoppers umändern, um den Status sowohl als auch die Items in dem Block zu speichern
                Inventar zum ändern des Hopper-Filter-Modi erstellen und konfigurieren

                */

                ClickEvent click = new ClickEvent();
                click.location = event.getClickedBlock().getLocation();
                click.time = System.currentTimeMillis();
                lastClickedBlock.put(player.getUniqueId().toString(), click);
                TileState state = (TileState) event.getClickedBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                String stringVerbotenStatus = container.get(hopperVerbotenStatus, PersistentDataType.STRING);
                String stringErlaubtStatus = container.get(hopperErlaubtStatus, PersistentDataType.STRING);

                List<NamespacedKey> VerboteneItemsStatus = List.of(hopperVerbotenStatus, hopperErlaubtStatus);
                List<NamespacedKey> VerboteneItemKeys = List.of(hopperVerbotenesItem1, hopperVerbotenesItem2, hopperVerbotenesItem3, hopperVerbotenesItem4, hopperVerbotenesItem5);
                List<NamespacedKey> ErlaubteItemKeys = List.of(hopperErlaubtesItem1, hopperErlaubtesItem2, hopperErlaubtesItem3, hopperErlaubtesItem4, hopperErlaubtesItem5);

                for (NamespacedKey key : VerboteneItemsStatus) {
                    if (!container.has(key, PersistentDataType.STRING)) {
                        container.set(key, PersistentDataType.STRING, "§cDeaktiviert");
                    }
                }

                for (NamespacedKey key : VerboteneItemKeys) {
                    if (!container.has(key, PersistentDataType.STRING)) {
                        container.set(key, PersistentDataType.STRING, "AIR");
                    }
                    if (!player.hasPermission("Slot.Alpha") && key.equals(hopperVerbotenesItem4)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                    if (!player.hasPermission("Slot.Pro") && key.equals(hopperVerbotenesItem5)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                }

                for (NamespacedKey key : ErlaubteItemKeys) {
                    if (!container.has(key, PersistentDataType.STRING)) {
                        container.set(key, PersistentDataType.STRING, "AIR");
                    }
                    if (!player.hasPermission("Slot.Alpha") && key.equals(hopperErlaubtesItem4)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                    if (!player.hasPermission("Slot.Pro") && key.equals(hopperErlaubtesItem5)) {
                        container.set(key, PersistentDataType.STRING, "BARRIER");
                    }
                }

                state.update();

                HopperInventory.createInventory(player);

                ItemStack glass = (new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "§a ")).build();
                for (int i = 0; i < HopperInventory.getInventory().getSize(); i++) {
                    if (HopperInventory.getInventory().getItem(i) == null) {
                        HopperInventory.setInventoryItems(i, glass);
                    }
                }

                List<String> verboteneItemsLore = new ArrayList<>();
                verboteneItemsLore.add("§7Filtere hier nach Items, welche §cnicht §7eingesammelt werden sollen.");
                verboteneItemsLore.add("§7Alle anderen Items werden eingesammelt.");
                verboteneItemsLore.add("§a ");
                verboteneItemsLore.add("§7Status: " + stringVerbotenStatus);
                verboteneItemsLore.add("§e ");
                if (Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                    verboteneItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");
                }else{
                    verboteneItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");
                }

                ItemStack verboteneItemsItem;
                if (Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§aAktiviert")) {
                    verboteneItemsItem = (new ItemBuilder(Material.TNT_MINECART, 1,
                            "§cVerbotene Items").glow().lore(verboteneItemsLore)).build();
                } else {
                    verboteneItemsItem = (new ItemBuilder(Material.TNT_MINECART,
                            1,
                            "§cVerbotene Items").lore(verboteneItemsLore)).build();
                }

                List<String> erlaubteItemsLore = new ArrayList<>();
                erlaubteItemsLore.add("§7Filtere hier Items, die §aeingesammelt §7werden sollen.");
                erlaubteItemsLore.add("§7Alle anderen Items werden in dieser Option nicht eingesammelt.");
                erlaubteItemsLore.add("§a ");
                erlaubteItemsLore.add("§7Status: " + stringErlaubtStatus);
                erlaubteItemsLore.add("§e ");
                if (Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                    erlaubteItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");
                }else {
                    erlaubteItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");
                }

                ItemStack erlaubteItemsItem;
                if (Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§aAktiviert")) {
                    erlaubteItemsItem = (new ItemBuilder(Material.CHEST_MINECART,
                            1,
                            "§aErlaubte Items").glow().lore(erlaubteItemsLore).glow()).build();
                } else {
                    erlaubteItemsItem = (new ItemBuilder(Material.CHEST_MINECART,
                            1,
                            "§aErlaubte Items").lore(erlaubteItemsLore)).build();
                }

                ItemStack abschaltenItem = (new ItemBuilder(Material.BARRIER,
                        1,
                        "§cAbschalten")).build();

                HopperInventory.setInventoryItems(10, verboteneItemsItem);
                HopperInventory.setInventoryItems(12, erlaubteItemsItem);
                HopperInventory.setInventoryItems(16, abschaltenItem);

                HopperInventory.openInventory(player);
            }
        }
    }

    @EventHandler
    public void onHopperEvent(InventoryMoveItemEvent event) {

        // Inventory sourceInventory = event.getSource();
        Inventory destinationInventory = event.getDestination();

        /*

                Items die zu dem jeweiligen Hopper kommen filtern und/oder nicht weiterleiten

        */

        if (destinationInventory.getHolder() instanceof Hopper) {
            TileState state = (TileState) destinationInventory.getHolder();
            PersistentDataContainer container = state.getPersistentDataContainer();

            String stringHopperErlaubtesItem1 = container.get(hopperErlaubtesItem1, PersistentDataType.STRING);
            String stringHopperErlaubtesItem2 = container.get(hopperErlaubtesItem2, PersistentDataType.STRING);
            String stringHopperErlaubtesItem3 = container.get(hopperErlaubtesItem3, PersistentDataType.STRING);
            String stringHopperErlaubtesItem4 = container.get(hopperErlaubtesItem4, PersistentDataType.STRING);
            String stringHopperErlaubtesItem5 = container.get(hopperErlaubtesItem5, PersistentDataType.STRING);

            String stringHopperVerbotenesItem1 = container.get(hopperVerbotenesItem1, PersistentDataType.STRING);
            String stringHopperVerbotenesItem2 = container.get(hopperVerbotenesItem2, PersistentDataType.STRING);
            String stringHopperVerbotenesItem3 = container.get(hopperVerbotenesItem3, PersistentDataType.STRING);
            String stringHopperVerbotenesItem4 = container.get(hopperVerbotenesItem4, PersistentDataType.STRING);
            String stringHopperVerbotenesItem5 = container.get(hopperVerbotenesItem5, PersistentDataType.STRING);

            if (Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§cDeaktiviert")
                    && Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§aAktiviert")) {
                if (Objects.equals(event.getItem().getType().toString(), stringHopperVerbotenesItem1) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperVerbotenesItem2) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperVerbotenesItem3) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperVerbotenesItem4) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperVerbotenesItem5)) {
                    event.setCancelled(true);
                }
            } else if (Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§aAktiviert")
                    && Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                if (!(Objects.equals(event.getItem().getType().toString(), stringHopperErlaubtesItem1) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperErlaubtesItem2) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperErlaubtesItem3) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperErlaubtesItem4) ||
                        Objects.equals(event.getItem().getType().toString(), stringHopperErlaubtesItem5))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (inventoryClosedAfterClick) {
            HopperInteractEvent.inventoryClosedAfterClick = false;
        } else {
            long current = System.currentTimeMillis();
            HumanEntity player = event.getPlayer();
            ClickEvent click = lastClickedBlock.get(player.getUniqueId().toString());
            if (click != null && current - click.time >= 25L)
                lastClickedBlock.remove(player.getUniqueId().toString());
        }
    }
}
