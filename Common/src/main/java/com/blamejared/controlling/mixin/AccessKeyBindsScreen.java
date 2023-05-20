package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBindsScreen.class)
public interface AccessKeyBindsScreen {
    
    @Accessor("keyBindsList")
    KeyBindsList controlling$getKeyBindsList();
    
    @Accessor("keyBindsList")
    void controlling$setKeyBindsList(KeyBindsList newList);
    
    @Accessor("resetButton")
    Button controlling$getResetButton();
    
    @Accessor("resetButton")
    void controlling$setResetButton(Button resetButton);
    
}
