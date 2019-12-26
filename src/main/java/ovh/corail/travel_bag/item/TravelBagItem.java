package ovh.corail.travel_bag.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.helper.Helper;
import ovh.corail.travel_bag.helper.StyleType;
import ovh.corail.travel_bag.inventory.ContainerStackHandler;
import ovh.corail.travel_bag.inventory.TravelBagContainer;
import ovh.corail.travel_bag.inventory.TravelBagContainer.BagPlace;
import ovh.corail.travel_bag.registry.ModTabs;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class TravelBagItem extends Item {

    public TravelBagItem() {
        super(new Properties().group(ModTabs.mainTab).maxStackSize(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        if (world == null) {
            return;
        }
        list.add(new TranslationTextComponent(getTranslationKey() + ".desc").setStyle(StyleType.TOOLTIP_DESC));
        if (SupportMods.TOMBSTONE.isLoaded() && !TravelBagConfig.general.disableEnchantedTravelBag.get()) {
            if (!CompatibilityTombstone.INSTANCE.isEnchantedBag(stack)) {
                list.add(new TranslationTextComponent(getTranslationKey() + ".enchant").setStyle(StyleType.TOOLTIP_USE));
            } else if (!TravelBagConfig.general.disableGluttonySlot.get() && !CompatibilityTombstone.INSTANCE.hasPerkLevel(Minecraft.getInstance().player, 1)) {
                list.add(new TranslationTextComponent(getTranslationKey() + ".gluttony").setStyle(StyleType.TOOLTIP_USE));
            }
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            addStacks(items, 16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001, 2328370, 3823310);
        }
    }

    private void addStacks(NonNullList<ItemStack> items, int... colorCodes) {
        for (int colorCode : colorCodes) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt("color", colorCode);
            items.add(stack);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || getColor(oldStack, 0) != getColor(newStack, 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slotId, boolean isSelected) {
        if (TravelBagConfig.general.disableGluttonySlot.get()) {
            return;
        }
        int speed = Math.max(20, TravelBagConfig.general.gluttonySlotSpeed.get());
        boolean isEnchantedBag = SupportMods.TOMBSTONE.isLoaded() && CompatibilityTombstone.INSTANCE.isEnchantedBag(stack);
        if (!isEnchantedBag || !(entity instanceof PlayerEntity) || entity.world.isRemote || entity.ticksExisted % speed != 0) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (!SupportMods.TOMBSTONE.isLoaded() || !CompatibilityTombstone.INSTANCE.hasPerkLevel(player, 1)) {
            return;
        }
        boolean isBagContainer = Helper.getContainerBagStack(player).equals(stack) && player.openContainer instanceof TravelBagContainer;
        ItemStack gluttonyStack = getGluttonyStack(stack, isBagContainer);
        if (gluttonyStack.isEmpty()) {
            return;
        }
        Pair<Integer, ItemStack> playerSlot = findStackInPlayerInventory(gluttonyStack, player);
        if (playerSlot.getLeft() == -1) {
            return;
        }
        LazyOptional<IItemHandler> playerCapHolder = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (isBagContainer) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(capItem -> {
                int emptySlotId = -1;
                boolean isStackable = playerSlot.getRight().isStackable();
                // try to stack
                for (int num = 0; num < capItem.getSlots() - 1; num++) {
                    ItemStack currentStackInBag = capItem.getStackInSlot(num);
                    if (currentStackInBag.isEmpty()) {
                        if (emptySlotId > -1) {
                            continue;
                        }
                        emptySlotId = num;
                        if (!isStackable) {
                            break;
                        }
                    }
                    if (isStackable && currentStackInBag.getItem() == playerSlot.getRight().getItem() && currentStackInBag.getCount() < currentStackInBag.getMaxStackSize()) {
                        if (playerCapHolder.map(capInvent -> {
                            if (Helper.compareTags(currentStackInBag, playerSlot.getRight())) {
                                ItemHandlerHelper.insertItemStacked(capItem, capInvent.extractItem(playerSlot.getLeft(), 1, false).copy(), false);
                                return true;
                            }
                            return false;
                        }).orElse(false)) {
                            return;
                        }
                    }
                }
                // fill empty slot
                if (emptySlotId > -1) {
                    final int finalEmptySlotId = emptySlotId;
                    playerCapHolder.ifPresent(capInvent -> capItem.insertItem(finalEmptySlotId, capInvent.extractItem(playerSlot.getLeft(), 1, false).copy(), false));
                }
            });
            return;
        }
        // when it's not the opened container
        CompoundNBT tag = stack.getOrCreateTag();
        ListNBT bagNBT = tag.getList("custom_inventory", Constants.NBT.TAG_COMPOUND);
        Pair<Integer, ItemStack> res = findStackInBagInventoryOrEmpty(playerSlot.getRight(), bagNBT);
        if (res.getLeft() < 0) {
            return;
        }
        playerCapHolder.ifPresent(capInvent -> {
            ItemStack extracted = capInvent.extractItem(playerSlot.getLeft(), 1, false);
            if (res.getRight().isEmpty()) {
                setStackInCustomInventory(bagNBT, res.getLeft(), extracted.copy());
            } else {
                res.getRight().grow(1);
                setStackInCustomInventory(bagNBT, res.getLeft(), res.getRight());
            }
        });
    }

    private Pair<Integer, ItemStack> findStackInPlayerInventory(ItemStack stack, ServerPlayerEntity player) {
        return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(capInvent -> {
            for (int slotId = 0; slotId < capInvent.getSlots(); slotId++) {
                ItemStack currentStack = capInvent.getStackInSlot(slotId);
                if (currentStack.getItem() == stack.getItem()) {
                    return Pair.of(slotId, currentStack);
                }
            }
            return Pair.of(-1, ItemStack.EMPTY);
        }).orElse(Pair.of(-1, ItemStack.EMPTY));
    }

    private Pair<Integer, ItemStack> findStackInBagInventoryOrEmpty(ItemStack trackedStack, ListNBT bagNBT) {
        String registryName = trackedStack.getItem().getRegistryName().toString();
        Set<Integer> ids = IntStream.range(0, TravelBagContainer.GLUTTONY_SLOT_ID).boxed().collect(Collectors.toSet());
        boolean isStackable = trackedStack.isStackable();
        bagNBT.sort(Comparator.comparingInt(o -> ((CompoundNBT) o).getInt("Slot")));
        for (int i = 0; i < bagNBT.size(); i++) {
            CompoundNBT slotNBT = bagNBT.getCompound(i);
            int slotId = slotNBT.getInt("Slot");
            if (slotId == TravelBagContainer.GLUTTONY_SLOT_ID) {
                continue;
            }
            ids.remove(slotId);
            if (isStackable && registryName.equals(slotNBT.getString("id"))) {
                ItemStack currentStack = ItemStack.read(slotNBT);
                if (currentStack.getCount() < trackedStack.getMaxStackSize()) {
                    if (Helper.compareTags(currentStack, trackedStack)) {
                        return Pair.of(slotId, currentStack);
                    }
                }
            }
        }
        return Pair.of(ids.stream().min(Integer::compareTo).orElse(-1), ItemStack.EMPTY);
    }

    private ItemStack getGluttonyStack(ItemStack bag, boolean isBagContainer) {
        if (bag.getItem() == this) {
            if (isBagContainer) {
                return bag.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(capItem -> capItem.getStackInSlot(TravelBagContainer.GLUTTONY_SLOT_ID)).orElse(ItemStack.EMPTY);
            } else {
                CompoundNBT tag = bag.getOrCreateTag();
                if (tag.contains("custom_inventory", Constants.NBT.TAG_LIST)) {
                    ListNBT inventList = tag.getList("custom_inventory", Constants.NBT.TAG_COMPOUND);
                    return getStackInCustomInventory(inventList, TravelBagContainer.GLUTTONY_SLOT_ID);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private void setStackInCustomInventory(ListNBT inventList, int slotId, ItemStack stack) {
        CompoundNBT itemTag = stack.serializeNBT();
        itemTag.putInt("Slot", slotId);
        inventList.removeIf(tag -> ((CompoundNBT) tag).getInt("Slot") == slotId);
        inventList.add(itemTag);
    }

    private ItemStack getStackInCustomInventory(ListNBT inventList, int slotId) {
        for (int i = 0; i < inventList.size(); i++) {
            CompoundNBT entry = inventList.getCompound(i);
            if (entry.getInt("Slot") == slotId) {
                CompoundNBT slotNBT = inventList.getCompound(i);
                return ItemStack.read(slotNBT);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!player.world.isRemote && hand == Hand.MAIN_HAND) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent(getTranslationKey());
                }

                @Override
                public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new TravelBagContainer(windowId, playerInventory, BagPlace.MAIN_HAND);
                }
            }, buf -> buf.writeInt(0));
            return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ITextComponent displayName = super.getDisplayName(stack);
        boolean isEnchanted = SupportMods.TOMBSTONE.isLoaded() && CompatibilityTombstone.INSTANCE.isEnchantedBag(stack);
        return isEnchanted ? new TranslationTextComponent(MOD_ID + ".enchanted_item", displayName) : displayName;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return SupportMods.TOMBSTONE.isLoaded() && CompatibilityTombstone.INSTANCE.isEnchantedBag(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        if (stack.getItem() == this && stack.getTag() != null && stack.getTag().contains("custom_inventory", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT tag = stack.getTag().copy();
            tag.remove("custom_inventory");
            return tag;
        }
        return stack.getTag();
    }

    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider();
    }

    public static class CapProvider implements ICapabilityProvider {
        private final ContainerStackHandler handler = new ContainerStackHandler(TravelBagContainer.MAX_SLOT_ID);

        CapProvider() {
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return LazyOptional.of(() -> handler).cast();
            }
            if (SupportMods.CURIOS.isLoaded() && cap == CompatibilityCurios.ITEM) {
                return CompatibilityCurios.INSTANCE.getCuriosCapability(cap);
            }
            if (SupportMods.TOMBSTONE.isLoaded() && cap == CompatibilityTombstone.SOUL_CONSUMER_CAPABILITY) {
                return CompatibilityTombstone.INSTANCE.getTravelBagCapability(cap);
            }
            return LazyOptional.empty();
        }
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains("color", Constants.NBT.TAG_INT) ? tag.getInt("color") : 8606770;
    }
}
