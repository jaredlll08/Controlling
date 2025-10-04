package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;


/**
 * KeyEntryMouseClickedEvent is called at the start of {@link IKeyEntry#mouseClicked(MouseButtonEvent, boolean)}.
 * <p>
 * If you are consuming this event, call {@link KeyEntryMouseClickedEvent#setHandled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseClickedEvent extends MutableEvent implements IKeyEntryMouseClickedEvent {
    
    public static final EventBus<KeyEntryMouseClickedEvent> BUS = EventBus.create(KeyEntryMouseClickedEvent.class);
    
    private final IKeyEntry entry;
    private final MouseButtonEvent event;
    private final boolean doubleClick;
    
    private boolean handled;
    
    public KeyEntryMouseClickedEvent(IKeyEntry entry, MouseButtonEvent event, boolean doubleClick) {
        
        this.entry = entry;
        this.event = event;
        this.doubleClick = doubleClick;
    }
    
    public IKeyEntry getEntry() {
        
        return entry;
    }
    
    @Override
    public MouseButtonEvent event() {
        
        return event;
    }
    
    @Override
    public boolean doubleClick() {
        
        return doubleClick;
    }
    
    public boolean isHandled() {
        
        return handled;
    }
    
    public void setHandled(boolean handled) {
        
        this.handled = handled;
    }
    
}
