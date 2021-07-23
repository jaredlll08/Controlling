package com.blamejared.controlling.events;

import com.blamejared.controlling.client.gui.GuiNewControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        
        try {
            if(event.getGui() instanceof ControlsScreen gui && !(event.getGui() instanceof GuiNewControls)) {
                event.setGui(new GuiNewControls(gui.lastScreen, Minecraft.getInstance().options));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
