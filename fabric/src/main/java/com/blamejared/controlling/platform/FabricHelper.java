package com.blamejared.controlling.platform;

import net.fabricmc.loader.api.FabricLoader;

public class FabricHelper {
    
    public static boolean isFabricLoaded() {
        
        return FabricLoader.getInstance().isModLoaded("fabric-api");
    }
    
}
