package com.blamejared.controlling.events;

import com.blamejared.controlling.client.gui.GuiNewControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        try {
            if(event.getGui() instanceof ControlsScreen && !(event.getGui() instanceof GuiNewControls)) {
                ControlsScreen gui = (ControlsScreen) event.getGui();
                event.setGui(new GuiNewControls(gui.parentScreen, Minecraft.getInstance().gameSettings));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
