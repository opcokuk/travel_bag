package ovh.corail.travel_bag.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.helper.Helper;

import java.util.function.Supplier;

public class OpenCuriosBagPacket {
    private boolean isFirstSlot;

    public OpenCuriosBagPacket(boolean isFirstSlot) {
        this.isFirstSlot = isFirstSlot;
    }

    static OpenCuriosBagPacket fromBytes(PacketBuffer buf) {
        return new OpenCuriosBagPacket(buf.readBoolean());
    }

    static void toBytes(OpenCuriosBagPacket msg, PacketBuffer buf) {
        buf.writeBoolean(msg.isFirstSlot);
    }

    static class Handler {
        static void handle(final OpenCuriosBagPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context ctx = contextSupplier.get();
            if (Helper.isPacketToServer(ctx)) {
                ctx.enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.getSender();
                    if (player != null && SupportMods.CURIOS.isLoaded()) {
                        CompatibilityCurios.INSTANCE.openBag(player, message.isFirstSlot);
                    }
                });
            }
            ctx.setPacketHandled(true);
        }
    }
}
