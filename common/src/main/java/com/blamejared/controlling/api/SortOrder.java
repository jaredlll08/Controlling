package com.blamejared.controlling.api;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public enum SortOrder implements Comparator<KeyBindsList.Entry> {
    NONE("options.sortNone", (o1, o2) -> 0),
    AZ("options.sortAZ", Comparator.comparing(o -> o.getKeyDesc()
            .getString())),
    ZA("options.sortZA", Comparator.comparing(o -> o.getKeyDesc()
            .getString(), Comparator.reverseOrder())),
    KEY_AZ("options.sortKeyAZ", Comparator.<IKeyEntry, String> comparing(o -> o.getKey().getTranslatedKeyMessage()
            .getString()).thenComparing(o -> o.getKeyDesc().getString())),
    KEY_ZA("options.sortKeyZA", Comparator.<IKeyEntry, String> comparing(o -> o.getKey().getTranslatedKeyMessage()
                    .getString(), Comparator.reverseOrder())
            .thenComparing(o -> o.getKeyDesc().getString(), Comparator.reverseOrder()));
    
    private final Component display;
    private final Comparator<KeyBindsList.Entry> sorter;
    
    SortOrder(String key, Comparator<IKeyEntry> sorter) {
        
        this.sorter = (o1, o2) -> {
            if(o1 instanceof IKeyEntry first && o2 instanceof IKeyEntry second) {
                return sorter.compare(first, second);
            }
            throw new IllegalStateException("Cannot sort non 'IKeyEntry'!");
        };
        this.display = ControllingConstants.COMPONENT_OPTIONS_SORT.copy()
                .append(": ")
                .append(Component.translatable(key));
    }
    
    public SortOrder cycle() {
        
        return SortOrder.values()[(this.ordinal() + 1) % SortOrder.values().length];
    }
    
    public Component getDisplay() {
        
        return this.display;
    }
    
    @Override
    public int compare(KeyBindsList.Entry o1, KeyBindsList.Entry o2) {
        
        return this.sorter.compare(o1, o2);
    }
}
