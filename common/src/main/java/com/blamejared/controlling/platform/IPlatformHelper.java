package com.blamejared.controlling.platform;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
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
    
    default void handleKeyPress(NewKeyBindsScreen screen, Options options, int key, int scancode, int mods) {
        
        if(screen.selectedKey != null) {
            if(key == 256) {
                Services.PLATFORM.setKey(options, screen.selectedKey, InputConstants.UNKNOWN);
            } else {
                Services.PLATFORM.setKey(options, screen.selectedKey, InputConstants.getKey(key, scancode));
            }
            if(!Services.PLATFORM.isKeyCodeModifier(((AccessKeyMapping) screen.selectedKey).controlling$getKey())) {
                screen.selectedKey = null;
            }
            screen.lastKeySelection = Util.getMillis();
            screen.getKeyBindsList().resetMappingAndUpdateButtons();
        }
    }
    
}
