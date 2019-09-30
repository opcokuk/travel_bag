package ovh.corail.travel_bag;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.gui.TravelBagScreen;
import ovh.corail.travel_bag.item.TravelBagItem;
import ovh.corail.travel_bag.network.OpenCuriosBagPacket;
import ovh.corail.travel_bag.network.TakeAllPacket;
import ovh.corail.travel_bag.registry.ModContainers;
import ovh.corail.travel_bag.registry.ModItems;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@Mod(MOD_ID)
public class ModTravelBag {
    public static final String MOD_ID = "travel_bag";
    public static final String MOD_NAME = "Travel Bag";
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "travel_bag_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public ModTravelBag() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TravelBagConfig.GENERAL_SPEC);
        ModTravelBag.HANDLER.registerMessage(0, TakeAllPacket.class, TakeAllPacket::toBytes, TakeAllPacket::fromBytes, TakeAllPacket.Handler::handle);
        ModTravelBag.HANDLER.registerMessage(1, OpenCuriosBagPacket.class, OpenCuriosBagPacket::toBytes, OpenCuriosBagPacket::fromBytes, OpenCuriosBagPacket.Handler::handle);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        if (SupportMods.TOMBSTONE.isLoaded()) {
            CompatibilityTombstone.INSTANCE.register();
        }
    }

    private void clientInit(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainers.TRAVEL_BAG, TravelBagScreen::new);
        event.getMinecraftSupplier().get().getItemColors().register(TravelBagItem::getColor, ModItems.TRAVEL_BAG);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (SupportMods.CURIOS.isLoaded()) {
            CompatibilityCurios.INSTANCE.sendIMC();
        }
    }
}
