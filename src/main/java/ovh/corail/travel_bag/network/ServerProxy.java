package ovh.corail.travel_bag.network;

import ovh.corail.travel_bag.config.TravelBagConfig;

import static ovh.corail.travel_bag.ModTravelBag.LOGGER;

public class ServerProxy implements IProxy {
    @Override
    public void updateConfig() {
        LOGGER.info("Syncing Config on Client");
        PacketHandler.sendToAllPlayers(TravelBagConfig.getUpdatePacket());
    }
}
