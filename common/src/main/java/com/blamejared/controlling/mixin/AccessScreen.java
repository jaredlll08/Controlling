package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface AccessScreen {
    
    @Accessor("renderables")
    List<Renderable> controlling$getRenderables();
    
}
