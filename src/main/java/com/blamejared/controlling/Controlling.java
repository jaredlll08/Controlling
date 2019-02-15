package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.blamejared.controlling.reference.Reference.MODID;

@Mod(MODID)
public class Controlling {
    
    public Controlling() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }
    
    private void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
