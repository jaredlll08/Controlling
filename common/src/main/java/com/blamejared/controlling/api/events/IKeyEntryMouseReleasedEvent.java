package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;

public interface IKeyEntryMouseReleasedEvent {
    
    IKeyEntry getEntry();
    
    double getMouseX();
    
    double getMouseY();
    
    int getButtonId();
    
    boolean isHandled();
    
    void setHandled(boolean handled);
    
}
