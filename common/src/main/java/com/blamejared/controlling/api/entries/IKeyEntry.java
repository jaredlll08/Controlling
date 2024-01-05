package com.blamejared.controlling.api.entries;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public interface IKeyEntry {
    
    Component categoryName();
    
    KeyMapping getKey();
    
    Component getKeyDesc();
    
}
