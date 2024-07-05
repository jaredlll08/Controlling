package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "controlling", name = "Controlling", version = "GRADLETOKEN_VERSION", acceptableRemoteVersions = "*")
public class Controlling {

    @Mod.EventHandler
    private void init(final FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        }
    }
}
