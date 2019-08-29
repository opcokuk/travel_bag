package ovh.corail.travel_bag.helper;

import net.minecraft.item.ItemStack;

public class Helper {
    public static boolean existClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean compareTags(ItemStack stack1, ItemStack stack2) {
        boolean hasTag1 = stack1.hasTag();
        return hasTag1 == stack2.hasTag() && (!hasTag1 || stack1.getTag().equals(stack2.getTag())) && stack1.areCapsCompatible(stack2);
    }
}
