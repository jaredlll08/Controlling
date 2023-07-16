package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.IIsKeyCodeModifierEvent;
import com.mojang.blaze3d.platform.InputConstants;

/**
 * Fired to check if a {@link InputConstants.Key} is a valid key code modifier (like shift, control, alt).
 */
public class IsKeyCodeModifierEvent implements IIsKeyCodeModifierEvent {
    
    private final InputConstants.Key key;
    
    public IsKeyCodeModifierEvent(InputConstants.Key key) {
        
        this.key = key;
    }
    
    @Override
    public InputConstants.Key key() {
        
        return key;
    }
    
}
