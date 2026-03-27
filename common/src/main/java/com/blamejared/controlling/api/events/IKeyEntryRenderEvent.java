package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IKeyEntryRenderEvent {
    
    IKeyEntry entry();
    
    GuiGraphicsExtractor graphics();
    
    int x();
    
    int y();
    
    int rowLeft();
    
    int rowWidth();
    
    boolean hovered();
    
    float partialTicks();
    
}
