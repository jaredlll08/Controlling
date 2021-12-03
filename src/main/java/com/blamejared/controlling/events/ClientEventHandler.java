package com.blamejared.controlling.events;

import com.blamejared.controlling.client.gui.GuiNewControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openScreen(ScreenOpenEvent event) {
        
        try {
            if(event.getScreen() instanceof ControlsScreen gui && !(event.getScreen() instanceof GuiNewControls)) {
                event.setScreen(new GuiNewControls(gui.lastScreen, Minecraft.getInstance().options));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
