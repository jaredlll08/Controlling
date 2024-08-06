package com.blamejared.controlling.platform;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraftforge.client.settings.KeyModifier;

public class ForgePlatformHelper implements IPlatformHelper {
    
    @Override
    public boolean hasConflictingModifier(KeyMapping keybinding, KeyMapping other) {
        
        return keybinding.hasKeyModifierConflict(other);
    }
    
    @Override
    public void setKey(Options options, KeyMapping keybinding, InputConstants.Key key) {
        
        keybinding.setKeyModifierAndCode(null, key);
        IPlatformHelper.super.setKey(options, keybinding, key);
    }
    
    @Override
    public boolean isKeyCodeModifier(InputConstants.Key key) {
        
        return KeyModifier.isKeyCodeModifier(key);
    }
    
    @Override
    public void setToDefault(Options options, KeyMapping keybinding) {
        
        keybinding.setToDefault();
    }
    
}
