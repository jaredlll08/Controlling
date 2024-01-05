package com.blamejared.controlling;

import com.blamejared.controlling.api.entries.ICategoryEntry;
import com.blamejared.controlling.api.entries.IInputEntry;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

public class ControllingConstants {
    
    public static final MutableComponent COMPONENT_CONTROLS_RESET = Component.translatable("controls.reset");
    public static final MutableComponent COMPONENT_CONTROLS_RESET_ALL = Component.translatable("controls.resetAll");
    public static final MutableComponent COMPONENT_OPTIONS_CONFIRM_RESET = Component.translatable("options.confirmReset");
    public static final MutableComponent COMPONENT_OPTIONS_SHOW_NONE = Component.translatable("options.showNone");
    public static final MutableComponent COMPONENT_OPTIONS_SHOW_ALL = Component.translatable("options.showAll");
    public static final MutableComponent COMPONENT_OPTIONS_SHOW_CONFLICTS = Component.translatable("options.showConflicts");
    public static final MutableComponent COMPONENT_OPTIONS_SORT = Component.translatable("options.sort");
    public static final MutableComponent COMPONENT_OPTIONS_TOGGLE_FREE = Component.translatable("options.toggleFree");
    public static final MutableComponent COMPONENT_OPTIONS_AVAILABLE_KEYS = Component.translatable("options.availableKeys");
    
    
    public static final SearchableType<KeyBindsList.Entry> SEARCHABLE_KEYBINDINGS = new SearchableType.Builder<KeyBindsList.Entry>()
            .component(SearchableComponent.create("category", entry -> {
                if(entry instanceof ICategoryEntry cat) {
                    return Optional.of(cat.name().getString());
                } else if(entry instanceof IKeyEntry key) {
                    return Optional.of(key.categoryName().getString());
                }
                return Optional.empty();
            }))
            .component(SearchableComponent.create("key", entry -> {
                if(entry instanceof IKeyEntry key) {
                    return Optional.of(key.getKey().getTranslatedKeyMessage().getString());
                }
                return Optional.empty();
            }))
            .defaultComponent(SearchableComponent.create("name", entry -> {
                if(entry instanceof IKeyEntry key) {
                    return Optional.of(key.getKeyDesc().getString());
                } else if(entry instanceof IInputEntry input) {
                    return Optional.of(input.getInput().getName());
                }
                return Optional.empty();
            }))
            .build();
    
    
}
