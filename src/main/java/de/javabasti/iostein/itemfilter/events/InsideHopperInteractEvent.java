package de.javabasti.iostein.itemfilter.events;

import de.javabasti.iostein.itemfilter.ItemFilter;
import de.javabasti.iostein.itemfilter.utils.ErlaubteItemsInventory;
import de.javabasti.iostein.itemfilter.utils.HopperInventory;
import de.javabasti.iostein.itemfilter.utils.ItemBuilder;
import de.javabasti.iostein.itemfilter.utils.VerboteneItemsInventory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InsideHopperInteractEvent implements Listener {
    ItemFilter plugin;

    public InsideHopperInteractEvent(ItemFilter instance) {
        plugin = instance;
    }

    private final int startingSlot = 12;
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
    public void onInventoryInteractEvent(InventoryInteractEvent event) {
        if (event.getInventory().equals(VerboteneItemsInventory.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInsideInventoryClickEvent(InventoryClickEvent event) {

                /*

                Hinzufügen / Entfernen von den verbotenen Items aus dem Hopper, sowie Überprüfung & überarbeitung der Persistentdata

                */

        if (event.getInventory().equals(VerboteneItemsInventory.getInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                TileState state = (TileState) HopperInteractEvent.lastClickedBlock.get(player.getUniqueId().toString()).location.getBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                int endSlot = 16;
                if (Objects.equals(event.getClickedInventory(), VerboteneItemsInventory.getInventory())) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.BARRIER) || item.getType().equals(Material.AIR)) {
                        if (event.getSlot() >= startingSlot && event.getSlot() <= endSlot) {
                            VerboteneItemsInventory.setInventoryItems(event.getSlot(), new ItemStack(Material.AIR));
                            container.set(getHopperVerbotenesItemKey(event.getSlot()),PersistentDataType.STRING, "AIR");
                            state.update();
                        }
                    }
                    event.setCancelled(true);
                }
                if (Objects.equals(event.getClickedInventory(), player.getInventory())) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.AIR)) {
                        for (int i = startingSlot; i <= endSlot; i++) {
                            if (VerboteneItemsInventory.getInventory().getItem(i) == null) {
                                VerboteneItemsInventory.setInventoryItems(i, new ItemStack(item.getType(), 1));
                                container.set(getHopperVerbotenesItemKey(i), PersistentDataType.STRING, item.getType().toString());
                                state.update();
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
                /*

                Hinzufügen / Entfernen von den erlaubten Items aus dem Hopper, sowie Überprüfung & überarbeitung der Persistentdata

                */


        if (event.getInventory().equals(ErlaubteItemsInventory.getInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                TileState state = (TileState) HopperInteractEvent.lastClickedBlock.get(player.getUniqueId().toString()).location.getBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                int endSlot = 16;
                if (Objects.equals(event.getClickedInventory(), ErlaubteItemsInventory.getInventory())) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.BARRIER) || item.getType().equals(Material.AIR)) {
                        if (event.getSlot() >= startingSlot && event.getSlot() <= endSlot) {
                            ErlaubteItemsInventory.setInventoryItems(event.getSlot(), new ItemStack(Material.AIR));
                            container.set(getHopperErlaubtesItemKey(event.getSlot()),PersistentDataType.STRING, "AIR");
                            state.update();
                        }
                    }
                    event.setCancelled(true);
                }
                if (Objects.equals(event.getClickedInventory(), player.getInventory())) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.AIR)) {
                        for (int i = startingSlot; i <= endSlot; i++) {
                            if (ErlaubteItemsInventory.getInventory().getItem(i) == null) {
                                ErlaubteItemsInventory.setInventoryItems(i, new ItemStack(item.getType(), 1));
                                container.set(getHopperErlaubtesItemKey(i), PersistentDataType.STRING, item.getType().toString());
                                state.update();
                                event.setCancelled(true);
                                break;
                            }
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getInventory().equals(HopperInventory.getInventory())) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                Player player = (Player) event.getWhoClicked();
                TileState state = (TileState) HopperInteractEvent.lastClickedBlock.get(player.getUniqueId().toString()).location.getBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                /*

                Verbotene Items inventory erstellen und konfigurieren

                */

                if (event.getCurrentItem().getType().equals(Material.TNT_MINECART) &&
                        event.getCurrentItem().getItemMeta().getDisplayName().equals("§cVerbotene Items")) {
                    HopperInteractEvent.inventoryClosedAfterClick = true;

                    if (Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                        container.set(hopperVerbotenStatus, PersistentDataType.STRING, "§aAktiviert");
                        container.set(hopperErlaubtStatus, PersistentDataType.STRING, "§cDeaktiviert");
                        state.update();
                    }

                    VerboteneItemsInventory.createInventory(player);

                    ItemStack glass = (new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "§a ")).build();

                    for (int i = 0; i < VerboteneItemsInventory.getInventory().getSize(); i++) {
                        if (VerboteneItemsInventory.getInventory().getItem(i) == null) {
                            VerboteneItemsInventory.setInventoryItems(i, glass);
                        }
                    }

                    String stringVerbotenStatus = container.get(hopperVerbotenStatus, PersistentDataType.STRING);
                    List<String> verboteneItemsLore = new ArrayList<>();
                    verboteneItemsLore.add("§7Filtere hier nach Items, welche §cnicht §7eingesammelt werden sollen.");
                    verboteneItemsLore.add("§7Alle anderen Items werden eingesammelt.");
                    verboteneItemsLore.add("§a ");
                    verboteneItemsLore.add("§7Status: " + stringVerbotenStatus);
                    verboteneItemsLore.add("§e ");
                    if (Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                        verboteneItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");
                    }
                    if (Objects.equals(container.get(hopperVerbotenStatus, PersistentDataType.STRING), "§aAktiviert")) {
                        verboteneItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");
                    }

                    ItemStack verboteneItemsItem = (new ItemBuilder(Material.TNT_MINECART,
                            1,
                            "§cVerbotene Items").glow().lore(verboteneItemsLore)).build();

                    VerboteneItemsInventory.setInventoryItems(10, verboteneItemsItem);


                    String item1 = container.get(hopperVerbotenesItem1, PersistentDataType.STRING);
                    String item2 = container.get(hopperVerbotenesItem2, PersistentDataType.STRING);
                    String item3 = container.get(hopperVerbotenesItem3, PersistentDataType.STRING);
                    String item4 = container.get(hopperVerbotenesItem4, PersistentDataType.STRING);
                    String item5 = container.get(hopperVerbotenesItem5, PersistentDataType.STRING);

                    ItemStack item1i = new ItemStack(Material.valueOf(item1));
                    ItemStack item2i = new ItemStack(Material.valueOf(item2));
                    ItemStack item3i = new ItemStack(Material.valueOf(item3));


                    List<String> item45lore = new ArrayList<>();

                    ItemStack item4i = new ItemStack(Material.valueOf(item4));
                    ItemStack item5i = new ItemStack(Material.valueOf(item5));

                    ItemMeta item4m = item4i.getItemMeta();
                    ItemMeta item5m = item5i.getItemMeta();
                    item45lore.add("§7Du kannst diesen Slot durch");
                    item45lore.add("§7den Kauf eines Ranges freischalten.");
                    item45lore.add("§estore.iostein.net");

                    if (!item4i.getType().equals(Material.AIR) && item4i.getType().equals(Material.BARRIER)) {
                        assert item4m != null;
                        item4m.setDisplayName("§cNicht Freigeschaltet");
                        item4m.setLore(item45lore);
                        item4i.setItemMeta(item4m);
                    }
                    if (!item5i.getType().equals(Material.AIR ) && item5i.getType().equals(Material.BARRIER)) {
                        assert item5m != null;
                        item5m.setDisplayName("§cNicht Freigeschaltet");
                        item5m.setLore(item45lore);
                        item5i.setItemMeta(item5m);
                    }

                    VerboteneItemsInventory.setInventoryItems(12, item1i);
                    VerboteneItemsInventory.setInventoryItems(13, item2i);
                    VerboteneItemsInventory.setInventoryItems(14, item3i);
                    VerboteneItemsInventory.setInventoryItems(15, item4i);
                    VerboteneItemsInventory.setInventoryItems(16, item5i);

                    VerboteneItemsInventory.openInventory(player);
                    player.sendMessage("§a§lFilter §8» §7Du hast den Filtermodus §8'§eVerbotene Items§8' §7ausgewählt.");
                }
                /*

                Erlaubte Items inventory erstellen und konfigurieren

                */

                if (event.getCurrentItem().getType().equals(Material.CHEST_MINECART) && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aErlaubte Items")) {
                    HopperInteractEvent.inventoryClosedAfterClick = true;
                    if (Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                        container.set(hopperErlaubtStatus, PersistentDataType.STRING, "§aAktiviert");
                        container.set(hopperVerbotenStatus, PersistentDataType.STRING, "§cDeaktiviert");
                        state.update();
                    }
                    ErlaubteItemsInventory.createInventory(player);

                    ItemStack glass = (new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "§a ")).build();

                    for (int i = 0; i < ErlaubteItemsInventory.getInventory().getSize(); i++) {
                        if (ErlaubteItemsInventory.getInventory().getItem(i) == null) {
                            ErlaubteItemsInventory.setInventoryItems(i, glass);
                        }
                    }

                    String stringErlaubtStatus = container.get(hopperErlaubtStatus, PersistentDataType.STRING);
                    List<String> erlaubteItemsLore = new ArrayList<>();
                    erlaubteItemsLore.add("§7Filtere hier Items, die §aeingesammelt §7werden sollen.");
                    erlaubteItemsLore.add("§7Alle anderen Items werden in dieser Option nicht eingesammelt.");
                    erlaubteItemsLore.add("§a ");
                    erlaubteItemsLore.add("§7Status: "+stringErlaubtStatus);
                    erlaubteItemsLore.add("§e ");
                    if(Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§cDeaktiviert")) {
                        erlaubteItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");}
                    if(Objects.equals(container.get(hopperErlaubtStatus, PersistentDataType.STRING), "§aAktiviert")) {
                        erlaubteItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");}
                    ItemStack erlaubteItemsItem = (new ItemBuilder(Material.CHEST_MINECART,
                            1,
                            "§aErlaubte Items").glow().lore(erlaubteItemsLore)).build();

                    ErlaubteItemsInventory.setInventoryItems(10, erlaubteItemsItem);


                    String item1 = container.get(hopperErlaubtesItem1, PersistentDataType.STRING);
                    String item2 = container.get(hopperErlaubtesItem2, PersistentDataType.STRING);
                    String item3 = container.get(hopperErlaubtesItem3, PersistentDataType.STRING);
                    String item4 = container.get(hopperErlaubtesItem4, PersistentDataType.STRING);
                    String item5 = container.get(hopperErlaubtesItem5, PersistentDataType.STRING);

                    ItemStack item1i = new ItemStack(Material.valueOf(item1));
                    ItemStack item2i = new ItemStack(Material.valueOf(item2));
                    ItemStack item3i = new ItemStack(Material.valueOf(item3));


                    List<String> item45lore = new ArrayList<>();

                    ItemStack item4i = new ItemStack(Material.valueOf(item4));
                    ItemStack item5i = new ItemStack(Material.valueOf(item5));

                    ItemMeta item4m = item4i.getItemMeta();
                    ItemMeta item5m = item5i.getItemMeta();
                    item45lore.add("§7Du kannst diesen Slot durch");
                    item45lore.add("§7den Kauf eines Ranges freischalten.");
                    item45lore.add("§estore.iostein.net");

                    if (!item4i.getType().equals(Material.AIR) && item4i.getType().equals(Material.BARRIER)) {
                        assert item4m != null;
                        item4m.setDisplayName("§cNicht Freigeschaltet");
                        item4m.setLore(item45lore);
                        item4i.setItemMeta(item4m);
                    }
                    if (!item5i.getType().equals(Material.AIR ) && item5i.getType().equals(Material.BARRIER)) {
                        assert item5m != null;
                        item5m.setDisplayName("§cNicht Freigeschaltet");
                        item5m.setLore(item45lore);
                        item5i.setItemMeta(item5m);
                    }

                    ErlaubteItemsInventory.setInventoryItems(12, item1i);
                    ErlaubteItemsInventory.setInventoryItems(13, item2i);
                    ErlaubteItemsInventory.setInventoryItems(14, item3i);
                    ErlaubteItemsInventory.setInventoryItems(15, item4i);
                    ErlaubteItemsInventory.setInventoryItems(16, item5i);

                    ErlaubteItemsInventory.openInventory(player);
                    player.sendMessage("§a§lFilter §8» §7Du hast den Filtermodus §8'§eErlaubte Items§8' §7ausgewählt.");
                }
                if (event.getCurrentItem().getType().equals(Material.BARRIER) && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cAbschalten")) {
                    container.set(hopperErlaubtStatus, PersistentDataType.STRING, "§cDeaktiviert");
                    container.set(hopperVerbotenStatus, PersistentDataType.STRING, "§cDeaktiviert");
                    state.update();
                    player.sendMessage("§a§lFilter §8» §7Du hast den Filter ausgeschaltet.");
                    player.closeInventory();
                    long current = System.currentTimeMillis();
                    HopperInteractEvent.ClickEvent click = HopperInteractEvent.lastClickedBlock.get(player.getUniqueId().toString());
                    if (click != null && current - click.time >= 25L)
                        HopperInteractEvent.lastClickedBlock.remove(player.getUniqueId().toString());
                }
            }
        }
    }
    private NamespacedKey getHopperErlaubtesItemKey(int slot) {
        switch (slot) {
            case startingSlot:
                return hopperErlaubtesItem1;
            case startingSlot + 1:
                return hopperErlaubtesItem2;
            case startingSlot + 2:
                return hopperErlaubtesItem3;
            case startingSlot + 3:
                return hopperErlaubtesItem4;
            case startingSlot + 4:
                return hopperErlaubtesItem5;
            default:
                throw new IllegalArgumentException("Slot existiert nicht - " + slot);
        }
    }
    private NamespacedKey getHopperVerbotenesItemKey(int slot) {
        switch (slot) {
            case startingSlot:
                return hopperVerbotenesItem1;
            case startingSlot + 1:
                return hopperVerbotenesItem2;
            case startingSlot + 2:
                return hopperVerbotenesItem3;
            case startingSlot + 3:
                return hopperVerbotenesItem4;
            case startingSlot + 4:
                return hopperVerbotenesItem5;
            default:
                throw new IllegalArgumentException("Slot existiert nicht - " + slot);
        }
    }
}
