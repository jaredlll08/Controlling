package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.IHasConflictingModifierEvent;
import net.minecraft.client.KeyMapping;

/**
 * Fired to check if a {@link KeyMapping} conflicts with another {@link KeyMapping}.
 */
public record HasConflictingModifierEvent(KeyMapping thisMapping,
                                          KeyMapping otherMapping) implements IHasConflictingModifierEvent {
    
    
}
