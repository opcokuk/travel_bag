package ovh.corail.travel_bag.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

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
}
