package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("controlling")
public class Controlling {
    
    public Controlling() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
    
}
