package uk.co.loonyrules.rp.melonco.crates;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import uk.co.loonyrules.rp.melonco.CratesPlugin;
import uk.co.loonyrules.rp.melonco.utils.InventoryUtil;
import uk.co.loonyrules.rp.melonco.utils.Items;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Crate extends BukkitRunnable implements Listener
{

    private static final Set<Crate> instances = Sets.newConcurrentHashSet();

    /**
     * Get all Crate instances
     * @return All the current Crate's and their instances
     */
    private static Collection<Crate> getCrates()
    {
        return instances;
    }

    /**
     * Gets the Crate a player is opening
     * @param opener Of the Player you want to search for
     * @return Crate instance if exists
     */
    public static Optional<Crate> getOpeningCrate(UUID opener)
    {
        return getCrates().stream().filter(Crate::isInUse).filter(crate -> crate.getUser().getUniqueId() == opener).findFirst();
    }

    private static void removeCrate(Crate crate)
    {
        Iterator<Crate> iterator = instances.iterator();

        while (iterator.hasNext())
        {
            if(iterator.next() == crate)
                iterator.remove();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final CratesPlugin cratesPlugin;
    private final String name, displayName;
    private final ItemStack key;
    private final ItemStack[] rewards;
    private final long slowdownTicks = 50L; // With 2.5 seconds left we'll start the slowdown process

    private Inventory inventory;
    private LinkedList<ItemStack> conveyor;
    private Player user;

    private long revealTicks;
    private Iterator<ItemStack> iterator;
    private boolean forwards = true;

    /**
     * Initialise an instance of a Crate without a Key for validating openings.
     * @param cratesPlugin Instance of the CratesPlugin
     * @param name Name of this Crate (Used for any storage reasons)
     * @param displayName DisplayName of this Crate (The coloured name that'll be shown to players)
     * @param rewards Potential rewards for this Crate
     */
    public Crate(CratesPlugin cratesPlugin, String name, String displayName, ItemStack[] rewards)
    {
        this(cratesPlugin, name, displayName, null, rewards);
    }

    /**
     * Initialise an instance of a Crate with a Key for validating openings.
     * @param cratesPlugin Instance of the CratesPlugin
     * @param name Name of this Crate (Used for any storage reasons)
     * @param displayName DisplayName of this Crate (The coloured name that'll be shown to players)
     * @param key The Key used for validating openings
     * @param rewards Potential rewards for this Crate
     */
    public Crate(CratesPlugin cratesPlugin, String name, String displayName, ItemStack key, ItemStack[] rewards)
    {
        // Putting Crate instance into our "cache"
        instances.add(this);

        // Assigning variables
        this.cratesPlugin = cratesPlugin;
        this.name = name;
        this.displayName = displayName;
        this.key = key;
        this.rewards = rewards;
    }

    /**
     * Return the name of this Crate. This will be for any storage reasons.
     * @return Name of the Crate
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return the display name of this Crate. This has the potential to be coloured.
     * @return Display name of this Crate
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Check if this Crate requires a Key to open.
     * @return True: If this Crate requires a key. False: If this Crate doesn't require a key.
     */
    public boolean requiresKey()
    {
        return getKey() != null;
    }

    /**
     * Get the Key used to open this Crate
     * @return Instance of a Key
     */
    public ItemStack getKey()
    {
        return key;
    }

    /**
     * Get the possible rewards for this Crate
     * @return An array of the possible rewards
     */
    public ItemStack[] getRewards()
    {
        return rewards;
    }

    /**
     * Get the current Inventory instance for this Crate
     * @return Current Inventory.
     */
    public Inventory getInventory()
    {
        return inventory;
    }

    /**
     * Get the current Conveyor for this Crate
     * @return Rewards randomised into a conveyor belt/queue system
     */
    public LinkedList<ItemStack> getConveyor() throws IllegalAccessException
    {
        // No active conveyor because the Crate isn't being opened
        if(!isInUse())
            throw new IllegalAccessException();

        return conveyor;
    }

    /**
     * Found out whether or not this Crate is being opened.
     * @return True if it's being opened and false if not.
     */
    public boolean isInUse()
    {
        return getUser() != null;
    }

    /**
     * Get the Player that's opening this Crate.
     * @return Opening this Crate
     */
    public Player getUser()
    {
        return user;
    }

    /**
     * Find out how long the runnable will run for in ticks before revealing their reward
     * @return Time in ticks before reveal
     */
    public long getRevealTicks()
    {
        return revealTicks;
    }

    /**
     * Method that's called when you want to unregister this Crate
     * @throws IllegalAccessException - Crate isn't being used and tried to be cancelled.
     */
    public void unregister() throws IllegalAccessException
    {
        // If this Crate is in use, cancel the opening process.
        if(isInUse())
            cancelOpening();

        // Unregistering this Listener
        cratesPlugin.unregister(this);
        Crate.removeCrate(this);
    }

    /**
     * Method called when the Crate's opening is being cancelled
     * @throws IllegalAccessException - Crate isn't being used and tried to be cancelled.
     */
    public void cancelOpening() throws IllegalAccessException
    {
        // If it's not in use throw an exception
        if(!isInUse())
            throw new IllegalAccessException("Cannot cancel a crate opening that doesn't exist.");

        // Closing the Inventory of this player
        if(user != null && user.isOnline())
        {
            user.closeInventory();
            inventory = null;
        }

        // Unregistering the listener
        cratesPlugin.unregister(this);

        // Unregistering this BukkitRunnable
        try {
            cancel();
        } catch(IllegalStateException e) {
            // Crate was already cancelled and there's no point in throwing this exception as this is used for a cleanup
        }
    }

    /**
     * Method called when you want to open the Crate for a specific Player
     * @param player To open the Crate for
     */
    public boolean open(Player player)
    {
        // If the inventory is registered already, allow anything
        if(inventory != null)
        {
            player.openInventory(inventory);
            return true;
        }

        // New opener, calling onOpen for more control over opening this Crate.
        if(!onOpen(player))
            return false;

        // Setting user
        user = player;

        // Registering listener
        cratesPlugin.register(this);

        // Opening inventory
        inventory = Bukkit.createInventory(null, 27, this.displayName);
        InventoryUtil.fill(inventory, 0, 8, Items.PLACEHOLDER);
        InventoryUtil.fill(inventory, 18, 26, Items.PLACEHOLDER);
        inventory.setItem(0, Items.POINTER);
        player.openInventory(inventory);

        // Randomising reveal time
        revealTicks = (ThreadLocalRandom.current().nextInt(8, 12) + 1) * 20;

        /*
         * Registering the runnable.
         *  - Used 2 ticks because at 1 tick the Pointer looks glitchy on the client side and the sound gets spammed too much
         */
        runTaskTimer(cratesPlugin, 0L, 2L);
        return true;
    }

    private synchronized ItemStack safeNext()
    {
        if(iterator == null || !iterator.hasNext())
        {
            // Initialising Conveyor
            List<ItemStack> rewards = Lists.newArrayList(this.rewards.clone());
            Collections.shuffle(rewards);
            conveyor = new LinkedList<>(rewards);
            iterator = conveyor.iterator();
        }

        return iterator.next();
    }

    @Override
    public void run()
    {
        // Decrementing ticks until reveal
        --revealTicks;

        // Conveyor section
        {
            /*
             * • slowdownTicks >= revealTicks
             *   That means we need to slow down
             * • Slowing down speed depends on how long until reveal
             *   Closer it gets the more it slows down
             * • It'll stop because there'll be no more ticks
             *   That's the winning slot item (+9 to get the item it's pointing to)
             */

            if(slowdownTicks < revealTicks || (slowdownTicks >= revealTicks && revealTicks % (revealTicks <= 50 && revealTicks > 40 ? 2 : revealTicks <= 40 && revealTicks > 30 ? 6 : revealTicks <= 30 && revealTicks > 20 ? 8 : revealTicks <= 20 ? 14 : 18) == 0))
            {
                // 9th slot is the lowest and 17th is the highest
                for(int i = 9; i < 18; i++)
                {
                    // Getting item to the right
                    ItemStack current = inventory.getItem(i);

                    // Moving current down by one
                    if(current != null && i > 9)
                        inventory.setItem(i - 1, current);

                    inventory.setItem(i, safeNext());
                }
            }
        }

        /*
         * Affected by slowdown, but not as strong/slow to make a more random outcome
         */
        {
            if(slowdownTicks < revealTicks || (slowdownTicks >= revealTicks && revealTicks % (revealTicks <= 50 && revealTicks > 40 ? 2 : revealTicks <= 40 && revealTicks > 30 ? 5 : revealTicks <= 30 && revealTicks > 20 ? 6 : revealTicks <= 20 ? 10 : 14) == 0))
            {
                int slot = inventory.first(Items.POINTER);

                // In case we ever misplace the Pointer
                if(slot == -1)
                {
                    inventory.setItem(0, Items.POINTER);
                    return;
                }

                inventory.setItem(slot, Items.PLACEHOLDER);

                // An absolute dreadful de/incrementing system that I'm ashamed works
                if(slot >= 4)
                {
                    if(!forwards)
                        slot--;
                    else {
                        if(slot >= 8)
                        {
                            forwards = false;
                            slot--;
                        } else slot++;
                    }
                } else if(slot >= 0) {
                    if(!forwards)
                    {
                        if(slot == 0)
                        {
                            forwards = true;
                            slot++;
                        } else slot--;
                    } else slot++;
                }

                inventory.setItem(slot, Items.POINTER);

                // Winning stuff
                if(revealTicks <= 0)
                {
                    ItemStack reward = inventory.getItem(slot + 9);

                    user.sendMessage("You won the item in the slot #" + (slot + 9) + " which is " + (reward == null ? "nothing" : reward.hasItemMeta() && reward.getItemMeta().hasDisplayName() ? reward.getItemMeta().getDisplayName() : reward.getType().toString()));

                    // Cancelling this task
                    this.cancel();

                    // Because some people like sounds
                    user.playSound(user.getLocation(), Sound.NOTE_BASS_DRUM, 1f, 1f);
                    user.playSound(user.getLocation(), Sound.FIREWORK_BLAST, 1f, 0.23f);
                    user.playSound(user.getLocation(), Sound.VILLAGER_YES, 1f, 1f);

                    // Wait 20 ticks before deregistering this Crate
                    cratesPlugin.getServer().getScheduler().runTaskLater(cratesPlugin, () ->
                    {
                        // Error upon deregistering
                        try {
                            unregister();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }, 20L);

                    return;
                }

                user.playSound(user.getLocation(), Sound.NOTE_PLING, 1f, 1f);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Inventory inventory = player.getOpenInventory().getTopInventory();

        // This isn't our inventory to manage
        if(this.inventory == null || inventory == null || getUser() == null || (!this.inventory.getTitle().equals(inventory.getTitle()) && getUser() != player))
            return;

        // If a player leaves lets be mean and not give them their items
        try {
            unregister();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory(), clickedInventory = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();

        // Not a valid item
        if (clickedInventory == null || currentItem == null || this.inventory == null || !isOpenInventoryThis(player))
            return;

        String storedTitle = this.inventory.getTitle();

        // Ensuring the click involves our inventory
        if(!inventory.getTitle().equals(storedTitle) && !clickedInventory.getTitle().equals(storedTitle))
            return;

        // Disallowing entirely if they have our inventory open
        event.setCancelled(true);
    }

    private boolean isOpenInventoryThis(Player player)
    {
        if(player == null || !player.isOnline())
            return false;

        // No inventory open
        if(inventory == null)
            return false;

        InventoryView inventoryView = player.getOpenInventory();
        Inventory topInventory = inventoryView.getTopInventory();

        return topInventory != null && topInventory.getTitle().equals(inventory.getTitle());
    }

    /**
     * Called when a Player requests to open this Crate
     * @param player
     * @return True: Player can open this Crate. False: Player cannot open this Crate.
     */
    public abstract boolean onOpen(Player player);

}