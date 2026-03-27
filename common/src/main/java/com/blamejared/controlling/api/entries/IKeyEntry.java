package com.blamejared.controlling.api.entries;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface IKeyEntry {
    
    Component categoryName();
    
    KeyMapping getKey();
    
    Component getName();
    
    Button getBtnResetKeyBinding();
    
    Button getBtnChangeKeyBinding();
    
    List<GuiEventListener> children();
    
    boolean mouseClicked(MouseButtonEvent event, boolean doubleClick);
    
    boolean mouseReleased(MouseButtonEvent event);
    
    void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTicks);
    
}
