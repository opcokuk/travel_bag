package ovh.corail.travel_bag.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "travel_bag_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    static {
        HANDLER.registerMessage(0, TakeAllPacket.class, TakeAllPacket::toBytes, TakeAllPacket::fromBytes, TakeAllPacket.Handler::handle);
        HANDLER.registerMessage(1, OpenCuriosBagPacket.class, OpenCuriosBagPacket::toBytes, OpenCuriosBagPacket::fromBytes, OpenCuriosBagPacket.Handler::handle);
    }

    public static <T> void sendToServer(T message) {
        HANDLER.sendToServer(message);
    }
}
