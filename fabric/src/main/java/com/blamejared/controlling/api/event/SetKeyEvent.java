package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.ISetKeyEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

/**
 * Fired when a key is set.
 */
public record SetKeyEvent(Options options, KeyMapping mapping, InputConstants.Key key) implements ISetKeyEvent {

}
