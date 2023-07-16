package com.blamejared.controlling.api.events;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

public interface ISetToDefaultEvent {
    
    Options options();
    
    KeyMapping mapping();
    
}
