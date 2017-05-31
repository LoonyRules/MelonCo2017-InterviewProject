package uk.co.loonyrules.rp.melonco.crates;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.loonyrules.rp.melonco.CratesPlugin;

public class EnderCrate extends Crate
{

    /**
     * Initialises a new instance of the EnderCrate
     * @param cratesPlugin Instance of the CratesPlugin
     */
    public EnderCrate(CratesPlugin cratesPlugin)
    {
        super(
                // CratesPlugin instance
                cratesPlugin,

                // Raw name of this Crate
                "ENDER",

                // The "prefix" of this Case (if you will)
                "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "EnderCrate",

                // Key for validating openings
                new ItemStack(Material.IRON_HOE),

                // Potential rewards
                new ItemStack[] {
                        new ItemStack(Material.GRASS),
                        new ItemStack(Material.STONE),
                        new ItemStack(Material.DIRT),
                        new ItemStack(Material.COAL),
                        new ItemStack(Material.COAL, 1, (short) 1), // Charcoal
                        new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.IRON_INGOT),
                        new ItemStack(Material.GOLD_INGOT),
                        new ItemStack(Material.INK_SACK, 1, (short) 4), // Lapis Lazuli
                        new ItemStack(Material.REDSTONE),
                        new ItemStack(Material.STICK),
                        new ItemStack(Material.EGG),
                        new ItemStack(Material.CAKE),
                        new ItemStack(Material.COOKED_BEEF),
                        new ItemStack(Material.COOKED_CHICKEN),
                        new ItemStack(Material.GOLDEN_APPLE),
                        new ItemStack(Material.APPLE)
                }
        );
    }

    /**
     * Inheriting the onOpen method to disallow players that are Flying from opening this specific Crate.
     * @param player That's opening the Crate
     * @return True: Player isn't flying. False: Player is flying.
     */
    @Override
    public boolean onOpen(Player player)
    {
        boolean result = !player.isFlying();

        if(!result)
            player.sendMessage(ChatColor.RED + "You cannot be flying when opening the " + getDisplayName());

        return result;
    }

}