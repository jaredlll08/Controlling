package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsScreen.class)
public interface AccessKeyBindsScreen {
    
    @Accessor("controlList")
    ControlList getKeyBindsList();
    
    @Accessor("controlList")
    void setKeyBindsList(ControlList newList);
    
}
