package ovh.corail.travel_bag.registry;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ovh.corail.travel_bag.inventory.TravelBagContainer;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers {
    public static final ContainerType<TravelBagContainer> TRAVEL_BAG = IForgeContainerType.create(TravelBagContainer::new);

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(TRAVEL_BAG.setRegistryName(MOD_ID, "travel_bag"));
    }
}
