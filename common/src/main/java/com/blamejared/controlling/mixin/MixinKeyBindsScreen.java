package com.blamejared.controlling.mixin;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsScreen.class)
public abstract class MixinKeyBindsScreen extends OptionsSubScreen {
    
    public MixinKeyBindsScreen(Screen $$0, Options $$1, Component $$2) {
        
        super($$0, $$1, $$2);
    }
    
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/controls/KeyBindsScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public GuiEventListener controlling$replaceKeyBindsList(GuiEventListener par1) {
        
        if(controlling$isOurs() && par1.getClass().equals(KeyBindsList.class)) {
            return controlling$asOurs().hijackKeyBindsList();
        }
        return par1;
    }
    
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/OptionsSubScreen;init()V"))
    public void controlling$onSuperInit(CallbackInfo ci) {
        
        if(controlling$isOurs()) {
            controlling$asOurs().hijackInit();
        }
    }
    
    @Unique
    private boolean controlling$isOurs() {
        
        return ((Object) this) instanceof NewKeyBindsScreen;
    }
    
    @Unique
    private NewKeyBindsScreen controlling$asOurs() {
        
        return (NewKeyBindsScreen) (Object) this;
    }
    
}
