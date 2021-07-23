package com.blamejared.controlling.client.gui;


import net.minecraft.client.resources.language.I18n;

import java.util.Comparator;
import java.util.List;

public enum SortOrder {
    NONE(entries -> {
    }), AZ(entries -> {
        entries.sort(Comparator.comparing(o -> ((GuiNewKeyBindingList.KeyEntry) o).getKeyDesc()));
    }), ZA(entries -> {
        entries.sort((o1, o2) -> ((GuiNewKeyBindingList.KeyEntry) o2).getKeyDesc()
                .compareTo(((GuiNewKeyBindingList.KeyEntry) o1).getKeyDesc()));
    });
    
    private final ISort sorter;
    
    SortOrder(ISort sorter) {
        
        this.sorter = sorter;
    }
    
    public SortOrder cycle() {
        
        return SortOrder.values()[(this.ordinal() + 1) % SortOrder.values().length];
    }
    
    public void sort(List<GuiNewKeyBindingList.Entry> list) {
        
        this.sorter.sort(list);
    }
    
    public String getName() {
        
        return switch(this) {
            case NONE -> I18n.get("options.sortNone");
            case AZ -> I18n.get("options.sortAZ");
            case ZA -> I18n.get("options.sortZA");
        };
    }
}
