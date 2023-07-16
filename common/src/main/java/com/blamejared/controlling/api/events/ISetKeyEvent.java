package com.blamejared.controlling.api.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

public interface ISetKeyEvent {
    
    Options options();
    
    KeyMapping mapping();
    
    InputConstants.Key key();
    
}
