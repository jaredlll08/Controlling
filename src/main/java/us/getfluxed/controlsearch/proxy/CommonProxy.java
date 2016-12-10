package us.getfluxed.controlsearch.proxy;

import net.minecraftforge.common.MinecraftForge;
import us.getfluxed.controlsearch.events.CommonEventHandler;

/**
 * Created by Jared on 8/28/2016.
 */
public class CommonProxy {

    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

}
