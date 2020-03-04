package ovh.corail.travel_bag.network;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import ovh.corail.travel_bag.config.TravelBagConfig;

import static ovh.corail.travel_bag.ModTravelBag.LOGGER;

public class ServerProxy implements IProxy {
    private boolean isConfigDirty = false;

    @Override
    public void preInit() {
        // only register the event on dedicated server
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Override
    public void markConfigDirty() {
        if (!((MinecraftServer) LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER)).getPlayerList().getPlayers().isEmpty()) {
            this.isConfigDirty = true;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && this.isConfigDirty) {
            this.isConfigDirty = false;
            LOGGER.info("Syncing Config on Client");
            PacketHandler.sendToAllPlayers(TravelBagConfig.getUpdatePacket());
        }
    }
}
