package com.blamejared.controlling.api.events;

import net.minecraft.client.KeyMapping;

public interface IHasConflictingModifierEvent {
    
    KeyMapping thisMapping();
    
    KeyMapping otherMapping();
    
}
