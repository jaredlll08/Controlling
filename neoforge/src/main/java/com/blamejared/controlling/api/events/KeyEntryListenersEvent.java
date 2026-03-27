package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * GetKeyEntryListenersEvent is called to get the values for {@link IKeyEntry#children()}.
 * Allowing for mods to add more listeners.
 */
public class KeyEntryListenersEvent extends Event implements IKeyEntryListenersEvent {
    
    private final IKeyEntry entry;
    
    private final List<GuiEventListener> listeners;
    
    public KeyEntryListenersEvent(IKeyEntry entry) {
        
        this.entry = entry;
        this.listeners = new ArrayList<>();
        
        listeners().add(entry.getBtnChangeKeyBinding());
        listeners().add(entry.getBtnResetKeyBinding());
    }
    
    
    public List<GuiEventListener> listeners() {
        
        return listeners;
    }
    
    public IKeyEntry entry() {
        
        return entry;
    }
    
}
