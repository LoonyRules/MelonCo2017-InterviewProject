package uk.co.loonyrules.rp.melonco.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import uk.co.loonyrules.rp.melonco.builders.ItemBuilder;

public class Items
{

    public static final ItemStack
            PLACEHOLDER = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build(),
            POINTER = new ItemBuilder(Material.HOPPER).setDisplayName(ChatColor.YELLOW + "Selector").build();

}