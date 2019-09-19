package ovh.corail.travel_bag.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import ovh.corail.travel_bag.registry.ModTags;

public class TravelBagSlot extends SlotItemHandler {
    public int timeInUse = 0;

    public TravelBagSlot(IItemHandler handler, int index, int xPosition, int yPosition) {
        super(handler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.getItem().isIn(ModTags.Items.TRAVEL_BAG_DENIED_ITEMS) && super.isItemValid(stack);
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        this.timeInUse = 40;
    }
}
