package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

public interface IKeyEntryListenersEvent {
    
    List<GuiEventListener> listeners();
    
    IKeyEntry entry();
    
}
