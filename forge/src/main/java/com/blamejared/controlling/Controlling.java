package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("controlling")
public class Controlling {
    
    public Controlling() {
        ScreenEvent.Opening.BUS.addListener(ClientEventHandler::openGui);
    }
    
}
