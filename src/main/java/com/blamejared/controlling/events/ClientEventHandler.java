package com.blamejared.controlling.events;

import com.blamejared.controlling.client.gui.GuiNewControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.lang.reflect.Field;

public class ClientEventHandler {
    
    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        try {
            if(event.getGui() instanceof ControlsScreen && !(event.getGui() instanceof GuiNewControls)) {
                ControlsScreen gui = (ControlsScreen) event.getGui();
                event.setGui(new GuiNewControls(gui.parentScreen, Minecraft.getInstance().gameSettings));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
