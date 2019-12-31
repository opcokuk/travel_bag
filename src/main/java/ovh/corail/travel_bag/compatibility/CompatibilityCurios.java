package ovh.corail.travel_bag.compatibility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.network.NetworkHooks;
import ovh.corail.travel_bag.inventory.TravelBagContainer;
import ovh.corail.travel_bag.inventory.TravelBagContainer.BagPlace;
import ovh.corail.travel_bag.network.OpenCuriosBagPacket;
import ovh.corail.travel_bag.network.PacketHandler;
import ovh.corail.travel_bag.registry.ModItems;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;

import javax.annotation.Nullable;
import java.util.SortedMap;

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

    public void openBag(PlayerEntity player, boolean isFirstSlot) {
        CurioStackHandler handler = getBagStackHandler(player);
        if (handler != null) {
            if (player.world.isRemote) {
                PacketHandler.sendToServer(new OpenCuriosBagPacket(isFirstSlot));
            } else {
                ItemStack stack = handler.getStackInSlot(isFirstSlot ? 0 : 1);
                if (stack.getItem() == ModItems.TRAVEL_BAG) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                        @Override
                        public ITextComponent getDisplayName() {
                            return stack.getDisplayName();
                        }

                        @Override
                        public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
                            return new TravelBagContainer(windowId, playerInventory, isFirstSlot ? BagPlace.CURIOS_BAG_0 : BagPlace.CURIOS_BAG_1);
                        }
                    }, buf -> buf.writeInt(isFirstSlot ? 1 : 2));
                }
            }
        }
    }

    public ItemStack getCuriosStack(PlayerEntity player, boolean isFirstSlot) {
        CurioStackHandler handler = getBagStackHandler(player);
        return handler != null ? handler.getStackInSlot(isFirstSlot ? 0 : 1) : ItemStack.EMPTY;
    }

    @Nullable
    private CurioStackHandler getBagStackHandler(PlayerEntity player) {
        return INVENTORY != null ? player.getCapability(INVENTORY, null).map(iCurioItemHandler -> {
            SortedMap<String, CurioStackHandler> map = iCurioItemHandler.getCurioMap();
            return map != null ? map.get("bag") : null;
        }).orElse(null) : null;
    }

    public <T> LazyOptional<T> getCuriosCapability(Capability<T> cap) {
        return ITEM != null && cap == ITEM ? LazyOptional.of(TravelBagCuriosCap::new).cast() : LazyOptional.empty();
    }

    public class TravelBagCuriosCap implements ICurio {
    }
}
