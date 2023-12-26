package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;

@Mod("controlling")
public class Controlling {
    
    public Controlling() {
        
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (remote, isServer) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }
    
    private void init(final FMLClientSetupEvent event) {
        
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
    }
    
}
