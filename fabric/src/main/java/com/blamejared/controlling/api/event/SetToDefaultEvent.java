package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.ISetToDefaultEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

/**
 * Fired when a key is set to the default, either through the individual reset button or the global reset button.
 */
public record SetToDefaultEvent(Options options, KeyMapping mapping) implements ISetToDefaultEvent {

}
