package net.voltyx.simplestamina.gui.overlay;

import net.voltyx.simplestamina.ElementsSimplestaminaMod;
import net.voltyx.simplestamina.ModConfig;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;

@ElementsSimplestaminaMod.ModElement.Tag
public class OverlayStamina extends ElementsSimplestaminaMod.ModElement {
    public OverlayStamina(ElementsSimplestaminaMod instance) {
        super(instance, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent event) {
        // register event handler for rendering the overlay
        MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
    }

    @SideOnly(Side.CLIENT)
    public static class GUIRenderEventClass {

        // resource locations for stamina overlay textures
        private static final ResourceLocation BASE_LAYER = new ResourceLocation("simplestamina:textures/sloi_2.png");
        private static final ResourceLocation FULL_LAYER = new ResourceLocation("simplestamina:textures/sloi_1.png");
        private static final ResourceLocation HALF_LAYER = new ResourceLocation("simplestamina:textures/sloi_3.png");

        @SubscribeEvent(priority = EventPriority.NORMAL)
        @SideOnly(Side.CLIENT)
        public void eventHandler(RenderGameOverlayEvent.Post event) {
            // only render for non cancelable events and the correct overlay type
            if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.HELMET) return;

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            GameType gamemode = mc.playerController.getCurrentGameType();

            // only render in survival or adventure mode
            if (gamemode != GameType.SURVIVAL && gamemode != GameType.ADVENTURE) return;

            // get current stamina from player data
            double stamina = player.getEntityData().getDouble("stamina");

            // width of one stamina segment
            int barWidth = 9;
            // amount of overlap between adjacent segments
            int overlap = 1;
            // total number of stamina segments
            int totalBars = 10;

            // get scaled screen dimensions based on gui scale
            ScaledResolution scaled = new ScaledResolution(mc);
            int screenWidth = scaled.getScaledWidth();
            int screenHeight = scaled.getScaledHeight();

// get hotbar position
int hotbarY = screenHeight - 10;

// calculate overlay position relative to hotbar
int baseY = hotbarY - ModConfig.overlayBaseY - 39;
int baseX = screenWidth / 2 + ModConfig.overlayBaseX - 82 + 164;

// rendering setup
GlStateManager.disableDepth();
GlStateManager.depthMask(false);
GlStateManager.enableBlend();
GlStateManager.tryBlendFuncSeparate(
        GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
);
GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
GlStateManager.disableAlpha();

// draw base layer
mc.renderEngine.bindTexture(BASE_LAYER);
for (int i = 0; i < totalBars; i++) {
    int x = baseX - i * (barWidth - overlap);
    mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
}

// draw full layer
mc.renderEngine.bindTexture(FULL_LAYER);
for (int i = 0; i < totalBars; i++) {
    if (stamina >= (i + 1) * 2) {
        int x = baseX - i * (barWidth - overlap);
        mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
    }
}

// draw half layer
mc.renderEngine.bindTexture(HALF_LAYER);
for (int i = 0; i < totalBars; i++) {
    if (stamina >= (i * 2 + 1)) {
        int x = baseX - i * (barWidth - overlap);
        mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
    }
}

// restore state
GlStateManager.depthMask(true);
GlStateManager.enableDepth();
GlStateManager.enableAlpha();
GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
