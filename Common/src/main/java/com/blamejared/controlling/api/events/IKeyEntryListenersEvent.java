package com.blamejared.controlling.api.events;

import com.blamejared.controlling.client.NewKeyBindsList;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

public interface IKeyEntryListenersEvent {
    
    List<GuiEventListener> getListeners();
    
    NewKeyBindsList.KeyEntry getEntry();
}
