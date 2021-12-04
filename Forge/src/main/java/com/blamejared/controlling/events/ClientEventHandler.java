package com.blamejared.controlling.events;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        
        try {
            if(event.getGui() instanceof ControlsScreen gui && !(event.getGui() instanceof NewKeyBindsScreen)) {
                event.setGui(new NewKeyBindsScreen(((AccessOptionsSubScreen)gui).getLastScreen(), Minecraft.getInstance().options));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
