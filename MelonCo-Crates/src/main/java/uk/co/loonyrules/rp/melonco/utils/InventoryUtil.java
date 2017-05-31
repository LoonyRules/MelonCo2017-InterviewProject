package uk.co.loonyrules.rp.melonco.utils;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil
{

    public static void fill(Inventory inventory, int start, int finish, ItemStack itemStack)
    {
        for(int i = start; i <= finish; i++)
            inventory.setItem(i, itemStack);
    }

    public static boolean isValidClick(ClickType clickType)
    {
        return clickType.isLeftClick() || clickType.isRightClick() || clickType.isShiftClick();
    }

}