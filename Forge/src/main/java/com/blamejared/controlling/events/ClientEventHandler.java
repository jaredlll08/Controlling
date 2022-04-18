package com.blamejared.controlling.events;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openGui(ScreenOpenEvent event) {
        
        try {
            if(event.getScreen() instanceof KeyBindsScreen gui && !(event.getScreen() instanceof NewKeyBindsScreen)) {
                event.setScreen(new NewKeyBindsScreen(((AccessOptionsSubScreen) gui).getLastScreen(), Minecraft.getInstance().options));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
