package ovh.corail.travel_bag.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;

public class GluttonySlot extends TravelBagSlot {
    private boolean hasGluttony;

    public GluttonySlot(IItemHandler handler, int index, int xPosition, int yPosition, PlayerEntity player) {
        super(handler, index, xPosition, yPosition);
        this.hasGluttony = SupportMods.TOMBSTONE.isLoaded(); // TODO disabled // && CompatibilityTombstone.INSTANCE.hasPerkLevel(player, 1);
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {
        return this.hasGluttony && super.canTakeStack(player);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.hasGluttony && super.isItemValid(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isEnabled() {
        return this.hasGluttony;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }
}
