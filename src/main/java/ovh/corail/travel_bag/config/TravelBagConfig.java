package ovh.corail.travel_bag.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import ovh.corail.travel_bag.network.UpdateConfigPacket;

import static ovh.corail.travel_bag.ModTravelBag.*;

public class TravelBagConfig {

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Boolean> disableEnchantedTravelBag;
        public final ForgeConfigSpec.ConfigValue<Boolean> disableGluttonySlot;
        public final ForgeConfigSpec.ConfigValue<Integer> gluttonySlotSpeed;

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

            builder.pop();
        }
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

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ConfigEvent {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onReloadConfig(ModConfig.Reloading event) {
            if (event.getConfig().getModId().equals(MOD_ID) && event.getConfig().getType() == ModConfig.Type.SERVER) {
                // sync the config on all clients on dedicated server without the need to relog
                PROXY.markConfigDirty();
            }
        }
    }

    public static UpdateConfigPacket getUpdatePacket() {
        return new UpdateConfigPacket(general.disableEnchantedTravelBag.get(), general.disableGluttonySlot.get(), general.gluttonySlotSpeed.get());
    }

    public static void updateConfig(UpdateConfigPacket packet) {
        // directly set the ConfigValues
        general.disableEnchantedTravelBag.set(packet.disableEnchantedTravelBag);
        general.disableGluttonySlot.set(packet.disableGluttonySlot);
        general.gluttonySlotSpeed.set(packet.gluttonySlotSpeed);
    }
}
