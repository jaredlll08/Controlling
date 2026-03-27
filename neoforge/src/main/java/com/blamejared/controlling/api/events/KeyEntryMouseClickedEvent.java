package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.neoforged.bus.api.Event;


/**
 * KeyEntryMouseClickedEvent is called at the start of {@link IKeyEntry#mouseClicked(MouseButtonEvent, boolean)}.
 * <p>
 * If you are consuming this event, call {@link KeyEntryMouseClickedEvent#handled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseClickedEvent extends Event implements IKeyEntryMouseClickedEvent {
    
    private final IKeyEntry entry;
    private final MouseButtonEvent event;
    private final boolean doubleClick;
    private boolean handled;
    
    public KeyEntryMouseClickedEvent(IKeyEntry entry, MouseButtonEvent event, boolean doubleClick) {
        
        this.entry = entry;
        this.event = event;
        this.doubleClick = doubleClick;
    }
    
    public IKeyEntry entry() {
        
        return entry;
    }
    
    public MouseButtonEvent event() {
        
        return event;
    }
    
    public boolean doubleClick() {
        
        return doubleClick;
    }
    
    public boolean handled() {
        
        return handled;
    }
    
    public void handled(boolean handled) {
        
        this.handled = handled;
    }
    
}
