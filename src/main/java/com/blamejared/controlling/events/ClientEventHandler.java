package com.blamejared.controlling.events;

import com.blamejared.controlling.client.gui.GuiNewControls;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;

public class ClientEventHandler {
    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        try {
            if (event.gui instanceof GuiControls && !(event.gui instanceof GuiNewControls)) {
                event.gui = new GuiNewControls(
                        Minecraft.getMinecraft().currentScreen,
                        Minecraft.getMinecraft().gameSettings
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
