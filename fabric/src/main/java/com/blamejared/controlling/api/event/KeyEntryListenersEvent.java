package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryListenersEvent;
import com.blamejared.controlling.client.NewKeyBindsList;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * GetKeyEntryListenersEvent is called to get the values for {@link IKeyEntry#children()}.
 * Allowing for mods to add more listeners.
 */
public class KeyEntryListenersEvent implements IKeyEntryListenersEvent {
    
    private final IKeyEntry entry;
    
    private final List<GuiEventListener> listeners;
    
    public KeyEntryListenersEvent(IKeyEntry entry) {
        
        this.entry = entry;
        this.listeners = new ArrayList<>();
        
        getListeners().add(entry.getBtnChangeKeyBinding());
        getListeners().add(entry.getBtnResetKeyBinding());
    }
    
    
    public List<GuiEventListener> getListeners() {
        
        return listeners;
    }
    
    public IKeyEntry getEntry() {
        
        return entry;
    }
    
}
