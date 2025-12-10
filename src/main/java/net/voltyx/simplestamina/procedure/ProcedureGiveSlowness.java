package net.voltyx.simplestamina.procedure;

import net.voltyx.simplestamina.ElementsSimplestaminaMod;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsSimplestaminaMod.ModElement.Tag
public class ProcedureGiveSlowness extends ElementsSimplestaminaMod.ModElement {
    public ProcedureGiveSlowness(ElementsSimplestaminaMod instance) {
        super(instance, 4);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        // check if entity exists
        if (dependencies.get("entity") == null) {
            System.err.println("failed to load dependency entity for procedure giveslowness!");
            return;
        }

        Entity entity = (Entity) dependencies.get("entity");

        // if stamina is zero apply strong slowness effect
        if (entity.getEntityData().getDouble("stamina") == 0) {
            if (entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).addPotionEffect(
                        new PotionEffect(MobEffects.SLOWNESS, 80, 2, false, false)
                );
            }
        }

        // if stamina is between 1 and 6 apply weaker slowness
        double stamina = entity.getEntityData().getDouble("stamina");
        if (stamina != 0 && stamina <= 6) {
            if (entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).addPotionEffect(
                        new PotionEffect(MobEffects.SLOWNESS, 5, 1, false, false)
                );
            }
        }

        // clamp stamina to food level if greater than food
        if (entity.getEntityData().getDouble("stamina") > 
                ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).getFoodStats().getFoodLevel() : 0)) {
            entity.getEntityData().setDouble("stamina",
                    ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).getFoodStats().getFoodLevel() : 0));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // execute procedure at the end of each player tick
        if (event.phase == TickEvent.Phase.END) {
            Entity entity = event.player;
            World world = entity.world;
            int i = (int) entity.posX;
            int j = (int) entity.posY;
            int k = (int) entity.posZ;

            java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
            dependencies.put("x", i);
            dependencies.put("y", j);
            dependencies.put("z", k);
            dependencies.put("world", world);
            dependencies.put("entity", entity);
            dependencies.put("event", event);

            executeProcedure(dependencies);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // register this class to listen to player tick events
        MinecraftForge.EVENT_BUS.register(this);
    }
}
