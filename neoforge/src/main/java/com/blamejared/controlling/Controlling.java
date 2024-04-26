package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("controlling")
public class Controlling {
    
    public Controlling(IEventBus modEventBus) {
        
        modEventBus.addListener(this::init);
    }
    
    private void init(final FMLClientSetupEvent event) {
        
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
    }
    
}
