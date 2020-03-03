package ovh.corail.travel_bag.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.inventory.TravelBagContainer;
import ovh.corail.travel_bag.inventory.TravelBagContainer.BagPlace;

public class Helper {

    public static boolean compareTags(ItemStack stack1, ItemStack stack2) {
        boolean hasTag1 = stack1.hasTag();
        return hasTag1 == stack2.hasTag() && (!hasTag1 || stack1.getTag().equals(stack2.getTag())) && stack1.areCapsCompatible(stack2);
    }

    public static ItemStack getContainerBagStack(PlayerEntity player) {
        if (player.openContainer instanceof TravelBagContainer) {
            return getContainerBagStack(player, ((TravelBagContainer)player.openContainer).getBagPlace());
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getContainerBagStack(PlayerEntity player, BagPlace place) {
        if (place == BagPlace.MAIN_HAND) {
            return player.getHeldItemMainhand();
        } else if (SupportMods.CURIOS.isLoaded()) {
            return CompatibilityCurios.INSTANCE.getCuriosStack(player, place == BagPlace.CURIOS_BAG_0);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isPacketToClient(NetworkEvent.Context ctx) {
        return ctx.getDirection().getOriginationSide() == LogicalSide.SERVER && ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT;
    }

    public static boolean isPacketToServer(NetworkEvent.Context ctx) {
        return ctx.getDirection().getOriginationSide() == LogicalSide.CLIENT && ctx.getDirection().getReceptionSide() == LogicalSide.SERVER;
    }
}
