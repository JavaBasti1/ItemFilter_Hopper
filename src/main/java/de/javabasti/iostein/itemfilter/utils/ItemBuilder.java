package de.javabasti.iostein.itemfilter.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"deprecation", "CopyConstructorMissesField"})
public class ItemBuilder {
    private final ItemStack item;

    private ItemMeta meta;

    private final Material material;

    private final int amount;

    private MaterialData data;

    private short damage = 0;

    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    private final String displayname;

    private List<String> lore = new ArrayList<>();

    private List<ItemFlag> flags = new ArrayList<>();

    private boolean andSymbol = true;

    public ItemBuilder(Material material, int amount, String displayname) {
        if (material == null)
            material = Material.AIR;

        this.item = new ItemStack(material, amount);
        this.material = material;
        boolean unsafeStackSize = false;
        if ((amount > material.getMaxStackSize() || amount <= 0) && !unsafeStackSize)
            amount = 1;
        this.amount = amount;
        this.displayname = displayname;
    }

    @Deprecated
    public ItemBuilder(ItemBuilder builder) {
        this.item = builder.item;
        this.meta = builder.meta;
        this.material = builder.material;
        this.amount = builder.amount;
        this.damage = builder.damage;
        this.data = builder.data;
        this.enchantments = builder.enchantments;
        this.displayname = builder.displayname;
        this.lore = builder.lore;
        this.flags = builder.flags;
    }

    @Deprecated
    public ItemBuilder damage(short damage) {
        this.damage = damage;
        return this;
    }

    public void lore(String line) {
        this.lore.add(this.andSymbol ? ChatColor.translateAlternateColorCodes('&', line) : line);
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    @Deprecated
    public ItemBuilder lores(String... lines) {
        for (String line : lines)
            lore(this.andSymbol ? ChatColor.translateAlternateColorCodes('&', line) : line);
        return this;
    }

    public void flag(ItemFlag flag) {
        this.flags.add(flag);
    }

    public ItemBuilder glow() {
        item.addUnsafeEnchantment((this.material != Material.BOW) ? Enchantment.ARROW_INFINITE : Enchantment.LUCK, 10);
        flag(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    @Deprecated
    public ItemBuilder owner(String user) {
        if (this.material == Material.PLAYER_HEAD) {
            SkullMeta smeta = (SkullMeta)this.meta;
            smeta.setOwner(user);
            this.meta = smeta;
        }
        return this;
    }

    @Deprecated
    public Unsafe unsafe() {
        return new Unsafe(this);
    }

    @Deprecated
    public ItemBuilder replaceAndSymbol() {
        replaceAndSymbol(!this.andSymbol);
        return this;
    }

    public void replaceAndSymbol(boolean replace) {
        this.andSymbol = replace;
    }

    @Deprecated
    public short getDamage() {
        return this.damage;
    }

    @Deprecated
    public List<String> getLore() {
        return this.lore;
    }

    public ItemStack build() {
        this.item.setType(this.material);
        this.item.setAmount(this.amount);
        this.item.setDurability(this.damage);
        this.meta = this.item.getItemMeta();
        if (this.data != null)
            this.item.setData(this.data);
        if (this.enchantments.size() > 0)
            this.item.addUnsafeEnchantments(this.enchantments);
        if (this.displayname != null) {
            assert this.meta != null;
            this.meta.setDisplayName(this.displayname);
        }
        if (this.lore.size() > 0) {
            assert this.meta != null;
            this.meta.setLore(this.lore);
        }
        if (this.flags.size() > 0)
            for (ItemFlag f : this.flags) {
                assert this.meta != null;
                this.meta.addItemFlags(f);
            }
        this.item.setItemMeta(this.meta);
        return this.item;
    }

    public static class Unsafe {

        protected final ItemBuilder builder;

        public Unsafe(ItemBuilder builder) {
            this.builder = builder;
        }

    }
}

