package com.blamejared.controlling.mixin;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public class OpenGuiMixin {
    
    @Shadow
    public Screen screen;
    
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen upgradeControlScreen(Screen opened) {
        // Swap the control options screen with our own instance whenever something tries to open one
        if(opened != null && KeyBindsScreen.class.equals(opened.getClass())) {
            return new NewKeyBindsScreen(this.screen, Minecraft.getInstance().options);
        }
        return opened;
    }
}
