package ovh.corail.travel_bag.compatibility;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.ModList;
import ovh.corail.travel_bag.helper.Helper;

public enum SupportMods implements IStringSerializable {
    CURIOS("curios"),
    TOMBSTONE("tombstone"),
    QUARK("quark", "vazkii.quark.api.IQuarkButtonIgnored");

    private final String modid;
    private final boolean loaded;

    SupportMods(String modid, String... classNamesToCheck) {
        this.modid = modid;
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent() && (classNamesToCheck.length == 0 || Helper.existClass(classNamesToCheck));
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public String getName() {
        return this.modid;
    }
}
