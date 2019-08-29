package ovh.corail.travel_bag.compatibility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ovh.corail.tombstone.api.TombstoneAPIProps;
import ovh.corail.tombstone.api.capability.ITBCapability;
import ovh.corail.tombstone.api.capability.Perk;
import ovh.corail.travel_bag.config.TravelBagConfig;
import ovh.corail.travel_bag.item.TravelBagSoulItem;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class CompatibilityTombstone {
    public static CompatibilityTombstone INSTANCE = new CompatibilityTombstone();

    private static final ResourceLocation TRAVEL_BAG = new ResourceLocation(MOD_ID, "textures/items/travel_bag.png");
    @CapabilityInject(ITBCapability.class)
    public static final Capability<ITBCapability> PLAYER_CAPABILITY = null;
    public static Perk travel_bag_perk = null;

    public Item getTravelBag() {
        return new TravelBagSoulItem();
    }

    public LazyOptional<ITBCapability> getPlayerCapability(PlayerEntity player) {
        return PLAYER_CAPABILITY != null ? player.getCapability(PLAYER_CAPABILITY, null) : LazyOptional.empty();
    }

    public boolean hasGluttony(PlayerEntity player) {
        return travel_bag_perk != null && getPlayerCapability(player).map(cap -> cap.getPerkLevel(player, travel_bag_perk) > 0).orElse(false);
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
                return TravelBagConfig.general.isGluttonySlotDisabled();
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
