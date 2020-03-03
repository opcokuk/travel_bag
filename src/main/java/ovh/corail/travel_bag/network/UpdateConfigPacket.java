package ovh.corail.travel_bag.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.helper.Helper;

import java.util.function.Supplier;

public class UpdateConfigPacket {
    public final boolean disableEnchantedTravelBag, disableGluttonySlot;
    public final int gluttonySlotSpeed;

    public UpdateConfigPacket(boolean disableEnchantedTravelBag, boolean disableGluttonySlot, int gluttonySlotSpeed) {
        this.disableEnchantedTravelBag = disableEnchantedTravelBag;
        this.disableGluttonySlot = disableGluttonySlot;
        this.gluttonySlotSpeed = gluttonySlotSpeed;
    }

    static UpdateConfigPacket fromBytes(PacketBuffer buf) {
        return new UpdateConfigPacket(buf.readBoolean(), buf.readBoolean(), buf.readInt());
    }

    static void toBytes(UpdateConfigPacket msg, PacketBuffer buf) {
        buf.writeBoolean(msg.disableEnchantedTravelBag);
        buf.writeBoolean(msg.disableGluttonySlot);
        buf.writeInt(msg.gluttonySlotSpeed);
    }

    static class Handler {
        static void handle(UpdateConfigPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context ctx = contextSupplier.get();
            if (Helper.isPacketToClient(ctx)) {
                ctx.enqueueWork(() -> TravelBagConfig.updateConfig(message));
            }
            ctx.setPacketHandled(true);
        }
    }
}
