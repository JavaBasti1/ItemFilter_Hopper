package de.javabasti.iostein.itemfilter.utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

@SuppressWarnings({"deprecation", "CopyConstructorMissesField"})
public class ItemBuilder {
    private ItemStack item;

    private ItemMeta meta;

    private Material material = Material.STONE;

    private int amount = 1;

    private MaterialData data;

    private short damage = 0;

    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    private String displayname;

    private List<String> lore = new ArrayList<>();

    private List<ItemFlag> flags = new ArrayList<>();

    private boolean andSymbol = true;

    private boolean unsafeStackSize = false;

    public ItemBuilder(Material material) {
        if (material == null)
            material = Material.AIR;
        this.item = new ItemStack(material);
        this.material = material;
    }

    public ItemBuilder(Material material, int amount) {
        if (material == null)
            material = Material.AIR;
        if (amount > material.getMaxStackSize() || amount <= 0)
            amount = 1;
        this.amount = amount;
        this.item = new ItemStack(material, amount);
        this.material = material;
    }

    public ItemBuilder(Material material, int amount, String displayname) {
        if (material == null)
            material = Material.AIR;

        this.item = new ItemStack(material, amount);
        this.material = material;
        if ((amount > material.getMaxStackSize() || amount <= 0) && !this.unsafeStackSize)
            amount = 1;
        this.amount = amount;
        this.displayname = displayname;
    }

    public ItemBuilder(Material material, String displayname) {
        if (material == null)
            material = Material.AIR;

        this.item = new ItemStack(material);
        this.material = material;
        this.displayname = displayname;
    }

    public ItemBuilder(ItemStack item) {

        this.item = item;
        this.material = item.getType();
        this.amount = item.getAmount();
        this.data = item.getData();
        this.damage = item.getDurability();
        this.enchantments = item.getEnchantments();
        if (item.hasItemMeta()) {
            this.meta = item.getItemMeta();
            this.displayname = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
            this.lore = item.getItemMeta().getLore();
            this.flags.addAll(item.getItemMeta().getItemFlags());
        }
    }

    public ItemBuilder(FileConfiguration cfg, String path) {
        this(Objects.requireNonNull(cfg.getItemStack(path)));
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

    public void enchant(Enchantment enchant, int level) {
        this.enchantments.put(enchant, level);
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
        protected final ReflectionUtils utils = new ReflectionUtils();

        protected final ItemBuilder builder;

        public Unsafe(ItemBuilder builder) {
            this.builder = builder;
        }

        @SuppressWarnings("deprecation")
        public class ReflectionUtils {
            public String getString(ItemStack item, String key) {
                Object compound = getNBTTagCompound(getItemAsNMSStack(item));
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    return (String)compound.getClass().getMethod("getString", new Class[] { String.class }).invoke(compound, new Object[] { key });
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public ItemStack setString(ItemStack item, String key, String value) {
                Object nmsItem = getItemAsNMSStack(item);
                Object compound = getNBTTagCompound(nmsItem);
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    compound.getClass().getMethod("setString", new Class[] { String.class, String.class }).invoke(compound, key, value);
                    nmsItem = setNBTTag(compound, nmsItem);
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
                return getItemAsBukkitStack(nmsItem);
            }

            public int getInt(ItemStack item, String key) {
                Object compound = getNBTTagCompound(getItemAsNMSStack(item));
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    return (Integer) compound.getClass().getMethod("getInt", new Class[]{String.class}).invoke(compound, new Object[]{key});
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                    return -1;
                }
            }

            public ItemStack setInt(ItemStack item, String key, int value) {
                Object nmsItem = getItemAsNMSStack(item);
                Object compound = getNBTTagCompound(nmsItem);
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    compound.getClass().getMethod("setInt", new Class[] { String.class, Integer.class }).invoke(compound, key, value);
                    nmsItem = setNBTTag(compound, nmsItem);
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
                return getItemAsBukkitStack(nmsItem);
            }

            public double getDouble(ItemStack item, String key) {
                Object compound = getNBTTagCompound(getItemAsNMSStack(item));
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    return (Double) compound.getClass().getMethod("getDouble", new Class[]{String.class}).invoke(compound, new Object[]{key});
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                    return Double.NaN;
                }
            }

            public ItemStack setDouble(ItemStack item, String key, double value) {
                Object nmsItem = getItemAsNMSStack(item);
                Object compound = getNBTTagCompound(nmsItem);
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    compound.getClass().getMethod("setDouble", new Class[] { String.class, Double.class }).invoke(compound, key, value);
                    nmsItem = setNBTTag(compound, nmsItem);
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
                return getItemAsBukkitStack(nmsItem);
            }

            public boolean getBoolean(ItemStack item, String key) {
                Object compound = getNBTTagCompound(getItemAsNMSStack(item));
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    return (Boolean) compound.getClass().getMethod("getBoolean", new Class[]{String.class}).invoke(compound, new Object[]{key});
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            public ItemStack setBoolean(ItemStack item, String key, boolean value) {
                Object nmsItem = getItemAsNMSStack(item);
                Object compound = getNBTTagCompound(nmsItem);
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    compound.getClass().getMethod("setBoolean", new Class[] { String.class, Boolean.class }).invoke(compound, key,
                            value);
                    nmsItem = setNBTTag(compound, nmsItem);
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
                return getItemAsBukkitStack(nmsItem);
            }

            public boolean hasKey(ItemStack item, String key) {
                Object compound = getNBTTagCompound(getItemAsNMSStack(item));
                if (compound == null)
                    compound = getNewNBTTagCompound();
                try {
                    return (Boolean) compound.getClass().getMethod("hasKey", new Class[]{String.class}).invoke(compound, new Object[]{key});
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            public Object getNewNBTTagCompound() {
                String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                try {
                    return Class.forName("net.minecraft.server." + ver + ".NBTTagCompound").newInstance();
                } catch (ClassNotFoundException|IllegalAccessException|InstantiationException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public Object setNBTTag(Object tag, Object item) {
                try {
                    item.getClass().getMethod("setTag", new Class[] { item.getClass() }).invoke(item, tag);
                    return item;
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public Object getNBTTagCompound(Object nmsStack) {
                try {
                    return nmsStack.getClass().getMethod("getTag", new Class[0]).invoke(nmsStack);
                } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public Object getItemAsNMSStack(ItemStack item) {
                try {
                    Method m = getCraftItemStackClass().getMethod("asNMSCopy", ItemStack.class);
                    return m.invoke(getCraftItemStackClass(), item);
                } catch (NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public ItemStack getItemAsBukkitStack(Object nmsStack) {
                try {
                    Method m = getCraftItemStackClass().getMethod("asCraftMirror", nmsStack.getClass());
                    return (ItemStack)m.invoke(getCraftItemStackClass(), new Object[] { nmsStack });
                } catch (NoSuchMethodException|java.lang.reflect.InvocationTargetException|IllegalAccessException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public Class<?> getCraftItemStackClass() {
                String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                try {
                    return Class.forName("org.bukkit.craftbukkit." + ver + ".inventory.CraftItemStack");
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }
    }
}

