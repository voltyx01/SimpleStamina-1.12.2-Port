package net.voltyx.simplestamina.procedure;

import net.voltyx.simplestamina.ElementsSimplestaminaMod;
import net.voltyx.simplestamina.potion.PotionEnergyBoost;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import java.util.Map;

@ElementsSimplestaminaMod.ModElement.Tag
public class ProcedureStaminaPlayerTick extends ElementsSimplestaminaMod.ModElement {
    public ProcedureStaminaPlayerTick(ElementsSimplestaminaMod instance) {
        super(instance, 2);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        // Ensure that the entity dependency is provided
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure staminaplayertick!");
            return;
        }

        Entity entity = (Entity) dependencies.get("entity");

        // For Creative & Spectator mode, stamina is always full (20)
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isCreative() || player.isSpectator()) {
                player.getEntityData().setDouble("stamina", 20);
                player.getEntityData().setInteger("stamina_tick_counter", 0);
                return;
            }
        }

        // Load current stamina and tick counter values from the entity's data
        double stamina = entity.getEntityData().getDouble("stamina");
        int tickCounter = entity.getEntityData().getInteger("stamina_tick_counter");

        // Increment the tick counter each tick
        tickCounter++;

        // If the player is sprinting, decrease stamina every 10 ticks
        if (entity.isSprinting()) {
            if (tickCounter >= 10) {
                tickCounter = 0;
                if (stamina > 0) stamina -= 1;
                else stamina = 0;
            }
        }
        // If the player is not sprinting, regenerate stamina
        else {
            // Check if the player has the EnergyBoost potion effect
            boolean hasEnergyBoost = false;
            if (entity instanceof EntityLivingBase) {
                for (PotionEffect effect : ((EntityLivingBase) entity).getActivePotionEffects()) {
                    if (effect.getPotion() == PotionEnergyBoost.potion) {
                        hasEnergyBoost = true;
                        break;
                    }
                }
            }

            // Determine how many ticks to wait before regenerating stamina
            int regenTicks = hasEnergyBoost ? 4 : 6;

            // Regenerate stamina if the tick counter reaches the threshold
            if (tickCounter >= regenTicks) {
                tickCounter = 0;
                if (stamina < 20)
                    stamina += 1;
            }
        }

        // Save updated stamina and tick counter back to the entity's data
        entity.getEntityData().setDouble("stamina", stamina);
        entity.getEntityData().setInteger("stamina_tick_counter", tickCounter);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Only execute on the END phase of the player tick
        if (event.phase == TickEvent.Phase.END) {
            Entity entity = event.player;
            World world = entity.world;

            // Prepare dependencies map for procedure execution
            java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
            dependencies.put("entity", entity);
            dependencies.put("world", world);
            dependencies.put("x", (int) entity.posX);
            dependencies.put("y", (int) entity.posY);
            dependencies.put("z", (int) entity.posZ);
            dependencies.put("event", event);

            // Execute the stamina procedure
            this.executeProcedure(dependencies);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // Register this class to the Forge event bus to receive player tick events
        MinecraftForge.EVENT_BUS.register(this);
    }
}
