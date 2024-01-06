package com.blamejared.controlling.api;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.blamejared.controlling.mixin.AccessKeyMapping;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.controls.KeyBindsList;

import java.util.function.Predicate;

public enum DisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> keyEntry.getKey().isUnbound()), CONFLICTING(keyEntry -> {
        
        for(KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if(!key.getName().equals(keyEntry.getKey().getName()) && !key.isUnbound()) {
                if(((AccessKeyMapping) key).controlling$getKey()
                        .getValue() == ((AccessKeyMapping) keyEntry.getKey()).controlling$getKey().getValue()) {
                    return true;
                }
            }
        }
        return false;
    });
    
    
    private final Predicate<IKeyEntry> predicate;
    
    DisplayMode(Predicate<IKeyEntry> predicate) {
        
        this.predicate = predicate;
    }
    
    public Predicate<KeyBindsList.Entry> getPredicate() {
        
        return entry -> entry instanceof IKeyEntry keyEntry && predicate.test(keyEntry);
    }
}
