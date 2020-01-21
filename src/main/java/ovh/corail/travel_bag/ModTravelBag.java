package ovh.corail.travel_bag;

import com.google.common.reflect.Reflection;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.CompatibilityQuark;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.gui.TravelBagScreen;
import ovh.corail.travel_bag.item.TravelBagItem;
import ovh.corail.travel_bag.network.PacketHandler;
import ovh.corail.travel_bag.registry.ModContainers;
import ovh.corail.travel_bag.registry.ModItems;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@SuppressWarnings("UnstableApiUsage")
@Mod(MOD_ID)
public class ModTravelBag {
    public static final String MOD_ID = "travel_bag";
    public static final String MOD_NAME = "Travel Bag";

    public ModTravelBag() {
        Reflection.initialize(PacketHandler.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TravelBagConfig.GENERAL_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        if (SupportMods.TOMBSTONE.isLoaded()) {
            CompatibilityTombstone.INSTANCE.register();
        }
    }

    private void clientInit(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainers.TRAVEL_BAG, SupportMods.QUARK.isLoaded() ? CompatibilityQuark.ButtonIgnoredScreen::new : TravelBagScreen::new);
        event.getMinecraftSupplier().get().getItemColors().register(TravelBagItem::getColor, ModItems.TRAVEL_BAG);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (SupportMods.CURIOS.isLoaded()) {
            CompatibilityCurios.INSTANCE.sendIMC();
        }
    }
}
