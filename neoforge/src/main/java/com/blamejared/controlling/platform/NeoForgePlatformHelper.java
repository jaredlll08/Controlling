package com.blamejared.controlling.platform;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessKeyBindsScreenNeoForge;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class NeoForgePlatformHelper implements IPlatformHelper {
    
    @Override
    public boolean hasConflictingModifier(KeyMapping keybinding, KeyMapping other) {
        
        return keybinding.hasKeyModifierConflict(other);
    }
    
    @Override
    public void setKey(Options options, KeyMapping keybinding, InputConstants.Key key) {
        
        keybinding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), key);
        IPlatformHelper.super.setKey(options, keybinding, key);
    }
    
    @Override
    public boolean isKeyCodeModifier(InputConstants.Key key) {
        
        return KeyModifier.isKeyCodeModifier(key);
    }

    @Override
    public void handleKeyPress(NewKeyBindsScreen screen, Options options, int key, int scancode, int mods) {
        IPlatformHelper.super.handleKeyPress(screen, options, key, scancode, mods);
        if(screen.selectedKey != null) {
            InputConstants.Key pressed = InputConstants.getKey(key, scancode);
            AccessKeyBindsScreenNeoForge access = (AccessKeyBindsScreenNeoForge) screen;
            if (net.neoforged.neoforge.client.settings.KeyModifier.isKeyCodeModifier(pressed)) {
                access.setLastPressedModifier(pressed);
                access.setIsLastModifierHeldDown(true);
            } else {
                access.setLastPressedKey(pressed);
                access.setIsLastKeyHeldDown(true);
            }
        }
    }

    @Override
    public void setToDefault(Options options, KeyMapping keybinding) {
        
        keybinding.setToDefault();
    }
    
}
