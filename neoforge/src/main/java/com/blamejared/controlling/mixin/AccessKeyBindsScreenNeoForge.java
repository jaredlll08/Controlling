package com.blamejared.controlling.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBindsScreen.class)
public interface AccessKeyBindsScreenNeoForge {
    
    @Accessor("lastPressedModifier")
    InputConstants.Key getLastPressedModifier();
    
    @Accessor("lastPressedModifier")
    void setLastPressedModifier(InputConstants.Key lastPressedModifier);
    
    @Accessor("isLastKeyHeldDown")
    boolean isIsLastKeyHeldDown();
    
    @Accessor("isLastKeyHeldDown")
    void setIsLastKeyHeldDown(boolean isLastKeyHeldDown);
    
    @Accessor("isLastModifierHeldDown")
    boolean isIsLastModifierHeldDown();
    
    @Accessor("isLastModifierHeldDown")
    void setIsLastModifierHeldDown(boolean isLastModifierHeldDown);
    
    @Accessor("lastPressedKey")
    InputConstants.Key getLastPressedKey();
    
    @Accessor("lastPressedKey")
    void setLastPressedKey(InputConstants.Key lastPressedKey);
    
}
