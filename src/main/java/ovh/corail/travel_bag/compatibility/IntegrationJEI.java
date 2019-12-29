package ovh.corail.travel_bag.compatibility;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import ovh.corail.travel_bag.ModTravelBag;
import ovh.corail.travel_bag.registry.ModItems;

import javax.annotation.Nonnull;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModTravelBag.MOD_ID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.TRAVEL_BAG, new TravelBagInterpreter());
    }

    private static class TravelBagInterpreter implements ISubtypeInterpreter {
        @Override
        public String apply(ItemStack stack) {
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.contains("color", Constants.NBT.TAG_INT)) {
                return String.valueOf(tag.getInt("color"));
            }
            return String.valueOf(8606770);
        }
    }
}
