package ovh.corail.travel_bag.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.tombstone.api.magic.ISoulConsumer;
import ovh.corail.travel_bag.config.TravelBagConfig;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class TravelBagSoulItem extends TravelBagItem implements ISoulConsumer {
    public TravelBagSoulItem() {
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ITextComponent displayName = super.getDisplayName(stack);
        return isEnchanted(stack) ? new TranslationTextComponent(MOD_ID + ".enchanted_item", displayName) : displayName;
    }

    @Override
    public boolean isEnchanted(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && stack.getTag().getBoolean("has_soul");
    }

    @Override
    public boolean isEnchantedBag(ItemStack stack) {
        return isEnchanted(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return isEnchanted(stack);
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
