package ovh.corail.travel_bag.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.SupportMods;

import java.util.function.Supplier;

public class OpenCuriosBagPacket {
    private boolean isFirstSlot;

    public OpenCuriosBagPacket(boolean isFirstSlot) {
        this.isFirstSlot = isFirstSlot;
    }

    public static OpenCuriosBagPacket fromBytes(PacketBuffer buf) {
        return new OpenCuriosBagPacket(buf.readBoolean());
    }

    public static void toBytes(OpenCuriosBagPacket msg, PacketBuffer buf) {
        buf.writeBoolean(msg.isFirstSlot);
    }

    public static class Handler {
        public static void handle(final OpenCuriosBagPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null && SupportMods.CURIOS.isLoaded()) {
                    CompatibilityCurios.INSTANCE.openBag(player, message.isFirstSlot);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
