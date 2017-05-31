package uk.co.loonyrules.rp.melonco.enums;

import org.bukkit.Material;
import uk.co.loonyrules.rp.melonco.crates.Crate;
import uk.co.loonyrules.rp.melonco.crates.EnderCrate;

import java.util.Arrays;
import java.util.Optional;

/**
 * This CratesPlugin, because of the non-dynamic support, won't support multiple crates using the same Material.
 * If I were to need to make this support I'd store the Crates in a config and then load them in with locations
 * that I can cycle through to double check the integrity instead of forcing 1 block to 1 Crate.
 */
public enum CrateType
{

    ENDER(Material.ENDER_CHEST, EnderCrate.class);

    private final Material type;
    private final Class<? extends Crate> crateClass;

    CrateType(Material type, Class<? extends Crate> crateClass)
    {
        this.type = type;
        this.crateClass = crateClass;
    }

    public Material getType()
    {
        return type;
    }

    public Class<? extends Crate> getCrateClass()
    {
        return crateClass;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Optional<CrateType> getType(Material material)
    {
        return Arrays.stream(values()).filter(crateType -> crateType.getType() == material).findFirst();
    }

}
