package uk.co.loonyrules.rp.melonco.builders;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

/**
 * An easy to use ItemStack builder.
 * This class supports:
 * <ul>
 *     <li>Setting the {@link Material}</li>
 *     <li>Setting the material id (it'll be converted into {@link Material} by itself)</li>
 *     <li>Setting the amount</li>
 *     <li>Setting the display name</li>
 *     <li>Setting the MaterialData</li>
 *     <li>Adding/removing/clearing/setting the ItemFlags</li>
 *     <li>Adding/removing/clearing/setting the Lores</li>
 *     <li>Adding/removing/clearing/setting the Enchantments</li>
 *     <li>Unsafe enchantments</li>
 * </ul>
 *
 */
public class ItemBuilder
{

    private Material material;
    private short durability = 0;
    private int amount = 1;
    private String displayName;
    private MaterialData materialData;

    private Set<ItemFlag> itemFlags = Sets.newHashSet();
    private List<String> lores = Lists.newArrayList();
    private Map<Enchantment, Integer> enchantments = Maps.newHashMap();

    /**
     * Initialise a new ItemBuilder
     *
     * @param material Material of the item
     */
    public ItemBuilder(Material material)
    {
        setMaterial(material);
    }

    public ItemBuilder(int materialId)
    {
        this(Material.getMaterial(materialId));
    }

    public ItemBuilder(ItemStack itemStack)
    {
        this.material = itemStack.getType();
        this.durability = itemStack.getDurability();
        this.amount = itemStack.getAmount();
        this.materialData = itemStack.getData();

        if(!itemStack.hasItemMeta())
            return;

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta.hasDisplayName())
            this.displayName = itemMeta.getDisplayName();

