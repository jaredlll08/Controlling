package com.blamejared.controlling.events;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openGui(ScreenEvent.Opening event) {
        
        try {
            if(event.getScreen() instanceof KeyBindsScreen gui && !(event.getScreen() instanceof NewKeyBindsScreen)) {
                event.setNewScreen(new NewKeyBindsScreen(((AccessOptionsSubScreen) gui).controlling$getLastScreen(), Minecraft.getInstance().options));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
