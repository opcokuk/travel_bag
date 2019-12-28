package ovh.corail.travel_bag.compatibility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ovh.corail.tombstone.api.TombstoneAPIProps;
import ovh.corail.tombstone.api.capability.ITBCapability;
import ovh.corail.tombstone.api.capability.Perk;
import ovh.corail.tombstone.api.magic.ISoulConsumer;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.registry.ModItems;

import javax.annotation.Nullable;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class CompatibilityTombstone {
    public static CompatibilityTombstone INSTANCE = new CompatibilityTombstone();

    private static final ResourceLocation TRAVEL_BAG = new ResourceLocation(MOD_ID, "textures/items/travel_bag.png");
    @CapabilityInject(ISoulConsumer.class)
    public static final Capability<ISoulConsumer> SOUL_CONSUMER_CAPABILITY = null;
    @CapabilityInject(ITBCapability.class)
    public static final Capability<ITBCapability> PLAYER_CAPABILITY = null;
    public static Perk travel_bag_perk = null;

    public <T> LazyOptional<T>  getTravelBagCapability(Capability<T> cap) {
        return SOUL_CONSUMER_CAPABILITY != null && cap == SOUL_CONSUMER_CAPABILITY ? LazyOptional.of(TravelBagTombstoneCap::new).cast() : LazyOptional.empty();
    }

    public boolean isEnchantedBag(ItemStack stack) {
        return stack.getItem() == ModItems.TRAVEL_BAG && SOUL_CONSUMER_CAPABILITY != null ? getTravelBagCapability(SOUL_CONSUMER_CAPABILITY).map(cap -> cap.isEnchanted(stack)).orElse(false) : false;
    }

    public class TravelBagTombstoneCap implements ISoulConsumer {
        @Override
        public boolean isEnchanted(ItemStack stack) {
            CompoundNBT tag = stack.getTag();
            return tag != null && stack.getTag().getBoolean("has_soul");
        }

        @Override
        public boolean setEnchant(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
            if (TravelBagConfig.general.disableEnchantedTravelBag.get()) {
                return false;
            }
            stack.getOrCreateTag().putBoolean("has_soul", true);
            return true;
        }

        @Override
        public ITextComponent getEnchantSuccessMessage(PlayerEntity player) {
            return new TranslationTextComponent(MOD_ID + ".message.enchant_success");
        }

        @Override
        public ITextComponent getEnchantFailedMessage(PlayerEntity player) {
            return new TranslationTextComponent(MOD_ID + ".message.enchant_failed");
        }
    }

    public LazyOptional<ITBCapability> getPlayerCapability(PlayerEntity player) {
        return PLAYER_CAPABILITY != null ? player.getCapability(PLAYER_CAPABILITY, null) : LazyOptional.empty();
    }

    public boolean hasPerkLevel(@Nullable PlayerEntity player, int level) {
        return travel_bag_perk != null && player != null && getPlayerCapability(player).map(cap -> cap.getPerkLevel(player, travel_bag_perk) >= level).orElse(false);
    }

    public void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Perk.class, this::registerPerks);
    }

    @SubscribeEvent
    public void registerPerks(final RegistryEvent.Register<Perk> event) {
        travel_bag_perk = new Perk("travel_bag", TRAVEL_BAG) {
            @Override
            public int getLevelMax() {
                return 1;
            }

            @Override
            public boolean isDisabled() {
                return TravelBagConfig.general.disableGluttonySlot.get();
            }

            @Override
            public String getTooltip(int level, int actualLevel, int levelWithBonus) {
                if (level == 1) {
                    return "tombstone.perk.gluttony";
                }
                return "";
            }

            @Override
            public int getCost(int level) {
                return 1;
            }

            @Override
            public boolean isEncrypted() {
                return true;
            }
        };
        travel_bag_perk.setRegistryName(TombstoneAPIProps.OWNER, "travel_bag");
        event.getRegistry().register(travel_bag_perk);
    }
}
