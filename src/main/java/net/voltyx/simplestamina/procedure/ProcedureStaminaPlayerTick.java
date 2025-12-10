package net.voltyx.simplestamina.procedure;

import net.voltyx.simplestamina.ElementsSimplestaminaMod;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsSimplestaminaMod.ModElement.Tag
public class ProcedureStaminaPlayerTick extends ElementsSimplestaminaMod.ModElement {
    public ProcedureStaminaPlayerTick(ElementsSimplestaminaMod instance) {
        super(instance, 2);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        // check if entity exists in dependencies
        if (dependencies.get("entity") == null) {
            System.err.println("failed to load dependency entity for procedure staminaplayertick!");
            return;
        }

        Entity entity = (Entity) dependencies.get("entity");

        // if player is in creative or spectator mode set stamina to 20 and reset tick counter
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isCreative() || player.isSpectator()) {
                player.getEntityData().setDouble("stamina", 20);
                player.getEntityData().setInteger("stamina_tick_counter", 0);
                return; // skip rest of logic for creative and spectator
            }
        }

        // get current stamina and tick counter
        double stamina = entity.getEntityData().getDouble("stamina");
        int tickCounter = entity.getEntityData().getInteger("stamina_tick_counter");

        // increase tick counter each tick
        tickCounter++;

        // every 10 ticks update stamina
        if (tickCounter >= 10) {
            tickCounter = 0;

            // if sprinting decrease stamina by 1 if greater than 0
            if (entity.isSprinting()) {
                if (stamina > 0) {
                    stamina -= 1;
                } else {
                    stamina = 0;
                }
            } 
            // if not sprinting increase stamina by 1 up to 20
            else {
                if (stamina < 20) {
                    stamina += 1;
                }
            }
        }

        // save updated stamina and tick counter
        entity.getEntityData().setDouble("stamina", stamina);
        entity.getEntityData().setInteger("stamina_tick_counter", tickCounter);
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

            // prepare dependencies map
            java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
            dependencies.put("x", i);
            dependencies.put("y", j);
            dependencies.put("z", k);
            dependencies.put("world", world);
            dependencies.put("entity", entity);
            dependencies.put("event", event);

            // call procedure
            this.executeProcedure(dependencies);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // register this class to listen for player tick events
        MinecraftForge.EVENT_BUS.register(this);
    }
}
