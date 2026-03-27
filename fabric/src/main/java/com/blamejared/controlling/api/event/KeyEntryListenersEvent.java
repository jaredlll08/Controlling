package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryListenersEvent;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * GetKeyEntryListenersEvent is called to get the values for {@link IKeyEntry#children()}.
 * Allowing for mods to add more listeners.
 */
public record KeyEntryListenersEvent(IKeyEntry entry,
                                     List<GuiEventListener> listeners) implements IKeyEntryListenersEvent {
    
    public KeyEntryListenersEvent(IKeyEntry entry, List<GuiEventListener> listeners) {
        
        this.entry = entry;
        this.listeners = listeners;
        listeners().add(entry.getBtnChangeKeyBinding());
        listeners().add(entry.getBtnResetKeyBinding());
    }
    
    public KeyEntryListenersEvent(IKeyEntry entry) {
        
        this(entry, new ArrayList<>());
        
    }
    
}
