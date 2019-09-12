package ovh.corail.travel_bag.registry;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import ovh.corail.travel_bag.item.TravelBagItem;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@ObjectHolder(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final Item TRAVEL_BAG = Items.AIR;

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new TravelBagItem().setRegistryName(MOD_ID, "travel_bag"));
    }
}
