package ovh.corail.travel_bag.compatibility;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.travel_bag.gui.TravelBagScreen;
import ovh.corail.travel_bag.inventory.TravelBagContainer;
import vazkii.quark.api.IQuarkButtonIgnored;

public class CompatibilityQuark {
    @OnlyIn(Dist.CLIENT)
    public static class ButtonIgnoredScreen  extends TravelBagScreen implements IQuarkButtonIgnored {
        public ButtonIgnoredScreen(TravelBagContainer travelBagContainer, PlayerInventory playerInventory, ITextComponent title) {
            super(travelBagContainer, playerInventory, title);
        }
    }
}
