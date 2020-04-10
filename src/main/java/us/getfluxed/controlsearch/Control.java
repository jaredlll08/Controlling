package us.getfluxed.controlsearch;

import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import us.getfluxed.controlsearch.proxy.CommonProxy;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import static us.getfluxed.controlsearch.reference.Reference.*;

/**
 * Created by Jared on 8/28/2016.
 */
@Mod(modid = MODID, name = NAME, version = VERSION, clientSideOnly = true)
public class Control {
    
    @SidedProxy(clientSide = "us.getfluxed.controlsearch.proxy.ClientProxy", serverSide = "us.getfluxed.controlsearch.proxy.CommonProxy")
    public static CommonProxy PROXY;
    public static Set<String> PATRON_LIST = new HashSet<>();
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.registerEvents();
        new Thread(() -> {
            try {
                URL url = new URL("https://blamejared.com/patrons.txt");
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                urlConnection.setRequestProperty("User-Agent", "Controlling|1.12.2");
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    PATRON_LIST = reader.lines().filter(s -> !s.isEmpty()).collect(Collectors.toSet());
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
    
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    
    }
    
}
