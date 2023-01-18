package de.javabasti.iostein.itemfilter.utils;

import de.javabasti.iostein.itemfilter.ItemFilter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {

    private boolean inventoryClosedAfterClick = false;

    public boolean isInventoryClosedAfterClick() {
        return inventoryClosedAfterClick;
    }

    public void setInventoryClosedAfterClick(boolean value) {
        this.inventoryClosedAfterClick = value;
    }

    // Event zum speichern der Location & der Zeit des angeklickten Blockes für weitere Aktionen
    public final Map<String, ClickEvent> lastClickedBlock = new ConcurrentHashMap<>();

    public static class ClickEvent {
        public long time;

        public Location location;

        public ClickEvent() {
        }
    }

    public final int startingSlot = 12;

    public NamespacedKey hopperForbiddenState = new NamespacedKey(ItemFilter.getInstance(), "hopperForbiddenState");
    public NamespacedKey hopperAllowedState = new NamespacedKey(ItemFilter.getInstance(), "hopperAllowedState");

    public NamespacedKey hopperAllowedItem1 = new NamespacedKey(ItemFilter.getInstance(), "hopperAllowedItem1");
    public NamespacedKey hopperAllowedItem2 = new NamespacedKey(ItemFilter.getInstance(), "hopperAllowedItem2");
    public NamespacedKey hopperAllowedItem3 = new NamespacedKey(ItemFilter.getInstance(), "hopperAllowedItem3");
    public NamespacedKey hopperAllowedItem4 = new NamespacedKey(ItemFilter.getInstance(), "hopperAllowedItem4");
    public NamespacedKey hopperAllowedItem5 = new NamespacedKey(ItemFilter.getInstance(), "hopperAllowedItem5");

    public NamespacedKey hopperForbiddenItem1 = new NamespacedKey(ItemFilter.getInstance(), "hopperForbiddenItem1");
    public NamespacedKey hopperForbiddenItem2 = new NamespacedKey(ItemFilter.getInstance(), "hopperForbiddenItem2");
    public NamespacedKey hopperForbiddenItem3 = new NamespacedKey(ItemFilter.getInstance(), "hopperForbiddenItem3");
    public NamespacedKey hopperForbiddenItem4 = new NamespacedKey(ItemFilter.getInstance(), "hopperForbiddenItem4");
    public NamespacedKey hopperForbiddenItem5 = new NamespacedKey(ItemFilter.getInstance(), "hopperForbiddenItem5");

    public NamespacedKey hopperAllowedItemKey(int slot) {
        
        switch (slot) {
            case startingSlot:
                return hopperAllowedItem1;
            case startingSlot + 1:
                return hopperAllowedItem2;
            case startingSlot + 2:
                return hopperAllowedItem3;
            case startingSlot + 3:
                return hopperAllowedItem4;
            case startingSlot + 4:
                return hopperAllowedItem5;
            default:
                throw new IllegalArgumentException("Slot existiert nicht - " + slot);
        }
    }

    public NamespacedKey hopperForbiddenItemKey(int slot) {
        
        switch (slot) {
            case startingSlot:
                return hopperForbiddenItem1;
            case startingSlot + 1:
                return hopperForbiddenItem2;
            case startingSlot + 2:
                return hopperForbiddenItem3;
            case startingSlot + 3:
                return hopperForbiddenItem4;
            case startingSlot + 4:
                return hopperForbiddenItem5;
            default:
                throw new IllegalArgumentException("Slot existiert nicht - " + slot);
        }
    }
}
