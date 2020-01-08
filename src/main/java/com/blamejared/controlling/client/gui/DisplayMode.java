package com.blamejared.controlling.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.function.Predicate;

public enum DisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> keyEntry.getKeybinding().isInvalid()), CONFLICTING(keyEntry -> {
        
        for(KeyBinding key : Minecraft.getInstance().gameSettings.keyBindings) {
            if(key.getKeyDescription().equals(keyEntry.getKeybinding().getKeyDescription()) || key.isInvalid()) {
                continue;
            } else {
                if(key.getKey().getKeyCode() == keyEntry.getKeybinding().getKey().getKeyCode()) {
                    return true;
                }
            }
        }
        return false;
    });
    
    
    private Predicate<GuiNewKeyBindingList.KeyEntry> predicate;
    
    DisplayMode(Predicate<GuiNewKeyBindingList.KeyEntry> predicate) {
        this.predicate = predicate;
    }
    
    public Predicate<GuiNewKeyBindingList.KeyEntry> getPredicate() {
        return predicate;
    }
}
