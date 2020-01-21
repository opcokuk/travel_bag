package ovh.corail.travel_bag.compatibility;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.ModList;

public enum SupportMods implements IStringSerializable {
    CURIOS("curios"),
    TOMBSTONE("tombstone"),
    QUARK("quark");

    private final String modid;
    private final boolean loaded;

    SupportMods(String modid) {
        this.modid = modid;
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public String getName() {
        return this.modid;
    }
}
