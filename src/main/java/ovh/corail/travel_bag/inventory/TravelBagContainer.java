package ovh.corail.travel_bag.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.helper.Helper;
import ovh.corail.travel_bag.inventory.slot.GluttonySlot;
import ovh.corail.travel_bag.inventory.slot.LockedSlot;
import ovh.corail.travel_bag.inventory.slot.TravelBagSlot;
import ovh.corail.travel_bag.registry.ModContainers;

public class TravelBagContainer extends Container {
    private static final int SLOT_SIZE = 18;
    private static final int MIN_START_X = 8;
    public static final int GLUTTONY_SLOT_ID = 78;
    public static final int MAX_SLOT_ID = GLUTTONY_SLOT_ID + 1;
    private final int LINE_MAX, ROW_MAX = 6;
    private final IItemHandlerModifiable handler;
    private final boolean isEnchanted;
    private final ItemStack stack;
    public enum BagPlace { MAIN_HAND, CURIOS_BAG_0, CURIOS_BAG_1 }
    private final BagPlace bagPlace;

    protected TravelBagContainer(ContainerType<? extends TravelBagContainer> containerType, int windowId, PlayerInventory playerInventory, BagPlace bagPlace) {
        super(containerType, windowId);
        this.bagPlace = bagPlace;
        this.stack = Helper.getContainerBagStack(playerInventory.player, bagPlace);
        this.isEnchanted = SupportMods.TOMBSTONE.isLoaded() && CompatibilityTombstone.INSTANCE.isEnchantedBag(this.stack);
        this.handler = (IItemHandlerModifiable) this.stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(new ContainerStackHandler(MAX_SLOT_ID));
        this.LINE_MAX = 9 + (this.isEnchanted ? 4 : 0);
        addAllSlots(playerInventory);
    }

    public TravelBagContainer(int windowId, PlayerInventory playerInventory, BagPlace place) {
        this(ModContainers.TRAVEL_BAG, windowId, playerInventory, place);
    }

    public TravelBagContainer(int windowId, PlayerInventory playerInventory, int bagPlaceId) {
        this(ModContainers.TRAVEL_BAG, windowId, playerInventory, BagPlace.values()[MathHelper.clamp(bagPlaceId, 0, BagPlace.values().length - 1)]);
    }

    public TravelBagContainer(int windowId, PlayerInventory playerInventory) {
        this(ModContainers.TRAVEL_BAG, windowId, playerInventory, BagPlace.MAIN_HAND);
    }

    public BagPlace getBagPlace() {
        return this.bagPlace;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        CompoundNBT tag = this.stack.getOrCreateTag();
        ListNBT inventList = new ListNBT();
        for (int slot = 0; slot < this.handler.getSlots(); slot++) {
            ItemStack stack = this.handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                CompoundNBT itemTag = stack.serializeNBT();
                itemTag.putInt("Slot", slot);
                inventList.add(itemTag);
            }
        }
        tag.put("custom_inventory", inventList);
        player.container.detectAndSendChanges();

        PlayerInventory playerinventory = player.inventory;
        if (!playerinventory.getItemStack().isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, playerinventory.getItemStack(), player.inventory.currentItem);
            playerinventory.setItemStack(ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int countSlots = this.LINE_MAX * ROW_MAX;
            if (index < countSlots) {
                if (!mergeItemStack(itemstack1, countSlots + 1, this.inventorySlots.size() - 9, false) && !mergeItemStack(itemstack1, this.inventorySlots.size() - 9, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemstack1, 0, countSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return Helper.getContainerBagStack(player).equals(this.stack);
    }

    @Override
    public ContainerType<?> getType() {
        return ModContainers.TRAVEL_BAG;
    }

    public void addAllSlots(PlayerInventory playerInventory) {
        // bag slots
        int slotNum = 0, startX, startY = 18;
        for (int j = 0; j < ROW_MAX; ++j) {
            startX = MIN_START_X;
            for (int i = 0; i < this.LINE_MAX; ++i) {
                addSlot(new TravelBagSlot(this.handler, slotNum++, startX, startY));
                startX += SLOT_SIZE;
            }
            startY += SLOT_SIZE;
        }
        addSlot(new GluttonySlot(this.handler, GLUTTONY_SLOT_ID, MIN_START_X + 5, 142, playerInventory.player));
        // fill bag slots
        CompoundNBT nbt = this.stack.getTag();
        if (nbt != null && nbt.contains("custom_inventory", Constants.NBT.TAG_LIST)) {
            ListNBT inventList = nbt.getList("custom_inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inventList.size(); i++) {
                CompoundNBT tag = inventList.getCompound(i);
                this.handler.setStackInSlot(tag.getInt("Slot"), ItemStack.read(tag));
            }
        }
        // player slots
        slotNum = 9;
        startY += 14;
        for (int j = 0; j < 3; ++j) {
            startX = MIN_START_X + (this.isEnchanted ? 36 : 0);
            for (int i = 0; i < 9; ++i) {
                addSlot(new Slot(playerInventory, slotNum++, startX, startY));
                startX += SLOT_SIZE;
            }
            startY += SLOT_SIZE;
        }
        slotNum = 0;
        startX = MIN_START_X + (this.isEnchanted ? 36 : 0);
        startY += 4;
        for (int i = 0; i < 9; ++i) {
            if (playerInventory.getStackInSlot(i).equals(this.stack)) {
                addSlot(new LockedSlot(playerInventory, slotNum++, startX, startY));
            } else {
                addSlot(new Slot(playerInventory, slotNum++, startX, startY));
            }
            startX += SLOT_SIZE;
        }
    }
}
