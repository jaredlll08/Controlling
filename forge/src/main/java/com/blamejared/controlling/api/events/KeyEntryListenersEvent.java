package com.blamejared.controlling.api.events;

import com.blamejared.controlling.client.NewKeyBindsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * GetKeyEntryListenersEvent is called to get the values for {@link NewKeyBindsList.KeyEntry#children()}.
 * Allowing for mods to add more listeners.
 */
public class KeyEntryListenersEvent extends Event implements IKeyEntryListenersEvent {
    
    private final NewKeyBindsList.KeyEntry entry;
    
    private final List<GuiEventListener> listeners;
    
    public KeyEntryListenersEvent(NewKeyBindsList.KeyEntry entry) {
        
        this.entry = entry;
        this.listeners = new ArrayList<>();
        
        getListeners().add(entry.getBtnChangeKeyBinding());
        getListeners().add(entry.getBtnResetKeyBinding());
    }
    
    
    public List<GuiEventListener> getListeners() {
        
        return listeners;
    }
    
    public NewKeyBindsList.KeyEntry getEntry() {
        
        return entry;
    }
    
}
