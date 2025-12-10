package net.voltyx.simplestamina.procedure;

import net.voltyx.simplestamina.potion.PotionEnergyBoost;
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
import java.util.Collection;

@ElementsSimplestaminaMod.ModElement.Tag
public class ProcedureGiveSlownessHunger extends ElementsSimplestaminaMod.ModElement {
	public ProcedureGiveSlownessHunger(ElementsSimplestaminaMod instance) {
		super(instance, 10);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure GiveSlownessHunger!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");

		// Получаем стамину
		double stamina = entity.getEntityData().getDouble("stamina");

		// Проверяем стамину с порогом, чтобы избежать проблем с double
		if (stamina <= 0.01) {

			// Добавляем эффекты
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 2, false, false));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 80, 1, false, false));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, 1, false, false));

			// Таймер на 5 секунд (100 тиков)
			int timer = entity.getEntityData().getInteger("stamina_food_timer");

			// Если таймер 0 — снимаем еду сразу
			if (timer == 0 && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				int food = player.getFoodStats().getFoodLevel();
				if (food >= 2) {
					player.getFoodStats().setFoodLevel(food - 2);
				}
			}

			// Увеличиваем таймер
			timer++;
			if (timer >= 100) timer = 0;
			entity.getEntityData().setInteger("stamina_food_timer", timer);

		} else {
			// Сброс таймера, если стамина > 0
			entity.getEntityData().setInteger("stamina_food_timer", 0);
		}

		// Ограничиваем стамину уровнем еды, если нет эффекта EnergyBoost
		boolean hasEnergyBoost = false;
		if (entity instanceof EntityLivingBase) {
			for (PotionEffect effect : ((EntityLivingBase) entity).getActivePotionEffects()) {
				if (effect.getPotion() == PotionEnergyBoost.potion) {
					hasEnergyBoost = true;
					break;
				}
			}
		}

		if (!hasEnergyBoost) {
			int food = (entity instanceof EntityPlayer) ? ((EntityPlayer) entity).getFoodStats().getFoodLevel() : 0;
			if (stamina > food) {
				entity.getEntityData().setDouble("stamina", food);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Entity entity = event.player;
			World world = entity.world;
			java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
			dependencies.put("x", (int) entity.posX);
			dependencies.put("y", (int) entity.posY);
			dependencies.put("z", (int) entity.posZ);
			dependencies.put("world", world);
			dependencies.put("entity", entity);
			dependencies.put("event", event);
			this.executeProcedure(dependencies);
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