        this.itemFlags = itemMeta.getItemFlags();
        this.lores = itemMeta.hasLore() ? itemMeta.getLore() : Lists.newArrayList();
        this.enchantments = itemMeta.getEnchants();
    }

    /**
     * Get the {@link Material} of the item being built.
     * @return The Material of the item.
     */
    public Material getMaterial()
    {
        return material;
    }

    /**
     * Set the {@link Material} of the item being built.
     *
     * @param material Material of the item
     * @return The builder instance
     */
    public ItemBuilder setMaterial(Material material)
    {
        this.material = material;
        return this;
    }

    /**
     * Get the material id of the item being built.
     * @return The material id of the item.
     */
    public int getMaterialId()
    {
        return material.getId();
    }

    /**
     * Set the material id of the item being built.
     * @param materialId The material id of the item.
     * @return The builder instance
     */
    public ItemBuilder setMaterialId(int materialId)
    {
        return setMaterial(Material.getMaterial(materialId));
    }

    /**
     * Get the item durability of the item being built.
     * @return The durability of the item being built.
     */
    public short getDurability()
    {
        return durability;
    }

    /**
     * Set the durability of the item being built.
     * @param durability The durability to set to.
     * @return The builder instance.
     */
    @Deprecated
    public ItemBuilder setDurability(int durability)
    {
        return setDurability((short) durability);
    }

    /**
     * Set the durability of the item being built.
     * @param durability The durability to set to
     * @return The builder instance.
     */
    public ItemBuilder setDurability(short durability)
    {
        this.durability = durability;
        return this;
    }

    /**
     * Get the amount of the item being built.
     * @return The amount it gives.
     */
    public int getAmount()
    {
        return amount;
    }

    /**
     * Set the amount of the item being built.
     * @param amount - The amount to give.
     * @return - The builder instance.
     */
    public ItemBuilder setAmount(int amount)
    {
        this.amount = amount;
        return this;
    }

    /**
     * Get the display name of the item being built.
     * @return The builder instance.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Set the display name of the item being built.
     * @param displayName What to set the name to
     * @return The builder instance.
     */
    public ItemBuilder setDisplayName(String displayName)
    {
        this.displayName = displayName;
        return this;
    }

    /**
     * Get the material data that will be applied to the item being built.
     * @return The MaterialData
     */
    public MaterialData getMaterialData()
    {
        return materialData;
    }

    /**
     * Set the MaterialData of the item being built.
     * @param materialData The MaterialData to set
     * @return The builder instance.
     */
    public ItemBuilder setMaterialData(MaterialData materialData)
    {
        this.materialData = materialData;
        return this;
    }

    /**
     * Get the item flags being applied to the item being built.
     * @return Set of ItemFlag's
     */
    public Set<ItemFlag> getItemFlags()
    {
        return itemFlags;
    }

    /**
     * Clear the ItemFlags
     * @return The builder instance
     */
    public ItemBuilder clearItemFlags()
    {
        itemFlags.clear();
        return this;
    }

    /**
     * Check whether the built item has an ItemFlag
     * @param itemFlag The ItemFlag to search for
     * @return Whether or not it has the ItemFlag
     */
    public boolean hasItemFlag(ItemFlag itemFlag)
    {
        return itemFlags.contains(itemFlag);
    }

    /**
     * Add an ItemFlag to the item
     * @param itemFlag The ItemFlag to add
     * @return The builder instance
     */
    public ItemBuilder addItemFlag(ItemFlag itemFlag)
    {
        return addItemFlags(itemFlag);
    }

    /**
     * Add multiple ItemFlags to the item
     * @param itemFlags ItemFlags to add
     * @return The builder instance
     */
    public ItemBuilder addItemFlags(ItemFlag... itemFlags)
    {
        Arrays.stream(itemFlags).forEach(this.itemFlags::add);
        return this;
    }

    /**
     * Remove an ItemFlag from the item
     * @param itemFlag The ItemFlag to remove
     * @return The builder instance
     */
    public ItemBuilder removeItemFlag(ItemFlag itemFlag)
    {
        return removeItemFlags(itemFlag);
    }

    /**
     * Remove multiple ItemFlags from the item
     * @param itemFlags ItemFlags to remove
     * @return The builder instance
     */
    public ItemBuilder removeItemFlags(ItemFlag... itemFlags)
    {
        Arrays.stream(itemFlags).forEach(this.itemFlags::remove);
        return this;
    }

    /**
     * Get the lores for the item being built.
     * @return A list of the lores.
     */
    public List<String> getLores()
    {
        return lores;
    }

    /**
     * Reset the lores for the item being built.
     * @return The builder instance.
     */
    public ItemBuilder clearLores()
    {
        this.lores = Lists.newArrayList();
        return this;
    }

    /**
     * Append onto the current lores for this item.
     * @param lores The lores to add
     * @return The builder instance.
     */
    public ItemBuilder appendLores(String... lores)
    {
        // Converting colour codes
        for(int i = 0; i < lores.length; i++)
            lores[i] = ChatColor.translateAlternateColorCodes('&', lores[i]);

        Collections.addAll(this.lores == null ? Lists.newArrayList() : this.lores, lores);
        return this;
    }

    /**
     * Set the item's lores to a specific list.
     * @param lores The lores to set to.
     * @return The builder instance.
     */
    public ItemBuilder setLores(List<String> lores)
    {
        Validate.notNull(lores);

        // Converting colour codes
        for(int i = 0; i < lores.size(); i++)
            lores.set(i, ChatColor.translateAlternateColorCodes('&', lores.get(i)));

        this.lores = lores;
        return this;
    }

    /**
     * Get the enchantments to apply to this item.
     * @return Map of enchantments
     */
    public Map<Enchantment, Integer> getEnchantments()
    {
        return enchantments;
    }

    /**
     * Clear the enchantments applied to this item.
     * @return The builder instance.
     */
    public ItemBuilder clearEnchantments()
    {
        enchantments.clear();
        return this;
    }

    /**
     * Get the level of an enchantment
     * @param enchantment The enchantment to get the level for
     * @return The level
     */
    public Integer getEnchantmentLevel(Enchantment enchantment)
    {
        return enchantments.get(enchantment);
    }

    /**
     * Check whether the item has a specific enchantment.
     * @param enchantment The needle
     * @return Whether or not it has the enchantment.
     */
    public boolean hasEnchantment(Enchantment enchantment)
    {
        return getEnchantmentLevel(enchantment) != null;
    }

    /**
     * Add an enchantment to the item
     * @param enchantment The enchantment to add
     * @param level The level of the enchantment to add (Supports unsafe enchantments)
     * @return The builder instance.
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level)
    {
        enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Set the enchantments to add to this item
     * @param enchantments The map of enchantments to set
     * @return The builder instance.
     */
    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments)
    {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Remove an enchantment from the item.
     * @param enchantment The enchantment to remove
     * @return The builder instance
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment)
    {
        enchantments.remove(enchantment);
        return this;
    }

    /**
     * Build the ItemBuilder data into an ItemStack
     * @return The built item
     */
    public ItemStack build()
    {
        ItemStack itemStack = new ItemStack(getMaterial(), getAmount(), getDurability());

        MaterialData materialData = getMaterialData();
        if(materialData != null)
            itemStack.setData(materialData);

        itemStack.addUnsafeEnchantments(enchantments);

        ItemMeta itemMeta = itemStack.getItemMeta();

        String displayName = getDisplayName();
        if(displayName != null)
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        itemMeta.setLore(lores);

        Set<ItemFlag> itemFlags = getItemFlags();
        itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[itemFlags.size()]));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}