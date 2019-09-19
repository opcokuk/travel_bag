package ovh.corail.travel_bag.registry;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class ModTags {
    public static class Items {
        public static final Tag<Item> TRAVEL_BAG_DENIED_ITEMS = tag("travel_bag_denied_items");

        private static Tag<Item> tag(String name) {
            return new ItemTags.Wrapper(new ResourceLocation(MOD_ID, name));
        }
    }
}
