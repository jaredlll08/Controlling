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
        World w;
        try {
            if(event.getGui() instanceof ControlsScreen && !(event.getGui() instanceof GuiNewControls)) {
                ControlsScreen gui = (ControlsScreen) event.getGui();
                Field parent = null;
                for(Field field : gui.getClass().getDeclaredFields()) {
                    if(field.getType() == Screen.class) {
                        parent = field;
                    }
                }
                if(parent == null) {
                    return;
                }
                parent.setAccessible(true);
                event.setGui(new GuiNewControls((Screen) parent.get(gui), Minecraft.getInstance().gameSettings));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
