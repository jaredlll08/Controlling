package com.blamejared.controlling.api;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.client.NewKeyBindsList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;

public enum SortOrder {
    NONE("options.sortNone", entries -> {
    }),
    AZ("options.sortAZ", entries -> entries.sort(Comparator.comparing(o -> ((NewKeyBindsList.KeyEntry) o).getKeyDesc()
            .getString()))),
    ZA("options.sortZA", entries -> entries.sort(Comparator.comparing(o -> ((NewKeyBindsList.KeyEntry) o).getKeyDesc()
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
    
    public void sort(List<NewKeyBindsList.Entry> list) {
        
        this.sorter.sort(list);
    }
    
    public Component getDisplay() {
        
        return this.display;
    }
}
