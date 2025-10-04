package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public interface IKeyEntryRenderEvent {
    
    IKeyEntry getEntry();
    
    GuiGraphics getGuiGraphics();
    
    int getY();
    
    int getX();
    
    int getRowLeft();
    
    int getRowWidth();
    
    boolean isHovered();
    
    float getPartialTicks();
    
}
