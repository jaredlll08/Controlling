package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.input.MouseButtonEvent;

public interface IKeyEntryMouseReleasedEvent {
    
    IKeyEntry entry();
    
    MouseButtonEvent event();
    
    boolean handled();
    
    void handled(boolean handled);
    
}
