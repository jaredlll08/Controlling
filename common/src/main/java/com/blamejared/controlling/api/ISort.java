package com.blamejared.controlling.api;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.screens.controls.KeyBindsList;

import java.util.List;

public interface ISort {
    
    void sort(List<IKeyEntry> entries);
    
}
