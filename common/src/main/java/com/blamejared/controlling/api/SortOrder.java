package com.blamejared.controlling.api;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.client.NewKeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;

import java.util.*;

public enum SortOrder {
    NONE("options.sortNone", entries -> {
    }),
    AZ("options.sortAZ", entries -> entries.sort(Comparator.comparing(o -> ((IKeyEntry) o).getKeyDesc()
            .getString()))),
    ZA("options.sortZA", entries -> entries.sort(Comparator.comparing(o -> ((IKeyEntry) o).getKeyDesc()
                    .getString())
            .reversed()));
    
    private final ISort sorter;
    private final Component display;
    
    SortOrder(String key, ISort sorter) {
        
        this.sorter = sorter;
        this.display = ControllingConstants.COMPONENT_OPTIONS_SORT.copy()
                .append(": ")
                .append(Component.translatable(key));
    }
    
    public SortOrder cycle() {
        
        return SortOrder.values()[(this.ordinal() + 1) % SortOrder.values().length];
    }
    
    public void sort(List<KeyBindsList.Entry> list) {
        
        list.removeIf(entry -> !(entry instanceof IKeyEntry));
        this.sorter.sort(list);
    }
    
    public Component getDisplay() {
        
        return this.display;
    }
    
}
