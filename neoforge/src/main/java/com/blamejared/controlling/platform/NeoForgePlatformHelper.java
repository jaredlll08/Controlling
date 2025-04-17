package com.blamejared.controlling.platform;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessKeyBindsScreenNeoForge;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class NeoForgePlatformHelper implements IPlatformHelper {
    
    @Override
    public boolean hasConflictingModifier(KeyMapping keybinding, KeyMapping other) {
        
        return keybinding.hasKeyModifierConflict(other);
    }
    
    @Override
    public void setKey(Options options, KeyMapping keybinding, InputConstants.Key key) {
        
        //This code is *never* called in a default neoforge environment, but is kept in-case some other mod calls it.
        keybinding.setKeyModifierAndCode(KeyModifier.NONE, key);
        IPlatformHelper.super.setKey(options, keybinding, key);
    }
    
    @Override
    public boolean isKeyCodeModifier(InputConstants.Key key) {
        
        return KeyModifier.isKeyCodeModifier(key);
    }
    
    @Override
    public Component getKeyName(KeyMapping mapping) {
        
        return mapping.getDisplayName();
    }
    
    @Override
    public void handleKeyPress(NewKeyBindsScreen screen, Options options, int key, int scancode, int mods) {
        
        if(screen.selectedKey != null) {
            InputConstants.Key pressed = InputConstants.getKey(key, scancode);
            AccessKeyBindsScreenNeoForge access = (AccessKeyBindsScreenNeoForge) screen;
            if(access.getLastPressedModifier() == InputConstants.UNKNOWN && KeyModifier.isKeyCodeModifier(pressed)) {
                access.setLastPressedModifier(pressed);
                access.setIsLastModifierHeldDown(true);
            } else {
                access.setLastPressedKey(pressed);
                access.setIsLastKeyHeldDown(true);
            }
        }
    }
    
    @Override
    public boolean handleKeyReleased(NewKeyBindsScreen screen, Options options, int key, int scancode, int mods) {
        
        AccessKeyBindsScreenNeoForge access = (AccessKeyBindsScreenNeoForge) screen;
        if(screen.selectedKey == null) {
            return false;
        }
        if(key == GLFW.GLFW_KEY_ESCAPE) {
            screen.selectedKey.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.UNKNOWN);
            screen.selectedKey.setKey(InputConstants.UNKNOWN);
            access.setLastPressedKey(InputConstants.UNKNOWN);
            access.setLastPressedModifier(InputConstants.UNKNOWN);
            access.setIsLastKeyHeldDown(false);
            access.setIsLastModifierHeldDown(false);
        } else {
            InputConstants.Key pressed = InputConstants.getKey(key, scancode);
            if(access.getLastPressedKey().equals(pressed)) {
                access.setIsLastKeyHeldDown(false);
            } else if(access.getLastPressedModifier().equals(pressed)) {
                access.setIsLastModifierHeldDown(false);
            }
            if(!access.isIsLastKeyHeldDown() && !access.isIsLastModifierHeldDown()) {
                if(!access.getLastPressedKey().equals(InputConstants.UNKNOWN)) {
                    screen.selectedKey.setKeyModifierAndCode(KeyModifier.getKeyModifier(access.getLastPressedModifier()), access.getLastPressedKey());
                    screen.selectedKey.setKey(access.getLastPressedKey());
                } else {
                    screen.selectedKey.setKeyModifierAndCode(KeyModifier.NONE, access.getLastPressedModifier());
                    screen.selectedKey.setKey(access.getLastPressedModifier());
                }
                access.setLastPressedKey(InputConstants.UNKNOWN);
                access.setLastPressedModifier(InputConstants.UNKNOWN);
            } else {
                return true;
            }
        }
        screen.selectedKey = null;
        screen.lastKeySelection = Util.getMillis();
        screen.getKeyBindsList().resetMappingAndUpdateButtons();
        return true;
    }
    
    @Override
    public void setToDefault(Options options, KeyMapping keybinding) {
        
        keybinding.setToDefault();
    }
    
}
