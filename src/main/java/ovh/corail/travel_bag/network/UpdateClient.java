package ovh.corail.travel_bag.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.travel_bag.config.TravelBagConfig;

import java.util.function.Supplier;

public class UpdateClient {
    public boolean disableGluttonySlot;

    public UpdateClient(boolean disableGluttonySlot) {
        this.disableGluttonySlot = disableGluttonySlot;
    }

    public static UpdateClient fromBytes(PacketBuffer buf) {
        return new UpdateClient(buf.readBoolean());
    }

    public static void toBytes(UpdateClient msg, PacketBuffer buf) {
        buf.writeBoolean(msg.disableGluttonySlot);
    }

    public static class Handler {
        public static void handle(final UpdateClient message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                TravelBagConfig.handleClientPacket(message);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
