package net.voltyx.simplestamina;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ModConfig {

    public static Configuration config;

    // Configurable overlay parameters
    public static int overlayBaseX = 0;
    public static int overlayBaseY = 0;

    public static void init(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "simplestamina.cfg");
        config = new Configuration(configFile);
        syncConfig();

        // Register listener for changes through the GUI
        MinecraftForge.EVENT_BUS.register(new ModConfig());
    }

    public static void syncConfig() {
        overlayBaseX = config.getInt("Overlay Base X", "overlay", overlayBaseX, 0, 5000,
                "X offset of the stamina bar from the right edge of the screen");
        overlayBaseY = config.getInt("Overlay Base Y", "overlay", overlayBaseY, 0, 5000,
                "Y offset of the stamina bar from the bottom edge of the screen");

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals("simplestamina")) {
            syncConfig(); // update values and save the file
        }
    }
}
