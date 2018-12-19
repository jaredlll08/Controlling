package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;

import static com.blamejared.controlling.reference.Reference.MODID;

@Mod(MODID)
public class Controlling {
    
    public static final Logger LOGGER = LogManager.getLogger(StringUtils.capitalize(MODID));
    
    public Controlling() {
        // Register the preInit method for modloading
        FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
        // Register the init method for modloading
        FMLModLoadingContext.get().getModEventBus().addListener(this::init);
        
    }
    
    private void preInit(final FMLPreInitializationEvent event) {
        // some preinit code
        
    }
    
    private void init(final FMLInitializationEvent event) {
        // some example code
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
