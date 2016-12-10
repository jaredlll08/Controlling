package us.getfluxed.controlsearch.proxy;

import net.minecraftforge.common.MinecraftForge;
import us.getfluxed.controlsearch.events.ClientEventHandler;

/**
 * Created by Jared on 8/28/2016.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void registerEvents() {
        super.registerEvents();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}

