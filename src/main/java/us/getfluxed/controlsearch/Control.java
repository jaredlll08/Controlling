package us.getfluxed.controlsearch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import us.getfluxed.controlsearch.proxy.CommonProxy;

import static us.getfluxed.controlsearch.reference.Reference.*;

/**
 * Created by Jared on 8/28/2016.
 */
@Mod(modid = MODID, name = NAME, version = VERSION, clientSideOnly = true)
public class Control {

    @SidedProxy(clientSide = "us.getfluxed.controlsearch.proxy.ClientProxy", serverSide = "us.getfluxed.controlsearch.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.registerEvents();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {

    }

}
