package net.voltyx.simplestamina;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ModConfigGui extends GuiConfig {

    public ModConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), "simplestamina", false, false, "Настройки SimpleStamina");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        Configuration cfg = ModConfig.config;

        for (String category : cfg.getCategoryNames()) {
            list.add(new ConfigElement(cfg.getCategory(category)));
        }

        return list;
    }
}
