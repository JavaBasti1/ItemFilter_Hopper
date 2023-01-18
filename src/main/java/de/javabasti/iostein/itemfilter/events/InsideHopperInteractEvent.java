package de.javabasti.iostein.itemfilter.events;

import de.javabasti.iostein.itemfilter.ItemFilter;
import de.javabasti.iostein.itemfilter.utils.*;
import org.bukkit.Material;
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


    @EventHandler
    public void onInventoryInteractEvent(InventoryInteractEvent event) {
        InventoryManager inventoryManager = ItemFilter.getInventoryManager();
        if (event.getInventory().equals(inventoryManager.getCreatedInventory("ForbiddenItemsInventory"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInsideInventoryClickEvent(InventoryClickEvent event) {
        Utils utils = ItemFilter.getUtils();
        InventoryManager inventoryManager = ItemFilter.getInventoryManager();
                /*

                Hinzufügen / Entfernen von den verbotenen Items aus dem Hopper, sowie Überprüfung & überarbeitung der Persistentdata

                */
        if (event.getInventory().equals(inventoryManager.getCreatedInventory("ForbiddenItemsInventory"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                TileState state = (TileState) utils.lastClickedBlock.get(player.getUniqueId().toString()).location.getBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                int endSlot = 16;
                if (Objects.equals(event.getClickedInventory(), inventoryManager.getCreatedInventory("ForbiddenItemsInventory"))) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.BARRIER) || item.getType().equals(Material.AIR)) {
                        if (event.getSlot() >= utils.startingSlot && event.getSlot() <= endSlot) {
                            inventoryManager.setItem("ForbiddenItemsInventory", event.getSlot(), new ItemStack(Material.AIR));
                            container.set(utils.hopperForbiddenItemKey(event.getSlot()), PersistentDataType.STRING, "AIR");
                            state.update();
                        }
                    }
                    event.setCancelled(true);
                }
                if (Objects.equals(event.getClickedInventory(), player.getInventory())) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.AIR)) {
                        for (int i = utils.startingSlot; i <= endSlot; i++) {
                            if (inventoryManager.getCreatedInventory("ForbiddenItemsInventory").getItem(i) == null) {
                                inventoryManager.setItem("ForbiddenItemsInventory", i, new ItemStack(item.getType(), 1));
                                container.set(utils.hopperForbiddenItemKey(i), PersistentDataType.STRING, item.getType().toString());
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

        if (event.getInventory().equals(inventoryManager.getCreatedInventory("AllowedItemsInventory"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                TileState state = (TileState) utils.lastClickedBlock.get(player.getUniqueId().toString()).location.getBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                int endSlot = 16;
                if (Objects.equals(event.getClickedInventory(), inventoryManager.getCreatedInventory("AllowedItemsInventory"))) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.BARRIER) || item.getType().equals(Material.AIR)) {
                        if (event.getSlot() >= utils.startingSlot && event.getSlot() <= endSlot) {
                            inventoryManager.setItem("AllowedItemsInventory", event.getSlot(), new ItemStack(Material.AIR));
                            container.set(utils.hopperAllowedItemKey(event.getSlot()), PersistentDataType.STRING, "AIR");
                            state.update();
                        }
                    }
                    event.setCancelled(true);
                }
                if (Objects.equals(event.getClickedInventory(), player.getInventory())) {
                    ItemStack item = event.getCurrentItem();
                    if (!item.getType().equals(Material.AIR)) {
                        for (int i = utils.startingSlot; i <= endSlot; i++) {
                            if (inventoryManager.getCreatedInventory("AllowedItemsInventory").getItem(i) == null) {
                                inventoryManager.setItem("AllowedItemsInventory", i, new ItemStack(item.getType(), 1));
                                container.set(utils.hopperAllowedItemKey(i), PersistentDataType.STRING, item.getType().toString());
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
        Utils utils = ItemFilter.getUtils();
        InventoryManager inventoryManager = ItemFilter.getInventoryManager();

        if (Objects.equals(event.getClickedInventory(), inventoryManager.getCreatedInventory("FilterInventory"))) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                Player player = (Player) event.getWhoClicked();
                TileState state = (TileState) utils.lastClickedBlock.get(player.getUniqueId().toString()).location.getBlock().getState();
                PersistentDataContainer container = state.getPersistentDataContainer();

                /*

                Verbotene Items inventory erstellen und konfigurieren

                */

                if (event.getCurrentItem().getType().equals(Material.TNT_MINECART) &&
                        event.getCurrentItem().getItemMeta().getDisplayName().equals("§cVerbotene Items")) {
                    utils.setInventoryClosedAfterClick(true);

                    if (Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§cDeaktiviert")) {
                        container.set(utils.hopperForbiddenState, PersistentDataType.STRING, "§aAktiviert");
                        container.set(utils.hopperAllowedState, PersistentDataType.STRING, "§cDeaktiviert");
                        state.update();
                    }

                    inventoryManager.createInventory("ForbiddenItemsInventory", 27, "§8» §c§lVerbotene Items");

                    ItemStack glass = (new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "§a ")).build();

                    for (int i = 0; i < inventoryManager.getCreatedInventory("ForbiddenItemsInventory").getSize(); i++) {
                        if (inventoryManager.getCreatedInventory("ForbiddenItemsInventory").getItem(i) == null) {
                            inventoryManager.setItem("ForbiddenItemsInventory", i, glass);
                        }
                    }

                    String stringVerbotenStatus = container.get(utils.hopperForbiddenState, PersistentDataType.STRING);
                    List<String> verboteneItemsLore = new ArrayList<>();
                    verboteneItemsLore.add("§7Filtere hier nach Items, welche §cnicht §7eingesammelt werden sollen.");
                    verboteneItemsLore.add("§7Alle anderen Items werden eingesammelt.");
                    verboteneItemsLore.add("§a ");
                    verboteneItemsLore.add("§7Status: " + stringVerbotenStatus);
                    verboteneItemsLore.add("§e ");
                    if (Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§cDeaktiviert")) {
                        verboteneItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");
                    }
                    if (Objects.equals(container.get(utils.hopperForbiddenState, PersistentDataType.STRING), "§aAktiviert")) {
                        verboteneItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");
                    }

                    ItemStack verboteneItemsItem = (new ItemBuilder(Material.TNT_MINECART,
                            1,
                            "§cVerbotene Items").glow().lore(verboteneItemsLore)).build();

                    inventoryManager.setItem("ForbiddenItemsInventory", 10, verboteneItemsItem);


                    String item1 = container.get(utils.hopperForbiddenItem1, PersistentDataType.STRING);
                    String item2 = container.get(utils.hopperForbiddenItem2, PersistentDataType.STRING);
                    String item3 = container.get(utils.hopperForbiddenItem3, PersistentDataType.STRING);
                    String item4 = container.get(utils.hopperForbiddenItem4, PersistentDataType.STRING);
                    String item5 = container.get(utils.hopperForbiddenItem5, PersistentDataType.STRING);

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

                    inventoryManager.setItem("ForbiddenItemsInventory", 12, item1i);
                    inventoryManager.setItem("ForbiddenItemsInventory", 13, item2i);
                    inventoryManager.setItem("ForbiddenItemsInventory", 14, item3i);
                    inventoryManager.setItem("ForbiddenItemsInventory", 15, item4i);
                    inventoryManager.setItem("ForbiddenItemsInventory", 16, item5i);

                    player.openInventory(inventoryManager.getCreatedInventory("ForbiddenItemsInventory"));
                    player.sendMessage("§a§lFilter §8» §7Du hast den Filtermodus §8'§eVerbotene Items§8' §7ausgewählt.");
                }
                /*

                Erlaubte Items inventory erstellen und konfigurieren

                */

                if (event.getCurrentItem().getType().equals(Material.CHEST_MINECART) && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aErlaubte Items")) {
                    utils.setInventoryClosedAfterClick(true);
                    if (Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§cDeaktiviert")) {
                        container.set(utils.hopperAllowedState, PersistentDataType.STRING, "§aAktiviert");
                        container.set(utils.hopperForbiddenState, PersistentDataType.STRING, "§cDeaktiviert");
                        state.update();
                    }
                    inventoryManager.createInventory("AllowedItemsInventory", 27, "§8» §a§lErlaubte Items");

                    ItemStack glass = (new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "§a ")).build();

                    for (int i = 0; i < inventoryManager.getCreatedInventory("AllowedItemsInventory").getSize(); i++) {
                        if (inventoryManager.getCreatedInventory("AllowedItemsInventory").getItem(i) == null) {
                            inventoryManager.setItem("AllowedItemsInventory", i, glass);
                        }
                    }

                    String stringErlaubtStatus = container.get(utils.hopperAllowedState, PersistentDataType.STRING);
                    List<String> erlaubteItemsLore = new ArrayList<>();
                    erlaubteItemsLore.add("§7Filtere hier Items, die §aeingesammelt §7werden sollen.");
                    erlaubteItemsLore.add("§7Alle anderen Items werden in dieser Option nicht eingesammelt.");
                    erlaubteItemsLore.add("§a ");
                    erlaubteItemsLore.add("§7Status: "+stringErlaubtStatus);
                    erlaubteItemsLore.add("§e ");
                    if(Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§cDeaktiviert")) {
                        erlaubteItemsLore.add("§8Klicke um diese Filter Option zu aktivieren.");}
                    if(Objects.equals(container.get(utils.hopperAllowedState, PersistentDataType.STRING), "§aAktiviert")) {
                        erlaubteItemsLore.add("§8Klicke um diese Filter Option zu deaktivieren.");}
                    ItemStack erlaubteItemsItem = (new ItemBuilder(Material.CHEST_MINECART,
                            1,
                            "§aErlaubte Items").glow().lore(erlaubteItemsLore)).build();

                    inventoryManager.setItem("AllowedItemsInventory", 10, erlaubteItemsItem);


                    String item1 = container.get(utils.hopperAllowedItem1, PersistentDataType.STRING);
                    String item2 = container.get(utils.hopperAllowedItem2, PersistentDataType.STRING);
                    String item3 = container.get(utils.hopperAllowedItem3, PersistentDataType.STRING);
                    String item4 = container.get(utils.hopperAllowedItem4, PersistentDataType.STRING);
                    String item5 = container.get(utils.hopperAllowedItem5, PersistentDataType.STRING);

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

                    inventoryManager.setItem("AllowedItemsInventory", 12, item1i);
                    inventoryManager.setItem("AllowedItemsInventory", 13, item2i);
                    inventoryManager.setItem("AllowedItemsInventory", 14, item3i);
                    inventoryManager.setItem("AllowedItemsInventory", 15, item4i);
                    inventoryManager.setItem("AllowedItemsInventory", 16, item5i);

                    player.openInventory(inventoryManager.getCreatedInventory("AllowedItemsInventory"));
                    player.sendMessage("§a§lFilter §8» §7Du hast den Filtermodus §8'§eErlaubte Items§8' §7ausgewählt.");
                }
                if (event.getCurrentItem().getType().equals(Material.BARRIER) && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cAbschalten")) {
                    container.set(utils.hopperAllowedState, PersistentDataType.STRING, "§cDeaktiviert");
                    container.set(utils.hopperForbiddenState, PersistentDataType.STRING, "§cDeaktiviert");
                    state.update();
                    player.sendMessage("§a§lFilter §8» §7Du hast den Filter ausgeschaltet.");
                    player.closeInventory();
                    long current = System.currentTimeMillis();
                    Utils.ClickEvent click = utils.lastClickedBlock.get(player.getUniqueId().toString());
                    if (click != null && current - click.time >= 25L)
                        utils.lastClickedBlock.remove(player.getUniqueId().toString());
                }
            }
        }
    }
}
