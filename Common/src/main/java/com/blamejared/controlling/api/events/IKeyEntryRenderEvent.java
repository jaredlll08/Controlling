package com.blamejared.controlling.api.events;

import com.blamejared.controlling.client.NewKeyBindsList;
import com.mojang.blaze3d.vertex.PoseStack;

public interface IKeyEntryRenderEvent {
    
    NewKeyBindsList.KeyEntry getEntry();
    
    PoseStack getStack();
    
    int getSlotIndex();
    
    int getY();
    
    int getX();
    
    int getRowLeft();
    
    int getRowWidth();
    
    int getMouseX();
    
    int getMouseY();
    
    boolean isHovered();
    
    float getPartialTicks();
}
