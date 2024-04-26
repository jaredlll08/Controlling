package com.blamejared.controlling.api;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;

public enum SortOrder {
    NONE("options.sortNone", entries -> {
    }),
    AZ("options.sortAZ", entries -> entries.sort(Comparator.comparing(o -> o.getKeyDesc()
            .getString()))),
    ZA("options.sortZA", entries -> entries.sort(Comparator.comparing(o -> o.getKeyDesc()
            .getString(), Comparator.reverseOrder()))),
    KEY_AZ("options.sortKeyAZ", entries -> entries.sort(Comparator.<IKeyEntry, String>comparing(o -> o.getKeyDesc()
            .getString()).thenComparing(o -> o.getKeyDesc().getString()))),
    KEY_ZA("options.sortKeyZA", entries -> entries.sort(Comparator.<IKeyEntry, String>comparing(o -> o.getKeyDesc()
            .getString(), Comparator.reverseOrder()).thenComparing(o -> o.getKeyDesc().getString(), Comparator.reverseOrder())));
    
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
        //noinspection rawtypes,unchecked
        this.sorter.sort((List<IKeyEntry>)(List)list);
    }
    
    public Component getDisplay() {
        
        return this.display;
    }
    
}
