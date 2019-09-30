package ovh.corail.travel_bag.registry;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import ovh.corail.travel_bag.inventory.TravelBagContainer;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@ObjectHolder(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers {
    public static final ContainerType<TravelBagContainer> TRAVEL_BAG = null;

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((windowId, playerInventory, data) -> new TravelBagContainer(windowId, playerInventory, data.getInt(0))).setRegistryName(MOD_ID, "travel_bag"));
    }
}
