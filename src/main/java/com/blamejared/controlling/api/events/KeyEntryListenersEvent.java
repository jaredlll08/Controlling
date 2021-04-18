package com.blamejared.controlling.api.events;

import com.blamejared.controlling.client.gui.GuiNewKeyBindingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GetKeyEntryListenersEvent is called to get the values for {@link GuiNewKeyBindingList.KeyEntry#getEventListeners()}.
 * Allowing for mods to add more listeners.
 */
public class KeyEntryListenersEvent extends Event {
    
    private final GuiNewKeyBindingList.KeyEntry entry;
    
    private final List<IGuiEventListener> listeners;
    
    public KeyEntryListenersEvent(GuiNewKeyBindingList.KeyEntry entry) {
        
        this.entry = entry;
        this.listeners = new ArrayList<>();
        
        getListeners().add(entry.getBtnChangeKeyBinding());
        getListeners().add(entry.getBtnResetKeyBinding());
    }
    
    
    public List<IGuiEventListener> getListeners() {
        
        return listeners;
    }
    
    public GuiNewKeyBindingList.KeyEntry getEntry() {
        
        return entry;
    }
    
}
