package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.input.MouseButtonEvent;

public interface IKeyEntryMouseReleasedEvent {
    
    IKeyEntry getEntry();
    
    MouseButtonEvent event();
    
    boolean isHandled();
    
    void setHandled(boolean handled);
    
}
