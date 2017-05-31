package uk.co.loonyrules.rp.melonco;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.loonyrules.rp.melonco.crates.Crate;
import uk.co.loonyrules.rp.melonco.enums.CrateType;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class CratesPlugin extends JavaPlugin implements Listener
{

    private CratesPlugin instance;

    @Override
    public void onEnable()
    {
        instance = this;

        register(this);
    }

    @Override
    public void onDisable()
    {
        unregister(this);
    }

    public void register(Listener listener)
    {
        instance.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void unregister(Listener listener)
    {
        HandlerList.unregisterAll(listener);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        // Not an Action we want
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Specifically for this Plugin if you Right-Click ANY Crate type it'll act as a Crate.
        Optional<CrateType> crateTypeOptional = CrateType.getType(block.getType());

        try {
            // Not a CrateType we're managing
            if(!crateTypeOptional.isPresent())
                return;

            event.setCancelled(true);

            // If they're already opening this Crate, open up their instance
            Optional<Crate> crateOptional = Crate.getOpeningCrate(player.getUniqueId());

            if(crateOptional.isPresent())
            {
                crateOptional.get().open(player);
                return;
            }

            // Initialsing a new instance of our Crate (I know it's not the best way for validation)
            Crate crate = crateTypeOptional.get().getCrateClass().getConstructor(CratesPlugin.class).newInstance(this);

            // Validating this opening
            ItemStack key = crate.getKey();
            if(crate.getUser() != player && crate.requiresKey() && !key.isSimilar(player.getItemInHand()))
            {
                player.sendMessage(ChatColor.RED + "The " + crate.getDisplayName() + ChatColor.RED + " requires a " + ChatColor.YELLOW + (key.hasItemMeta() ? key.getItemMeta().hasDisplayName() ? key.getItemMeta().getDisplayName() : key.getType().toString() : key.getType().toString()) + ChatColor.RED + " in your hand to open.");
                event.setCancelled(true);
                return;
            }

            crate.open(player);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}