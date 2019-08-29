package ovh.corail.travel_bag.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Comparator;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class ModTabs {
    public static final ItemGroup mainTab = new CreativeTabsPillar("mainTab");

    public static class CreativeTabsPillar extends ItemGroup {
        public CreativeTabsPillar(String label) {
            super(label);
            setBackgroundImageName("item_search.png");
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.TRAVEL_BAG);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fill(NonNullList<ItemStack> list) {
            super.fill(list);
            list.sort(Comparator.comparingInt(c -> Item.getIdFromItem(c.getItem())));
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

        @OnlyIn(Dist.CLIENT)
        public String getTranslationKey() {
            return MOD_ID + ".itemGroup." + getTabLabel();
        }
    }
}
