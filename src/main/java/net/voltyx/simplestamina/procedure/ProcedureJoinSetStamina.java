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
public class ProcedureJoinSetStamina extends ElementsSimplestaminaMod.ModElement {
    public ProcedureJoinSetStamina(ElementsSimplestaminaMod instance) {
        super(instance, 6);
    }

    /**
     * Sets the stamina of the given entity to full (20).
     *
     * @param dependencies A map containing the entity whose stamina will be set.
     */
    public static void executeProcedure(Map<String, Object> dependencies) {
        // Ensure that the entity dependency is present
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure JoinSetStamina!");
            return;
        }

        // Get the entity from dependencies and set stamina to full
        Entity entity = (Entity) dependencies.get("entity");
        entity.getEntityData().setDouble("stamina", 20);
    }

    /**
     * Event handler triggered whenever an entity joins the world.
     * This sets the entity's stamina to full upon joining.
     *
     * @param event The EntityJoinWorldEvent for the spawned entity
     */
    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        World world = event.getWorld();

        // Capture entity coordinates
        int i = (int) entity.posX;
        int j = (int) entity.posY;
        int k = (int) entity.posZ;

        // Prepare dependencies map for procedure execution
        java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
        dependencies.put("x", i);
        dependencies.put("y", j);
        dependencies.put("z", k);
        dependencies.put("world", world);
        dependencies.put("entity", entity);
        dependencies.put("event", event);

        // Execute the procedure to set full stamina
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
