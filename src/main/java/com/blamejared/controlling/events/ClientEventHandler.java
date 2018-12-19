package com.blamejared.controlling.events;

import com.blamejared.controlling.client.gui.GuiNewControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        try {
            if(event.getGui() instanceof GuiControls && !(event.getGui() instanceof GuiNewControls)) {
                GuiControls gui = (GuiControls) event.getGui();
                Field field = gui.getClass().getDeclaredField("parentScreen");
                field.setAccessible(true);
                event.setGui(new GuiNewControls((GuiScreen) field.get(gui), Minecraft.getInstance().gameSettings));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
