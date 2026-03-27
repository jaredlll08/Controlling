package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.input.MouseButtonEvent;

public interface IKeyEntryMouseClickedEvent {
    
    IKeyEntry entry();
    
    MouseButtonEvent event();
    
    boolean doubleClick();
    
    boolean handled();
    
    void handled(boolean handled);
    
}
