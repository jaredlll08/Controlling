package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.neoforged.bus.api.Event;

/**
 * KeyEntryMouseReleasedEvent is called at the start of {@link IKeyEntry#mouseReleased(MouseButtonEvent)}.
 *
 * If you are consuming this event, call {@link KeyEntryMouseReleasedEvent#setHandled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseReleasedEvent extends Event implements IKeyEntryMouseReleasedEvent {
    
    private final IKeyEntry entry;
    private final MouseButtonEvent event;
    
    private boolean handled;
    
    public KeyEntryMouseReleasedEvent(IKeyEntry entry, MouseButtonEvent event) {
        
        this.entry = entry;
        this.event = event;
    }
    
    public IKeyEntry getEntry() {
        
        return entry;
    }
    
    @Override
    public MouseButtonEvent event() {
        
        return event;
    }
    
    public boolean isHandled() {
        
        return handled;
    }
    
    public void setHandled(boolean handled) {
        
        this.handled = handled;
    }
    
}
