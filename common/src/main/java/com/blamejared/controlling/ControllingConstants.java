package com.blamejared.controlling;

import com.blamejared.controlling.api.entries.ICategoryEntry;
import com.blamejared.controlling.api.entries.IInputEntry;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;
import java.util.function.Function;

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
    public static final MutableComponent COMPONENT_OPTIONS_EXACT_MATCH = Component.translatable("options.exactMatch");
    public static final MutableComponent COMPONENT_OPTIONS_FUZZY_MATCH = Component.translatable("options.fuzzyMatch");
    
    
    private static final Function<KeyBindsList.Entry, Optional<String>> KEYBINDING_CATEGORY = entry -> {
        if(entry instanceof ICategoryEntry cat) {
            return Optional.of(cat.category().label().getString());
        } else if(entry instanceof IKeyEntry key) {
            return Optional.of(key.categoryName().getString());
        }
        return Optional.empty();
    };
    private static final Function<KeyBindsList.Entry, Optional<String>> KEYBINDING_KEY = entry -> {
        if(entry instanceof IKeyEntry key && !key.getKey().isUnbound()) {
            return Optional.of(key.getKey().getTranslatedKeyMessage().getString());
        }
        return Optional.empty();
    };
    private static final Function<KeyBindsList.Entry, Optional<String>> KEYBINDING_NAME = entry -> {
        if(entry instanceof IKeyEntry key) {
            return Optional.of(key.getName().getString());
        } else if(entry instanceof IInputEntry input) {
            return Optional.of(input.getInput().getName());
        }
        return Optional.empty();
    };

    public static final SearchableType<KeyBindsList.Entry> SEARCHABLE_KEYBINDINGS = createSearchableKeybindings(false);
    public static final SearchableType<KeyBindsList.Entry> EXACT_SEARCHABLE_KEYBINDINGS = createSearchableKeybindings(true);

    private static SearchableType<KeyBindsList.Entry> createSearchableKeybindings(boolean exactMatch) {
        return new SearchableType.Builder<KeyBindsList.Entry>()
            .component(createSearchableComponent("category", KEYBINDING_CATEGORY, exactMatch))
            .component(createSearchableComponent("key", KEYBINDING_KEY, exactMatch))
            .defaultComponent(createSearchableComponent("name", KEYBINDING_NAME, exactMatch))
            .build();
    }

    private static SearchableComponent<KeyBindsList.Entry> createSearchableComponent(String key, Function<KeyBindsList.Entry, Optional<String>> value, boolean exactMatch) {
        if(exactMatch) {
            return SearchableComponent.create(key, value, (entry, search) -> value.apply(entry)
                    .map(componentValue -> componentValue.equalsIgnoreCase(search))
                    .orElse(false));
        }
        return SearchableComponent.create(key, value);
    }
    
    
}
