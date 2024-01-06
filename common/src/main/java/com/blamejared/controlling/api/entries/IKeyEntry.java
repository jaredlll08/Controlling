package com.blamejared.controlling.api.entries;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface IKeyEntry {
    
    Component categoryName();
    
    KeyMapping getKey();
    
    Component getKeyDesc();
    
    Button getBtnResetKeyBinding();
    
    Button getBtnChangeKeyBinding();
    
    List<GuiEventListener> children();
    
    boolean mouseClicked(double mouseX, double mouseY, int buttonId);
    
    boolean mouseReleased(double mouseX, double mouseY, int buttonId);
    
    void render(GuiGraphics guiGraphics, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks);
    
}
