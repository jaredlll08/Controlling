package com.blamejared.controlling.client.gui;

import net.minecraft.client.resources.I18n;

import java.util.List;

public enum SortOrder {
    NONE(entries -> {
    }), AZ(entries -> {
        entries.sort((o1, o2) -> ((GuiNewKeyBindingList.KeyEntry) o1).getKeyDesc().compareTo(((GuiNewKeyBindingList.KeyEntry) o2).getKeyDesc()));
    }), ZA(entries -> {
        entries.sort((o1, o2) -> ((GuiNewKeyBindingList.KeyEntry) o2).getKeyDesc().compareTo(((GuiNewKeyBindingList.KeyEntry) o1).getKeyDesc()));
    });
    
    private ISort sorter;
    
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
        switch(this) {
            default:
            case NONE:
                return I18n.format("options.sortNone");
            case AZ:
                return I18n.format("options.sortAZ");
            case ZA:
                return I18n.format("options.sortZA");
        }
    }
}
