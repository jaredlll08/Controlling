package com.blamejared.controlling.platform;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

public interface IPlatformHelper {
    
    default boolean hasConflictingModifier(KeyMapping keybinding, KeyMapping other) {
        return false;
    }
    
    default void setKey(Options options, KeyMapping keybinding, InputConstants.Key key) {
        options.setKey(keybinding, key);
    }
    
    default void setToDefault(Options options, KeyMapping keybinding) {
        options.setKey(keybinding, keybinding.getDefaultKey());
    }
    
    default boolean isKeyCodeModifier(InputConstants.Key key) {
        return false;
    }
    
}
