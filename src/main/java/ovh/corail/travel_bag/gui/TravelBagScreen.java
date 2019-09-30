package ovh.corail.travel_bag.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.travel_bag.ModTravelBag;
import ovh.corail.travel_bag.compatibility.CompatibilityTombstone;
import ovh.corail.travel_bag.compatibility.SupportMods;
import ovh.corail.travel_bag.helper.Helper;
import ovh.corail.travel_bag.inventory.TravelBagContainer;
import ovh.corail.travel_bag.inventory.slot.TravelBagSlot;
import ovh.corail.travel_bag.network.TakeAllPacket;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class TravelBagScreen extends ContainerScreen<TravelBagContainer> {
    private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ResourceLocation ENCHANTED_INVENTORY_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/enchanted_travel_bag.png");
    private static final ResourceLocation ARROW = new ResourceLocation(MOD_ID, "textures/gui/arrow.png");
    private final boolean isEnchanted;
    private boolean isInit = true;

    public TravelBagScreen(TravelBagContainer travelBagContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(travelBagContainer, playerInventory, title);
        CompoundNBT tag = Helper.getContainerBagStack(playerInventory.player).getTag();
        this.isEnchanted = SupportMods.TOMBSTONE.isLoaded() && tag != null && tag.getBoolean("has_soul");
        this.xSize = this.isEnchanted ? 246 : 176;
        this.ySize = 220;
        this.passEvents = false;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        addButton(new ImageButton(this.guiLeft + this.xSize - 20, this.guiTop + 126, 12, 12, 0, 0, 12, ARROW, 12, 12, pressable -> ModTravelBag.HANDLER.sendToServer(new TakeAllPacket())));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String titleString = this.title.getFormattedText();
        float startPos = (this.xSize - this.font.getStringWidth(titleString)) / 2f;
        this.font.drawString(titleString, startPos, 8f, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        getMinecraft().getTextureManager().bindTexture(this.isEnchanted ? ENCHANTED_INVENTORY_BACKGROUND : INVENTORY_BACKGROUND);
        blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if (this.isEnchanted && SupportMods.TOMBSTONE.isLoaded() && CompatibilityTombstone.INSTANCE.hasGluttony(getMinecraft().player)) {
            int x = this.guiLeft + 11;
            int y = this.guiTop + 140;
            String langString = I18n.format("tombstone.perk.gluttony");
            this.font.drawString(langString, this.guiLeft + 5, y - 10, 0xff505050);
            fill(x, y, x + 20, y + 20, 0xffffffff);
            fill(x + 1, y + 1, x + 19, y + 19, 0xff000000);
            fillGradient(x + 2, y + 2, x + 18, y + 18, 0xff505050, 0xffc0c0c0);
        }
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        getMinecraft().getTextureManager().bindTexture(ENCHANTED_INVENTORY_BACKGROUND);
        this.container.inventorySlots.stream().filter(slot -> slot instanceof TravelBagSlot).forEach(slot -> {
            TravelBagSlot currentSlot = (TravelBagSlot) slot;
            if (this.isInit) {
                currentSlot.timeInUse = 0;
            } else {
                if (currentSlot.timeInUse > 0) {
                    blit(this.guiLeft + slot.xPos - 1, this.guiTop + slot.yPos - 1, 0, 238, 18, 18);
                    currentSlot.timeInUse--;
                }
            }
        });
        this.isInit = false;
    }
}
