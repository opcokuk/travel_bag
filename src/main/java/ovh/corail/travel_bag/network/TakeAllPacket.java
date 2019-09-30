package ovh.corail.travel_bag.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import ovh.corail.travel_bag.helper.Helper;
import ovh.corail.travel_bag.registry.ModItems;

import java.util.function.Supplier;

public class TakeAllPacket {

    public TakeAllPacket() {
    }

    public static TakeAllPacket fromBytes(PacketBuffer buf) {
        return new TakeAllPacket();
    }

    public static void toBytes(TakeAllPacket msg, PacketBuffer buf) {
    }

    public static class Handler {
        public static void handle(final TakeAllPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    ItemStack heldStack = Helper.getContainerBagStack(player);
                    if (heldStack.getItem() == ModItems.TRAVEL_BAG) {
                        heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                            for (int slot = 0; slot < cap.getSlots(); slot++) {
                                if (!cap.getStackInSlot(slot).isEmpty()) {
                                    ItemHandlerHelper.giveItemToPlayer(player, cap.extractItem(slot, 64, false), -1);
                                }
                            }
                        });
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

