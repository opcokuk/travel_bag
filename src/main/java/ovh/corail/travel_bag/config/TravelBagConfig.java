package ovh.corail.travel_bag.config;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import ovh.corail.travel_bag.ModTravelBag;
import ovh.corail.travel_bag.network.UpdateClient;
import ovh.corail.travel_bag.registry.ModItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TravelBagConfig {
    public static class General {
        public final ForgeConfigSpec.ConfigValue<Boolean> disableEnchantedTravelBag;
        public final ForgeConfigSpec.ConfigValue<Boolean> disableGluttonySlot;
        boolean serverDisableGluttonySlot = false;

        public boolean isGluttonySlotDisabled() {
            return this.serverDisableGluttonySlot;
        }

        public final ForgeConfigSpec.ConfigValue<Integer> gluttonySlotSpeed;
        public final ForgeConfigSpec.ConfigValue<List<String>> blacklistItems;

        General(ForgeConfigSpec.Builder builder) {
            builder.comment("Miscellaneous options").push("general");

            disableEnchantedTravelBag = builder
                    .comment("Disable to enchant the travel bag [default:false]")
                    .translation(getTranslation("disable_enchanted_travel_bag"))
                    .define("disable_enchanted_travel_bag", false);
            disableGluttonySlot = builder
                    .comment("Disable the Gluttony slot [default:false]")
                    .translation(getTranslation("disable_gluttony_slot"))
                    .define("disable_gluttony_slot", false);
            gluttonySlotSpeed = builder
                    .comment("Speed of extraction in ticks of the Gluttony slot [20..1000|default:20]")
                    .translation(getTranslation("gluttony_slot_speed"))
                    .defineInRange("gluttony_slot_speed", 20, 20, 1000);
            blacklistItems = builder
                    .comment("Items that can not be transferred in the Travel Bag [namespace|namespace:item_name")
                    .translation(getTranslation("blacklist_items"))
                    .define("blacklist_items", new ArrayList<>());

            builder.pop();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLogued(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().world.isRemote) {
            ModTravelBag.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new UpdateClient(general.disableGluttonySlot.get()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            general.serverDisableGluttonySlot = general.disableGluttonySlot.get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleClientPacket(final UpdateClient message) {
        general.serverDisableGluttonySlot = message.disableGluttonySlot;
    }

    private static String getTranslation(String name) {
        return MOD_ID + ".config." + name;
    }

    public static final ForgeConfigSpec GENERAL_SPEC;
    public static final TravelBagConfig.General general;

    static {
        final Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
        GENERAL_SPEC = specPair.getRight();
        general = specPair.getLeft();
    }

    public static boolean isAllowedInBag(ItemStack stack) {
        return stack.getItem() != ModItems.TRAVEL_BAG && !containRL(general.blacklistItems.get(), stack.getItem().getRegistryName());
    }

    private static boolean containRL(List<String> listRL, @Nullable ResourceLocation rl) {
        return rl != null && listRL.stream().anyMatch(p -> p.contains(":") ? rl.toString().equals(p) : rl.getNamespace().equals(p));
    }
}
