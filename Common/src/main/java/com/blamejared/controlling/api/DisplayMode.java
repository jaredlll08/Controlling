package com.blamejared.controlling.api;

import com.blamejared.controlling.client.NewKeyBindsList;
import com.blamejared.controlling.mixin.AccessKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.function.Predicate;

public enum DisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> keyEntry.getKeybinding().isUnbound()), CONFLICTING(keyEntry -> {
        
        for(KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if(!key.getName().equals(keyEntry.getKeybinding().getName()) && !key.isUnbound()) {
                if(((AccessKeyMapping) key).controlling$getKey()
                        .getValue() == ((AccessKeyMapping) keyEntry.getKeybinding()).controlling$getKey().getValue()) {
                    return true;
                }
            }
        }
        return false;
    });
    
    
    private final Predicate<NewKeyBindsList.KeyEntry> predicate;
    
    DisplayMode(Predicate<NewKeyBindsList.KeyEntry> predicate) {
        
        this.predicate = predicate;
    }
    
    public Predicate<NewKeyBindsList.KeyEntry> getPredicate() {
        
        return predicate;
    }
}
