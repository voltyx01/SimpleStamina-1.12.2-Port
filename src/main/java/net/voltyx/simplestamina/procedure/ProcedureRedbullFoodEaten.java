package net.voltyx.simplestamina.procedure;

import net.voltyx.simplestamina.potion.PotionEnergyBoost;
import net.voltyx.simplestamina.ElementsSimplestaminaMod;

import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsSimplestaminaMod.ModElement.Tag
public class ProcedureRedbullFoodEaten extends ElementsSimplestaminaMod.ModElement {
    public ProcedureRedbullFoodEaten(ElementsSimplestaminaMod instance) {
        super(instance, 8);
    }

    /**
     * Grants the EnergyBoost potion effect to the entity when the Redbull food item is eaten.
     *
     * @param dependencies A map containing the entity that ate the food.
     */
    public static void executeProcedure(Map<String, Object> dependencies) {
        // Ensure the entity dependency is provided
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure RedbullFoodEaten!");
            return;
        }

        // Get the entity from dependencies
        Entity entity = (Entity) dependencies.get("entity");

        // Only living entities can receive potion effects
        if (entity instanceof EntityLivingBase) {
            // Apply EnergyBoost potion effect:
            // Duration: 1200 ticks (60 seconds)
            // Amplifier: 0 (first level)
            // Ambient: false (not a subtle effect)
            // Show particles: false
            ((EntityLivingBase) entity).addPotionEffect(
                new PotionEffect(PotionEnergyBoost.potion, 1200, 0, false, false)
            );
        }
    }
}
