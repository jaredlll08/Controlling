package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * GetKeyEntryListenersEvent is called to get the values for {@link IKeyEntry#children()}.
 * Allowing for mods to add more listeners.
 */
public class KeyEntryListenersEvent extends MutableEvent implements IKeyEntryListenersEvent {
    
    public static final EventBus<KeyEntryListenersEvent> BUS = EventBus.create(KeyEntryListenersEvent.class);
    
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
