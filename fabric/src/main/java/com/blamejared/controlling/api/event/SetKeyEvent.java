package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.ISetKeyEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

/**
 * Fired when a key is set.
 */
public class SetKeyEvent implements ISetKeyEvent {
    
    private final Options options;
    private final KeyMapping mapping;
    private final InputConstants.Key key;
    
    public SetKeyEvent(Options options, KeyMapping mapping, InputConstants.Key key) {
        
        this.options = options;
        this.mapping = mapping;
        this.key = key;
    }
    
    @Override
    public Options options() {
        
        return options;
    }
    
    @Override
    public KeyMapping mapping() {
        
        return mapping;
    }
    
    @Override
    public InputConstants.Key key() {
        
        return key;
    }
    
}
