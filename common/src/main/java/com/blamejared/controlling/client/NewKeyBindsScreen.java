package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.SortOrder;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.mixin.AccessAbstractSelectionList;
import com.blamejared.controlling.mixin.AccessKeyBindsScreen;
import com.blamejared.controlling.platform.Services;
import com.blamejared.searchables.api.autcomplete.AutoCompletingEditBox;
import com.google.common.base.Suppliers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NewKeyBindsScreen extends KeyBindsScreen {
    
    private AutoCompletingEditBox<KeyBindsList.Entry> search;
    private DisplayMode displayMode;
    private SortOrder sortOrder = SortOrder.NONE;
    private Button buttonDisplayMode;
    private Button buttonSort;
    private final DisplayableBoolean confirmingReset = new DisplayableBoolean(false, ControllingConstants.COMPONENT_OPTIONS_CONFIRM_RESET, ControllingConstants.COMPONENT_CONTROLS_RESET_ALL);
    private boolean showFree;
    private boolean exactMatch;
    private Supplier<NewKeyBindsList> newKeyList;
    private Supplier<FreeKeysList> freeKeyList;
    
    public NewKeyBindsScreen(Screen lastScreen, Options options) {
        
        super(lastScreen, options);
        this.layout.setHeaderHeight(48);
        this.layout.setFooterHeight(56);
    }
    
    @Override
    protected void init() {
        
        super.init();
        this.search.moveCursor(0, false);
    }
    
    @Override
    protected void addTitle() {
        
        int searchX = 340; // default net.minecraft.client.gui.screens.options.controls.KeyBindsList.getRowWidth
        int centerX = this.width / 2;
        Supplier<List<KeyBindsList.Entry>> listSupplier = () -> getCustomList().getAllEntries();
        this.search = new AutoCompletingEditBox<>(font, centerX - searchX / 2, 20, searchX, Button.DEFAULT_HEIGHT, search, Component.translatable("selectWorld.search"), ControllingConstants.SEARCHABLE_KEYBINDINGS, listSupplier);
        this.search.addResponder(this::filterKeys);
        
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical(), layoutSettings -> layoutSettings.paddingVertical(8));
        header.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        header.addChild(this.search, layoutSettings -> layoutSettings.paddingVertical(4));
        setInitialFocus(this.search);
    }
    
    @Override
    protected void addContents() {
        
        this.newKeyList = Suppliers.memoize(() -> new NewKeyBindsList(this, this.minecraft));
        this.freeKeyList = () -> new FreeKeysList(this, this.minecraft);
        // Don't call setKeyBindsList as we don't want to reposition elements right now
        getAccess().controlling$setKeyBindsList(showFree ? this.freeKeyList.get() : this.newKeyList.get());
        this.layout.addToContents(getKeyBindsList());
        displayMode = DisplayMode.ALL;
        // Trigger initial display of suggestions, needs to be after we set the keybinds list
        this.search.autoComplete().accept("");
    }
    
    @Override
    protected void addFooter() {
        
        int controlsWidth = 340;
        int controlSpacing = 4;
        int btnWidth = (controlsWidth - controlSpacing * 3) / 4;
        int actionSpacing = 8;
        int actionBtnWidth = (controlsWidth - actionSpacing) / 2;
        this.resetButton(Button.builder(confirmingReset.currentDisplay(), PRESS_RESET)
                .width(actionBtnWidth)
                .build());
        resetButton().active = canReset();
        
        Button toggleFreeButton = Button.builder(ControllingConstants.COMPONENT_OPTIONS_TOGGLE_FREE, PRESS_FREE)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        this.buttonSort = Button.builder(sortOrder.getDisplay(), PRESS_SORT)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        this.buttonDisplayMode = Button.builder(displayModeDisplay(), PRESS_DISPLAY_MODE)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        Button buttonExactMatch = Button.builder(exactMatch ?
                        ControllingConstants.COMPONENT_OPTIONS_EXACT_MATCH
                        : ControllingConstants.COMPONENT_OPTIONS_FUZZY_MATCH, PRESS_EXACT_MATCH)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        GridLayout grid = this.layout.addToFooter(new GridLayout());
        grid.rowSpacing(4);
        GridLayout.RowHelper rowHelper = grid.createRowHelper(1);
        LinearLayout filters = rowHelper.addChild(LinearLayout.horizontal());
        filters.spacing(controlSpacing);
        filters.addChild(toggleFreeButton);
        filters.addChild(this.buttonSort);
        filters.addChild(this.buttonDisplayMode);
        filters.addChild(buttonExactMatch);

        LinearLayout actions = rowHelper.addChild(LinearLayout.horizontal());
        actions.spacing(actionSpacing);
        actions.addChild(resetButton());
        actions.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose())
                .width(actionBtnWidth)
                .build());
    }
    
    @Override
    protected void repositionElements() {
        
        super.repositionElements();
        resetButton().active = canReset();
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.search.autoComplete().extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }
    
    public Button resetButton() {
        
        return this.getAccess().controlling$getResetButton();
    }
    
    public void resetButton(Button button) {
        
        this.getAccess().controlling$setResetButton(button);
    }
    
    public void filterKeys() {
        
        filterKeys(search.getValue());
    }
    
    public void filterKeys(String lastSearch) {
        
        CustomList list = getCustomList();
        
        list.clearEntries();
        getKeyBindsList().setScrollAmount(0);
        if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            for(KeyBindsList.Entry allEntry : getCustomList().getAllEntries()) {
                list.addEntryInternal(allEntry);
            }
            return;
        }
        
        Predicate<KeyBindsList.Entry> extraPredicate = _ -> true;
        Consumer<List<IKeyEntry>> postConsumer = _ -> {};
        
        
        if(list instanceof NewKeyBindsList) {
            extraPredicate = displayMode.getPredicate();
            postConsumer = entries -> {
                entries.removeIf(entry -> !(entry instanceof IKeyEntry));
                list.sort(sortOrder);
            };
        }
        List<KeyBindsList.Entry> entries = (exactMatch ? ControllingConstants.EXACT_SEARCHABLE_KEYBINDINGS : ControllingConstants.SEARCHABLE_KEYBINDINGS)
                .filterEntries(list.getAllEntries(), lastSearch, extraPredicate);
        for(KeyBindsList.Entry entry : entries) {
            list.addEntryInternal(entry);
        }
        
        postConsumer.accept(getAbstractSelectionList().controlling$getChildren());
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        
        boolean b = super.mouseClicked(event, doubleClick);
        if(!b && search.isFocused() && !search.autoComplete().mouseClicked(event, doubleClick)) {
            this.setFocused(null);
            clearFocus();
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        
        if(search.autoComplete().mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        
        if(!search.isFocused() && this.selectedKey == null) {
            if(event.hasControlDown() && event.key() == GLFW.GLFW_KEY_F) {
                search.setFocused(true);
                return true;
            }
        }
        if(search.isFocused()) {
            if(event.isEscape()) {
                search.setFocused(false);
                return true;
            }
        }
        if(this.selectedKey != null) {
            Services.PLATFORM.handleKeyPress(this, this.options, event);
            return true;
        } else {
            return super.keyPressed(event);
        }
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        
        if(Services.PLATFORM.handleKeyReleased(this, this.options, event)) {
            return true;
        }
        return super.keyReleased(event);
    }
    
    private CustomList getCustomList() {
        
        if(this.getKeyBindsList() instanceof CustomList cl) {
            return cl;
        }
        throw new IllegalStateException("keyBindsList('%s') was not an instance of CustomList! You're either too early or another mod is messing with things.".formatted(this.getKeyBindsList()
                .getClass()));
    }
    
    public KeyBindsList getKeyBindsList() {
        
        return getAccess().controlling$getKeyBindsList();
    }
    
    public AccessAbstractSelectionList getAbstractSelectionList() {
        
        return (AccessAbstractSelectionList) this.getKeyBindsList();
    }
    
    private void setKeyBindsList(KeyBindsList newList) {
        
        getAccess().controlling$setKeyBindsList(newList);
        repositionElements();
    }
    
    private AccessKeyBindsScreen getAccess() {
        
        return ((AccessKeyBindsScreen) this);
    }
    
    private boolean canReset() {
        
        for(KeyMapping key : this.options.keyMappings) {
            if(!key.isDefault()) {
                return true;
            }
        }
        return false;
    }

    private Component displayModeDisplay() {
        return switch(displayMode) {
            case ALL -> ControllingConstants.COMPONENT_OPTIONS_SHOW_ALL;
            case NONE -> ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE;
            case CONFLICTING -> ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS;
        };
    }
    
    private final Button.OnPress PRESS_RESET = btn -> {
        NewKeyBindsScreen screen = NewKeyBindsScreen.this;
        Minecraft minecraft = Objects.requireNonNull(screen.minecraft);
        
        if(!confirmingReset.toggle()) {
            for(KeyMapping keybinding : minecraft.options.keyMappings) {
                Services.PLATFORM.setToDefault(minecraft.options, keybinding);
            }
            
            getKeyBindsList().resetMappingAndUpdateButtons();
        }
        btn.setMessage(confirmingReset.currentDisplay());
    };
    
    private final Button.OnPress PRESS_DISPLAY_MODE = btn -> {
        displayMode = switch(displayMode) {
            case ALL -> DisplayMode.CONFLICTING;
            case CONFLICTING -> DisplayMode.NONE;
            case NONE -> DisplayMode.ALL;
        };
        btn.setMessage(displayModeDisplay());
        filterKeys();
    };
    
    private final Button.OnPress PRESS_SORT = btn -> {
        sortOrder = sortOrder.cycle();
        btn.setMessage(sortOrder.getDisplay());
        filterKeys();
    };
    
    private final Button.OnPress PRESS_FREE = _ -> {
        removeWidget(getKeyBindsList());
        if(showFree) {
            buttonSort.active = true;
            buttonDisplayMode.active = true;
            resetButton().active = canReset(); // Fixes
            setKeyBindsList(newKeyList.get());
        } else {
            freeKeyList.get().recalculate();
            buttonSort.active = false;
            buttonDisplayMode.active = false;
            resetButton().active = false;
            setKeyBindsList(freeKeyList.get());
        }
        filterKeys();
        addRenderableWidget(getKeyBindsList());
        setFocused(getKeyBindsList());
        showFree = !showFree;
    };
    
    private final Button.OnPress PRESS_EXACT_MATCH = btn -> {
        exactMatch = !exactMatch;
        btn.setMessage(exactMatch ?
                ControllingConstants.COMPONENT_OPTIONS_EXACT_MATCH
                : ControllingConstants.COMPONENT_OPTIONS_FUZZY_MATCH
        );
        filterKeys();
    };

}
