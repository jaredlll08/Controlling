package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryMouseReleasedEvent;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * KeyEntryMouseReleasedEvent is called at the start of {@link IKeyEntry#mouseReleased(MouseButtonEvent)}.
 * <p>
 * If you are consuming this event, call {@link KeyEntryMouseReleasedEvent#handled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseReleasedEvent implements IKeyEntryMouseReleasedEvent {
    
    private final IKeyEntry entry;
    private final MouseButtonEvent event;
    
    private boolean handled;
    
    public KeyEntryMouseReleasedEvent(IKeyEntry entry, MouseButtonEvent event) {
        
        this.entry = entry;
        this.event = event;
    }
    
    public IKeyEntry entry() {
        
        return entry;
    }
    
    @Override
    public MouseButtonEvent event() {
        
        return event;
    }
    
    public boolean handled() {
        
        return handled;
    }
    
    public void handled(boolean handled) {
        
        this.handled = handled;
    }
    
}
