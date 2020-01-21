package ovh.corail.travel_bag.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import ovh.corail.travel_bag.ModTravelBag;
import ovh.corail.travel_bag.compatibility.CompatibilityCurios;
import ovh.corail.travel_bag.compatibility.SupportMods;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static KeyBinding keybindCuriosBag1, keybindCuriosBag2;

    static {
        if (SupportMods.CURIOS.isLoaded()) {
            ClientRegistry.registerKeyBinding(keybindCuriosBag1 = new KeyBinding(MOD_ID + ".keybind.curios_bag_0", KeyConflictContext.IN_GAME, InputMappings.INPUT_INVALID, ModTravelBag.MOD_NAME));
            ClientRegistry.registerKeyBinding(keybindCuriosBag2 = new KeyBinding(MOD_ID + ".keybind.curios_bag_1", KeyConflictContext.IN_GAME, InputMappings.INPUT_INVALID, ModTravelBag.MOD_NAME));
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {
        private static int COOLDOWN = 0;

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        public static void onPlayerTickEvent(TickEvent.ClientTickEvent event) {
            if (event.side == LogicalSide.SERVER || event.phase != TickEvent.Phase.START) {
                return;
            }
            if (COOLDOWN > 0) {
                COOLDOWN--;
                return;
            }
            boolean isFirst;
            if (SupportMods.CURIOS.isLoaded() && ((isFirst = keybindCuriosBag1.isPressed()) || keybindCuriosBag2.isPressed())) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && mc.currentScreen == null) {
                    CompatibilityCurios.INSTANCE.openBag(mc.player, isFirst);
                    COOLDOWN = 10;
                }
            }
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void onStitchTextures(TextureStitchEvent.Pre event) {
            if (SupportMods.CURIOS.isLoaded() && event.getMap().func_229223_g_().equals(PlayerContainer.field_226615_c_)) {
                event.addSprite(CompatibilityCurios.INSTANCE.EMPTY_BAG);
            }
        }
    }
}
