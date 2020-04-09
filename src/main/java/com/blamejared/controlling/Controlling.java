package com.blamejared.controlling;

import com.blamejared.controlling.events.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.blamejared.controlling.reference.Reference.MODID;

@Mod(MODID)
public class Controlling {
    
    public static Set<String> PATRON_LIST = new HashSet<>();
    
    public Controlling() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        new Thread(() -> {
            try {
                URL url = new URL("https://blamejared.com/patrons.txt");
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                urlConnection.setRequestProperty("User-Agent", "Controlling|1.15.2");
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    PATRON_LIST = reader.lines().filter(s -> !s.isEmpty()).collect(Collectors.toSet());
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
