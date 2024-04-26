package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.SortOrder;
import com.blamejared.controlling.mixin.AccessKeyBindsScreen;
import com.blamejared.controlling.mixin.AccessKeyMapping;
import com.blamejared.controlling.platform.Services;
import com.blamejared.searchables.api.autcomplete.AutoCompletingEditBox;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class NewKeyBindsScreen extends KeyBindsScreen {
    
    private AutoCompletingEditBox<KeyBindsList.Entry> search;
    private DisplayMode displayMode;
    private SortOrder sortOrder = SortOrder.NONE;
    private Button buttonNone;
    private Button buttonConflicting;
    private Button buttonSort;
    private Button toggleFreeButton;
    private final DisplayableBoolean confirmingReset = new DisplayableBoolean(false, ControllingConstants.COMPONENT_OPTIONS_CONFIRM_RESET, ControllingConstants.COMPONENT_CONTROLS_RESET_ALL);
    private boolean showFree;
    private Supplier<NewKeyBindsList> newKeyList;
    private Supplier<FreeKeysList> freeKeyList;
    
    public NewKeyBindsScreen(Screen screen, Options settings) {
        
        super(screen, settings);
    }
    
    @ApiStatus.Internal
    public void hijackInit() {
        
        this.resetButton(Button.builder(confirmingReset.currentDisplay(), PRESS_RESET)
                .build());
        resetButton().active = canReset();
        
        layout.setHeaderHeight(48);
        layout.setFooterHeight(56);
        
        int searchX = getKeyBindsList().getRowWidth();
        int btnWidth = Button.DEFAULT_WIDTH / 2 - 1;
        int centerX = this.width / 2;
        
        Supplier<List<KeyBindsList.Entry>> listSupplier = () -> getCustomList().getAllEntries();
        this.search = new AutoCompletingEditBox<>(font, centerX - searchX / 2, 20, searchX, Button.DEFAULT_HEIGHT, search, Component.translatable("selectWorld.search"), ControllingConstants.SEARCHABLE_KEYBINDINGS, listSupplier);
        this.search.addResponder(this::filterKeys);
        
        this.toggleFreeButton = Button.builder(ControllingConstants.COMPONENT_OPTIONS_TOGGLE_FREE, PRESS_FREE)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        this.buttonSort = Button.builder(sortOrder.getDisplay(), PRESS_SORT)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        this.buttonNone = Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE, PRESS_NONE)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        this.buttonConflicting = Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS, PRESS_CONFLICTING)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();
        
        displayMode = DisplayMode.ALL;
        setInitialFocus(this.search);
        // Trigger an initial auto complete
        this.search.moveCursor(0, false);
        
    }
    
    @Override
    protected void repositionElements() {
        
        super.repositionElements();
        resetButton().active = canReset();
        // Since we insert elements above the keybinding list, we need to
        // sort the elements to ensure that tab navigation works as intended.
        this.children()
                .sort(Comparator.comparingInt((ToIntFunction<GuiEventListener>) value -> value.getRectangle().top())
                        .thenComparingInt(listener -> listener.getRectangle().left()));
    }
    
    public KeyBindsList hijackKeyBindsList() {
        
        this.newKeyList = Suppliers.memoize(() -> new NewKeyBindsList(this, this.minecraft));
        this.freeKeyList = Suppliers.memoize(() -> new FreeKeysList(this, this.minecraft));
        return showFree ? this.freeKeyList.get() : this.newKeyList.get();
    }
    
    
    @Override
    protected void addTitle() {
        
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical(), layoutSettings -> layoutSettings.paddingVertical(8));
        header.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        header.addChild(this.search, layoutSettings -> layoutSettings.paddingVertical(4));
    }
    
    @Override
    protected void addFooter() {
        
        GridLayout grid = this.layout.addToFooter(new GridLayout());
        grid.rowSpacing(4);
        grid.columnSpacing(8);
        GridLayout.RowHelper rowHelper = grid.createRowHelper(2);
        LinearLayout topLeft = rowHelper.addChild(LinearLayout.horizontal());
        topLeft.spacing(4);
        topLeft.addChild(this.toggleFreeButton);
        topLeft.addChild(this.buttonSort);
        
        LinearLayout topRight = rowHelper.addChild(LinearLayout.horizontal());
        topRight.spacing(4);
        topRight.addChild(this.buttonNone);
        topRight.addChild(this.buttonConflicting);
        
        rowHelper.addChild(resetButton());
        rowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, $$0x -> this.onClose()).build());
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mxPos, int myPos, float partialTicks) {
        
        super.render(guiGraphics, mxPos, myPos, partialTicks);
        this.search.autoComplete().render(guiGraphics, mxPos, myPos, partialTicks);
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
        
        getKeyBindsList().children().clear();
        getKeyBindsList().setScrollAmount(0);
        if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            getKeyBindsList().children().addAll(getCustomList().getAllEntries());
            return;
        }
        
        Predicate<KeyBindsList.Entry> extraPredicate = entry -> true;
        Consumer<List<KeyBindsList.Entry>> postConsumer = entries -> {};
        CustomList list = getCustomList();
        
        if(list instanceof NewKeyBindsList) {
            extraPredicate = displayMode.getPredicate();
            postConsumer = entries -> sortOrder.sort(entries);
        }
        list.children()
                .addAll(ControllingConstants.SEARCHABLE_KEYBINDINGS.filterEntries(list.getAllEntries(), lastSearch, extraPredicate));
        postConsumer.accept(list.children());
    }
    
    @Override
    public boolean mouseClicked(double xpos, double ypos, int buttonId) {
        
        boolean b = super.mouseClicked(xpos, ypos, buttonId);
        if(!b && search.isFocused() && !search.autoComplete().mouseClicked(xpos, ypos, buttonId)) {
            search.setFocused(false);
            clearFocus();
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean mouseScrolled(double xpos, double ypos, double xDelta, double yDelta) {
        
        if(search.autoComplete().mouseScrolled(xpos, ypos, xDelta, yDelta)) {
            return true;
        }
        return super.mouseScrolled(xpos, ypos, xDelta, yDelta);
    }
    
    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        
        if(!search.isFocused() && this.selectedKey == null) {
            if(hasControlDown()) {
                if(InputConstants.isKeyDown(Minecraft.getInstance()
                        .getWindow()
                        .getWindow(), GLFW.GLFW_KEY_F)) {
                    search.setFocused(true);
                    return true;
                }
            }
        }
        if(search.isFocused()) {
            if(key == GLFW.GLFW_KEY_ESCAPE) {
                search.setFocused(false);
                return true;
            }
        }
        if(this.selectedKey != null) {
            if(key == 256) {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.UNKNOWN);
            } else {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.getKey(key, scancode));
            }
            if(!Services.PLATFORM.isKeyCodeModifier(((AccessKeyMapping) this.selectedKey).controlling$getKey())) {
                this.selectedKey = null;
            }
            this.lastKeySelection = Util.getMillis();
            this.getKeyBindsList().resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.keyPressed(key, scancode, mods);
        }
    }
    
    private CustomList getCustomList() {
        
        if(this.getKeyBindsList() instanceof CustomList cl) {
            return cl;
        }
        throw new IllegalStateException("keyBindsList('%s') was not an instance of CustomList! You're either too early or another mod is messing with things.".formatted(this.getKeyBindsList()
                .getClass()));
    }
    
    private KeyBindsList getKeyBindsList() {
        
        return getAccess().controlling$getKeyBindsList();
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
    
    private final Button.OnPress PRESS_NONE = btn -> {
        if(displayMode == DisplayMode.NONE) {
            buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE);
            displayMode = DisplayMode.ALL;
        } else {
            displayMode = DisplayMode.NONE;
            buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_ALL);
            buttonConflicting.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS);
        }
        filterKeys();
    };
    
    private final Button.OnPress PRESS_SORT = btn -> {
        sortOrder = sortOrder.cycle();
        btn.setMessage(sortOrder.getDisplay());
        filterKeys();
    };
    
    private final Button.OnPress PRESS_CONFLICTING = btn -> {
        if(displayMode == DisplayMode.CONFLICTING) {
            buttonConflicting.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS);
            displayMode = DisplayMode.ALL;
        } else {
            displayMode = DisplayMode.CONFLICTING;
            buttonConflicting.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_ALL);
            buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE);
        }
        filterKeys();
    };
    
    private final Button.OnPress PRESS_FREE = btn -> {
        removeWidget(getKeyBindsList());
        if(showFree) {
            buttonSort.active = true;
            buttonNone.active = true;
            buttonConflicting.active = true;
            resetButton().active = canReset(); // Fixes
            setKeyBindsList(newKeyList.get());
        } else {
            freeKeyList.get().recalculate();
            buttonSort.active = false;
            buttonNone.active = false;
            buttonConflicting.active = false;
            resetButton().active = false;
            setKeyBindsList(freeKeyList.get());
        }
        filterKeys();
        addRenderableWidget(getKeyBindsList());
        setFocused(getKeyBindsList());
        showFree = !showFree;
    };
    
}