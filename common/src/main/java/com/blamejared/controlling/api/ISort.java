package com.blamejared.controlling.api;

import net.minecraft.client.gui.screens.controls.KeyBindsList;

import java.util.List;

public interface ISort {
    
    void sort(List<KeyBindsList.Entry> entries);
    
}
