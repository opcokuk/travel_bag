package ovh.corail.travel_bag.registry;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import ovh.corail.travel_bag.recipe.RecipeColoredTravelBag;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@ObjectHolder(MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSerializers {
    public static final IRecipeSerializer<RecipeColoredTravelBag> COLORED_TRAVEL_BAG = new RecipeColoredTravelBag.Serializer<>(RecipeColoredTravelBag::new);

    @SubscribeEvent
    public static void onRegisterSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(COLORED_TRAVEL_BAG.setRegistryName(RecipeColoredTravelBag.Serializer.NAME));
    }
}
