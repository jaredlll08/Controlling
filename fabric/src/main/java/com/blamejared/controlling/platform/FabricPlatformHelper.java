package com.blamejared.controlling.platform;

import com.blamejared.controlling.api.event.ControllingEvents;
import com.blamejared.controlling.api.event.HasConflictingModifierEvent;
import com.blamejared.controlling.api.event.IsKeyCodeModifierEvent;
import com.blamejared.controlling.api.event.SetKeyEvent;
import com.blamejared.controlling.api.event.SetToDefaultEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

public class FabricPlatformHelper implements IPlatformHelper {
    
    @Override
    public boolean hasConflictingModifier(KeyMapping keybinding, KeyMapping other) {
        
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            return ControllingEvents.HAS_CONFLICTING_MODIFIERS_EVENT.invoker()
                    .handle(new HasConflictingModifierEvent(keybinding, other));
        }
        return IPlatformHelper.super.hasConflictingModifier(keybinding, other);
        
    }
    
    @Override
    public void setKey(Options options, KeyMapping keybinding, InputConstants.Key key) {
        
        boolean handled = FabricLoader.getInstance().isModLoaded("fabric")
                && ControllingEvents.SET_KEY_EVENT.invoker().handle(new SetKeyEvent(options, keybinding, key));
        if(!handled) {
            IPlatformHelper.super.setKey(options, keybinding, key);
        }
    }
    
    @Override
    public void setToDefault(Options options, KeyMapping keybinding) {
        
        boolean handled = FabricLoader.getInstance().isModLoaded("fabric")
                && ControllingEvents.SET_TO_DEFAULT_EVENT.invoker().handle(new SetToDefaultEvent(options, keybinding));
        if(!handled) {
            IPlatformHelper.super.setToDefault(options, keybinding);
        }
    }
    
    @Override
    public boolean isKeyCodeModifier(InputConstants.Key key) {
        
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            return ControllingEvents.IS_KEY_CODE_MODIFIER_EVENT.invoker().handle(new IsKeyCodeModifierEvent(key));
        }
        return IPlatformHelper.super.isKeyCodeModifier(key);
        
    }
    
}
