package ovh.corail.travel_bag.compatibility;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

public class CompatibilityCurios {
    public static final CompatibilityCurios INSTANCE = new CompatibilityCurios();
    private final ResourceLocation EMPTY_BAG = new ResourceLocation("curios", "textures/item/empty_bag_slot.png");
    @CapabilityInject(ICurioItemHandler.class)
    public static final Capability<ICurioItemHandler> INVENTORY = null;
    @CapabilityInject(ICurio.class)
    public static final Capability<ICurio> ITEM = null;

    public void sendIMC() {
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("bag").setSize(2));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_ICON, () -> new Tuple<>("bag", EMPTY_BAG));
    }

    public <T> LazyOptional<T> getCuriosCapability(Capability<T> cap) {
        return ITEM != null && cap == ITEM ? LazyOptional.of(TravelBagCuriosCap::new).cast() : LazyOptional.empty();
    }

    public class TravelBagCuriosCap implements ICurio {
    }
}
