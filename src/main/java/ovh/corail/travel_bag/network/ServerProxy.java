package ovh.corail.travel_bag.network;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import ovh.corail.travel_bag.config.TravelBagConfig;

import static ovh.corail.travel_bag.ModTravelBag.LOGGER;

public class ServerProxy implements IProxy {
    private boolean isConfigDirty = false;

    @Override
    public void updateConfigIfDirty() {
        if (this.isConfigDirty) {
            this.isConfigDirty = false;
            LOGGER.info("Syncing Config on Client");
            PacketHandler.sendToAllPlayers(TravelBagConfig.getUpdatePacket());
        }
    }

    @Override
    public void markConfigDirty() {
        if (!((MinecraftServer) LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER)).getPlayerList().getPlayers().isEmpty()) {
            this.isConfigDirty = true;
        }
    }
}
