package ovh.corail.travel_bag;

import com.google.common.reflect.Reflection;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.config.TravelBagModConfig;
import ovh.corail.travel_bag.gui.TravelBagScreen;
import ovh.corail.travel_bag.item.TravelBagItem;
import ovh.corail.travel_bag.network.ClientProxy;
import ovh.corail.travel_bag.network.IProxy;
import ovh.corail.travel_bag.network.PacketHandler;
import ovh.corail.travel_bag.network.ServerProxy;
import ovh.corail.travel_bag.registry.ModContainers;
import ovh.corail.travel_bag.registry.ModItems;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@Mod(MOD_ID)
public class ModTravelBag {
    public static final String MOD_ID = "travel_bag";
    public static final String MOD_NAME = "Travel Bag";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final IProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    @SuppressWarnings("UnstableApiUsage")
    public ModTravelBag() {
        Reflection.initialize(PacketHandler.class);
        registerSharedConfig(ModLoadingContext.get());
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::clientInit);
        modBus.addListener(this::enqueueIMC);
        if (SupportMods.TOMBSTONE.isLoaded()) {
            LOGGER.info("Registring Gluttony Perk");
            CompatibilityTombstone.INSTANCE.register();
        }
        PROXY.preInit();
    }

    private void registerSharedConfig(ModLoadingContext context) {
        context.getActiveContainer().addConfig(new TravelBagModConfig(TravelBagConfig.GENERAL_SPEC, context.getActiveContainer()));
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
