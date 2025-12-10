package net.voltyx.simplestamina.procedure;

import net.voltyx.simplestamina.ElementsSimplestaminaMod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsSimplestaminaMod.ModElement.Tag
public class ProcedureRespawnSetStamina extends ElementsSimplestaminaMod.ModElement {
    public ProcedureRespawnSetStamina(ElementsSimplestaminaMod instance) {
        super(instance, 5);
    }

    /**
     * Sets the stamina of the given entity to full (20).
     * 
     * @param dependencies A map containing the entity whose stamina will be set.
     */
    public static void executeProcedure(Map<String, Object> dependencies) {
        // Ensure the entity dependency is provided
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure RespawnSetStamina!");
            return;
        }

        // Get the entity from dependencies and set its stamina to full
        Entity entity = (Entity) dependencies.get("entity");
        entity.getEntityData().setDouble("stamina", 20);
    }

    /**
     * Event handler that triggers whenever an entity joins the world.
     * This includes player respawn.
     */
    @SubscribeEvent
    public void onEntitySpawned(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        // Capture entity coordinates
        int i = (int) entity.posX;
        int j = (int) entity.posY;
        int k = (int) entity.posZ;

        // Get the world the entity spawned in
        World world = event.getWorld();

        // Prepare dependencies map for procedure execution
        java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
        dependencies.put("x", i);
        dependencies.put("y", j);
        dependencies.put("z", k);
        dependencies.put("world", world);
        dependencies.put("entity", entity);
        dependencies.put("event", event);

        // Execute procedure to set full stamina
        this.executeProcedure(dependencies);
    }

    /**
     * Registers this class to the Forge event bus during pre-initialization.
     */
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
