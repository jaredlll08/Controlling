package com.blamejared.controlling.client.gui;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.function.Predicate;

public enum DisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> keyEntry.getKeybinding().isUnbound()), CONFLICTING(keyEntry -> {
        
        for(KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if(key.getName().equals(keyEntry.getKeybinding().getName()) || key.isUnbound()) {
                continue;
            } else {
                if(key.getKey().getValue() == keyEntry.getKeybinding().getKey().getValue()) {
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
