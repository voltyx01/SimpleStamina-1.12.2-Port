package net.voltyx.simplestamina.gui.overlay;

import net.voltyx.simplestamina.ElementsSimplestaminaMod;
import net.voltyx.simplestamina.ModConfig;
import net.voltyx.simplestamina.potion.PotionEnergyBoost;

import net.minecraft.world.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@ElementsSimplestaminaMod.ModElement.Tag
public class OverlayStamina extends ElementsSimplestaminaMod.ModElement {
    public OverlayStamina(ElementsSimplestaminaMod instance) {
        super(instance, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent event) {
        // Register the GUI render event handler to the Forge event bus
        MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
    }

    @SideOnly(Side.CLIENT)
    public static class GUIRenderEventClass {

        // Resource locations for different stamina bar textures
        private static final ResourceLocation BASE_LAYER = new ResourceLocation("simplestamina:textures/sloi_2.png");
        private static final ResourceLocation FULL_LAYER = new ResourceLocation("simplestamina:textures/sloi_1.png");
        private static final ResourceLocation HALF_LAYER = new ResourceLocation("simplestamina:textures/sloi_3.png");
        private static final ResourceLocation ENERGY_LAYER = new ResourceLocation("simplestamina:textures/sloi_5.png");

        @SubscribeEvent(priority = EventPriority.NORMAL)
        @SideOnly(Side.CLIENT)
        public void eventHandler(RenderGameOverlayEvent.Post event) {
            // Only render if the overlay type is HELMET and the event is not cancelable
            if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.HELMET) return;

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            GameType gamemode = mc.playerController.getCurrentGameType();

            // Only display stamina bar in Survival or Adventure mode
            if (gamemode != GameType.SURVIVAL && gamemode != GameType.ADVENTURE) return;

            // Get the player's current stamina value stored in their entity data
            double stamina = player.getEntityData().getDouble("stamina");

            // Bar dimensions and configuration
            int barWidth = 9;        // Width of each stamina segment
            int overlap = 1;         // How much segments overlap visually
            int totalBars = 10;      // Total number of stamina segments

            // Get the scaled screen resolution
            ScaledResolution scaled = new ScaledResolution(mc);
            int screenWidth = scaled.getScaledWidth();
            int screenHeight = scaled.getScaledHeight();

            // Calculate base coordinates for rendering the stamina bar
            int hotbarY = screenHeight - 10; // Hotbar Y position
            int baseY = hotbarY - ModConfig.overlayBaseY - 39; // Stamina bar Y
            int baseX = screenWidth / 2 + ModConfig.overlayBaseX - 82 + 164; // Stamina bar X

            // Setup OpenGL state for rendering the stamina bar
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            );
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableAlpha();

            // Render the base layer of the stamina bar (empty segments)
            mc.renderEngine.bindTexture(BASE_LAYER);
            for (int i = 0; i < totalBars; i++) {
                int x = baseX - i * (barWidth - overlap);
                mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
            }

            // Check if player has the energy boost potion effect
            boolean hasEnergyBoost = false;
            if (player instanceof EntityLivingBase) {
                for (PotionEffect effect : ((EntityLivingBase) player).getActivePotionEffects()) {
                    if (effect.getPotion() == PotionEnergyBoost.potion) {
                        hasEnergyBoost = true;
                        break;
                    }
                }
            }

            // Render the energy boost layer if the player has the potion effect
            if (hasEnergyBoost) {
                mc.renderEngine.bindTexture(ENERGY_LAYER);
                for (int i = 0; i < totalBars; i++) {
                    int x = baseX - i * (barWidth - overlap);
                    mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
                }
            }

            // Render full stamina segments based on current stamina value
            mc.renderEngine.bindTexture(FULL_LAYER);
            for (int i = 0; i < totalBars; i++) {
                if (stamina >= (i + 1) * 2) {
                    int x = baseX - i * (barWidth - overlap);
                    mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
                }
            }

            // Render half stamina segments if stamina is between full segments
            mc.renderEngine.bindTexture(HALF_LAYER);
            for (int i = 0; i < totalBars; i++) {
                if (stamina >= (i * 2 + 1)) {
                    int x = baseX - i * (barWidth - overlap);
                    mc.ingameGUI.drawModalRectWithCustomSizedTexture(x, baseY, 0, 0, barWidth, barWidth, barWidth, barWidth);
                }
            }

            // Restore OpenGL state after rendering
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
