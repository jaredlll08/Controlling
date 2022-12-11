package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.SearchType;
import com.blamejared.controlling.api.SortOrder;
import com.blamejared.controlling.mixin.AccessKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessKeyMapping;
import com.blamejared.controlling.mixin.AccessScreen;
import com.blamejared.controlling.platform.Services;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NewKeyBindsScreen extends KeyBindsScreen {
    
    
    private Button buttonReset;
    private final Options options;
    
    private String lastSearch;
    private EditBox search;
    
    private DisplayMode displayMode;
    private SearchType searchType;
    private SortOrder sortOrder;
    
    private Button buttonNone;
    private Button buttonConflicting;
    private FancyCheckbox buttonKey;
    private FancyCheckbox buttonCat;
    private boolean confirmingReset;
    private boolean showFree;
    
    private KeyBindsList customKeyList;
    private Supplier<FreeKeysList> freeKeyList;
    
    public NewKeyBindsScreen(Screen screen, Options settings) {
        
        super(screen, settings);
        this.options = settings;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void init() {
        
        this.customKeyList = new NewKeyBindsList(this, this.minecraft);
        this.freeKeyList = Suppliers.memoize(() -> new FreeKeysList(this, this.minecraft));
        this.setKeyBindsList(showFree ? this.freeKeyList.get() : this.customKeyList);
        this.addWidget(getKeyBindsList());
        this.setFocused(getKeyBindsList());
        this.addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_GUI_DONE, (btn) -> Objects.requireNonNull(this.minecraft)
                .setScreen(this.lastScreen)).bounds(this.width / 2 - 155 + 160, this.height - 29, 150, 20).build());
        
        this.buttonReset = this.addRenderableWidget(Button.builder(confirmingReset ? ControllingConstants.COMPONENT_OPTIONS_CONFIRM_RESET : ControllingConstants.COMPONENT_CONTROLS_RESET_ALL, btn -> {
            
            if(!confirmingReset) {
                confirmingReset = true;
                btn.setMessage(ControllingConstants.COMPONENT_OPTIONS_CONFIRM_RESET);
                return;
            }
            confirmingReset = false;
            btn.setMessage(ControllingConstants.COMPONENT_CONTROLS_RESET_ALL);
            for(KeyMapping keybinding : Objects.requireNonNull(minecraft).options.keyMappings) {
                Services.PLATFORM.setToDefault(minecraft.options, keybinding);
                KeyMapping.resetMapping();
            }
            
            KeyMapping.releaseAll();
        }).bounds(this.width / 2 - 155, this.height - 29, 74, 20).build());
        this.buttonNone = this.addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE, (btn) -> {
            if(displayMode == DisplayMode.NONE) {
                buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE);
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_ALL);
                buttonConflicting.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS);
            }
            filterKeys();
        }).bounds(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20).build());
        this.buttonConflicting = this.addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS, (btn) -> {
            if(displayMode == DisplayMode.CONFLICTING) {
                buttonConflicting.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS);
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                buttonConflicting.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_ALL);
                buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE);
            }
            filterKeys();
        }).bounds(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20).build());
        search = new EditBox(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, Component.empty());
        addWidget(search);
        this.buttonKey = this.addRenderableWidget(new FancyCheckbox(this.width / 2 - (155 / 2), this.height - 29 - 37, 11, 11, ControllingConstants.COMPONENT_OPTIONS_KEY, false, btn -> {
            buttonCat.selected(false);
            searchType = btn.selected() ? SearchType.KEY : SearchType.NAME;
            filterKeys();
        }));
        this.buttonCat = this.addRenderableWidget(new FancyCheckbox(this.width / 2 - (155 / 2), this.height - 29 - 50, 11, 11, ControllingConstants.COMPONENT_OPTIONS_CATEGORY, false, btn -> {
            buttonKey.selected(false);
            searchType = btn.selected() ? SearchType.CATEGORY : SearchType.NAME;
            filterKeys();
        }));
        sortOrder = SortOrder.NONE;
        Button buttonSort = this.addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_SORT.copy()
                .append(": " + sortOrder.getName()), (btn) -> {
            sortOrder = sortOrder.cycle();
            btn.setMessage(ControllingConstants.COMPONENT_OPTIONS_SORT.copy().append(": " + sortOrder.getName()));
            filterKeys();
        }).bounds(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20).build());
        
        this.addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_TOGGLE_FREE, (btn) -> {
            this.removeWidget(getKeyBindsList());
            if(showFree) {
                buttonSort.active = true;
                buttonCat.active = true;
                buttonKey.active = true;
                buttonNone.active = true;
                buttonConflicting.active = true;
                buttonReset.active = true;
                setKeyBindsList(customKeyList);
            } else {
                freeKeyList.get().recalculate();
                buttonSort.active = false;
                buttonCat.active = false;
                buttonKey.active = false;
                buttonNone.active = false;
                buttonConflicting.active = false;
                buttonReset.active = false;
                setKeyBindsList(freeKeyList.get());
            }
            this.addWidget(getKeyBindsList());
            this.setFocused(getKeyBindsList());
            showFree = !showFree;
        }).bounds(this.width / 2 - 155 + 76, this.height - 29, 74, 20).build());
        
        lastSearch = "";
        displayMode = DisplayMode.ALL;
        searchType = SearchType.NAME;
    }
    
    @Override
    public boolean charTyped(char character, int code) {
        
        return search.charTyped(character, code);
    }
    
    @Override
    public void tick() {
        
        this.search.tick();
        if(!lastSearch.equals(search.getValue())) {
            filterKeys();
        }
    }
    
    public void filterKeys() {
        
        lastSearch = search.getValue();
        getKeyBindsList().children().clear();
        if(getKeyBindsList() instanceof NewKeyBindsList) {
            
            if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
                getKeyBindsList().children().addAll(((CustomList) getKeyBindsList()).getAllEntries());
                return;
            }
            getKeyBindsList().setScrollAmount(0);
            Predicate<NewKeyBindsList.KeyEntry> filters = switch(searchType) {
                case NAME -> displayMode.getPredicate()
                        .and(keyEntry -> keyEntry.getKeyDesc()
                                .getString()
                                .toLowerCase()
                                .contains(lastSearch.toLowerCase()));
                case CATEGORY -> displayMode.getPredicate()
                        .and(keyEntry -> Component.translatable(keyEntry.getKeybinding().getCategory()).getString()
                                .toLowerCase()
                                .contains(lastSearch.toLowerCase()));
                case KEY -> displayMode.getPredicate()
                        .and(keyEntry -> keyEntry.getKeybinding()
                                .getTranslatedKeyMessage()
                                .getString()
                                .toLowerCase()
                                .contains(lastSearch.toLowerCase()));
            };
            
            
            for(NewKeyBindsList.Entry entry : ((CustomList) getKeyBindsList()).getAllEntries()) {
                if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                    if(entry instanceof NewKeyBindsList.KeyEntry keyEntry) {
                        if(filters.test(keyEntry)) {
                            getKeyBindsList().children().add(entry);
                        }
                    } else {
                        getKeyBindsList().children().add(entry);
                    }
                } else {
                    if(entry instanceof NewKeyBindsList.KeyEntry keyEntry) {
                        if(filters.test(keyEntry)) {
                            getKeyBindsList().children().add(entry);
                        }
                    }
                }
                
            }
            if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                Set<NewKeyBindsList.CategoryEntry> categories = new LinkedHashSet<>();
                
                for(KeyBindsList.Entry entry : getKeyBindsList().children()) {
                    if(entry instanceof NewKeyBindsList.CategoryEntry cEntry) {
                        categories.add(cEntry);
                        for(KeyBindsList.Entry child : getKeyBindsList().children()) {
                            if(child instanceof NewKeyBindsList.KeyEntry childEntry) {
                                if(childEntry.getKeybinding().getCategory().equals(cEntry.getName())) {
                                    categories.remove(cEntry);
                                }
                            }
                        }
                    }
                }
                getKeyBindsList().children().removeAll(categories);
            }
            sortOrder.sort(getKeyBindsList().children());
            
        } else if(getKeyBindsList() instanceof FreeKeysList) {
            if(lastSearch.isEmpty()) {
                getKeyBindsList().children().addAll(((CustomList) getKeyBindsList()).getAllEntries());
                return;
            }
            getKeyBindsList().setScrollAmount(0);
            
            for(FreeKeysList.Entry entry : ((CustomList) getKeyBindsList()).getAllEntries()) {
                if(entry instanceof FreeKeysList.InputEntry inputEntry) {
                    if(inputEntry.getInput().toString().toLowerCase().contains(lastSearch.toLowerCase())) {
                        getKeyBindsList().children().add(entry);
                    }
                } else {
                    getKeyBindsList().children().add(entry);
                }
                
            }
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        
        this.renderBackground(stack);
        getKeyBindsList().render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 16777215);
        boolean flag = false;
        
        if(!showFree) {
            for(KeyMapping keybinding : this.options.keyMappings) {
                if(!keybinding.isDefault()) {
                    flag = true;
                    break;
                }
            }
        }
        search.render(stack, mouseX, mouseY, partialTicks);
        this.buttonReset.active = flag;
        if(!flag) {
            confirmingReset = false;
            buttonReset.setMessage(ControllingConstants.COMPONENT_CONTROLS_RESET_ALL);
        }
        
        font.draw(stack, ControllingConstants.COMPONENT_OPTIONS_SEARCH, this.width / 2f - (155 / 2f) - (font.width(ControllingConstants.COMPONENT_OPTIONS_SEARCH.getString())) - 5, this.height - 29 - 42, 16777215);
        
        for(Renderable renderable : getScreenAccess().controlling$getRenderables()) {
            renderable.render(stack, mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public boolean mouseClicked(double mx, double my, int mb) {
        
        boolean valid;
        if(this.selectedKey != null) {
            this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(mb));
            this.selectedKey = null;
            KeyMapping.resetMapping();
            valid = true;
            search.setFocus(false);
        } else if(mb == 0 && getKeyBindsList().mouseClicked(mx, my, mb)) {
            this.setDragging(true);
            this.setFocused(getKeyBindsList());
            valid = true;
            search.setFocus(false);
        } else {
            valid = search.mouseClicked(mx, my, mb);
            if(!valid && search.isFocused() && mb == 1) {
                search.setValue("");
                valid = true;
            }
        }
        
        if(!valid) {
            
            for(GuiEventListener listeners : this.children()) {
                if(listeners.mouseClicked(mx, my, mb)) {
                    this.setFocused(listeners);
                    if(mb == 0) {
                        this.setDragging(true);
                    }
                    
                    return true;
                }
            }
            
        }
        
        
        return valid;
    }
    
    @Override
    public boolean mouseReleased(double mx, double my, int mb) {
        
        if(mb == 0 && getKeyBindsList().mouseReleased(mx, my, mb)) {
            this.setDragging(false);
            return true;
        } else if(search.isFocused()) {
            return search.mouseReleased(mx, my, mb);
        } else {
            this.setDragging(false);
            return false;
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        
        if(!search.isFocused() && this.selectedKey == null) {
            if(hasControlDown()) {
                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_F)) {
                    search.setFocus(true);
                    return true;
                }
            }
        }
        if(search.keyPressed(keyCode, scanCode, modifier)) {
            return true;
        }
        if(search.isFocused()) {
            if(keyCode == 256) {
                search.setFocus(false);
                return true;
            }
        }
        if(this.selectedKey != null) {
            if(keyCode == 256) {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.UNKNOWN);
            } else {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.getKey(keyCode, scanCode));
            }
            if(!Services.PLATFORM.isKeyCodeModifier(((AccessKeyMapping) this.selectedKey).controlling$getKey())) {
                this.selectedKey = null;
            }
            this.lastKeySelection = Util.getMillis();
            KeyMapping.resetMapping();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifier);
        }
    }
    
    
    private KeyBindsList getKeyBindsList() {
        
        return getAccess().controlling$getKeyBindsList();
    }
    
    
    private void setKeyBindsList(KeyBindsList newList) {
        
        getAccess().controlling$setKeyBindsList(newList);
    }
    
    private AccessScreen getScreenAccess() {
        
        return ((AccessScreen) this);
    }
    
    private AccessKeyBindsScreen getAccess() {
        
        return ((AccessKeyBindsScreen) this);
    }
    
}