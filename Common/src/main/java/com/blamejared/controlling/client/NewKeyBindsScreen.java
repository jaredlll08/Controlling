package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.*;
import com.blamejared.controlling.mixin.*;
import com.blamejared.controlling.platform.Services;
import com.blamejared.searchables.api.autcomplete.AutoCompletingEditBox;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.*;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.*;

public class NewKeyBindsScreen extends KeyBindsScreen {
    
    
    private AutoCompletingEditBox<KeyBindsList.Entry> search;
    private DisplayMode displayMode;
    private SortOrder sortOrder = SortOrder.NONE;
    private Button buttonNone;
    private Button buttonConflicting;
    private Button buttonSort;
    private final DisplayableBoolean confirmingReset = new DisplayableBoolean(false, ControllingConstants.COMPONENT_OPTIONS_CONFIRM_RESET, ControllingConstants.COMPONENT_CONTROLS_RESET_ALL);
    private boolean showFree;
    private Supplier<NewKeyBindsList> newKeyList;
    private Supplier<FreeKeysList> freeKeyList;
    
    public NewKeyBindsScreen(Screen screen, Options settings) {
        
        super(screen, settings);
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void init() {
        
        super.init();
        int searchX = getKeyBindsList().getRowWidth();
        int btnWidth = Button.DEFAULT_WIDTH / 2 - 1;
        int groupPadding = 5;
        int centerX = this.width / 2;
        int leftX = centerX - Button.DEFAULT_WIDTH - groupPadding;
        int rightX = centerX + groupPadding;
        
        int bottomY = this.height - 29;
        int rowSpacing = 24;
        int topRowY = bottomY - rowSpacing;
        
        Supplier<List<KeyBindsList.Entry>> listSupplier = () -> getCustomList().getAllEntries();
        this.search = addRenderableWidget(new AutoCompletingEditBox<>(font, centerX - searchX / 2, 22, searchX, Button.DEFAULT_HEIGHT, search, Component.translatable("selectWorld.search"), ControllingConstants.SEARCHABLE_KEYBINDINGS, listSupplier));
        this.search.addResponder(this::filterKeys);
        this.addRenderableOnly(this.search.autoComplete());
        
        this.newKeyList = Suppliers.memoize(() -> new NewKeyBindsList(this, this.minecraft));
        this.freeKeyList = Suppliers.memoize(() -> new FreeKeysList(this, this.minecraft));
        this.removeWidget(getKeyBindsList());
        this.setKeyBindsList(showFree ? this.freeKeyList.get() : this.newKeyList.get());
        this.addWidget(getKeyBindsList());
        
        this.removeWidget(resetButton());
        this.resetButton(addRenderableWidget(Button.builder(confirmingReset.currentDisplay(), PRESS_RESET)
                .bounds(leftX, bottomY, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                .build()));
        
        addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_TOGGLE_FREE, PRESS_FREE)
                .bounds(leftX, topRowY, btnWidth, Button.DEFAULT_HEIGHT)
                .build());
        
        this.buttonSort = addRenderableWidget(Button.builder(sortOrder.getDisplay(), PRESS_SORT)
                .bounds(leftX + btnWidth + 2, topRowY, btnWidth, Button.DEFAULT_HEIGHT)
                .build());
        
        this.buttonNone = addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE, PRESS_NONE)
                .bounds(rightX, topRowY, btnWidth, Button.DEFAULT_HEIGHT)
                .build());
        
        this.buttonConflicting = addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_CONFLICTS, PRESS_CONFLICTING)
                .bounds(rightX + btnWidth + 2, topRowY, btnWidth, Button.DEFAULT_HEIGHT)
                .build());
        
        displayMode = DisplayMode.ALL;
        setInitialFocus(this.search);
        // Trigger an initial auto complete
        this.search.moveCursor(0);
        // This is so dumb, but it works.
        // The only reason this is needed is that we don't replace the vanilla "Done" button.
        this.children()
                .sort(Comparator.comparingInt((ToIntFunction<GuiEventListener>) value -> value instanceof AbstractWidget aw ? aw.getY() : 0)
                        .thenComparingInt(value -> value instanceof AbstractWidget aw ? aw.getX() : 0));
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
    public boolean charTyped(char character, int code) {
        
        return search.charTyped(character, code);
    }
    
    @Override
    public void tick() {
        
        this.search.tick();
    }
//
//    /**
//     * Draws the screen and all the components in it.
//     */
//    @Override
//    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
//
//        this.renderBackground(stack);
//        getKeyBindsList().render(stack, mouseX, mouseY, partialTicks);
//        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 16777215);
//        boolean flag = false;
//
//        if(!showFree) {
//            for(KeyMapping keybinding : this.options.keyMappings) {
//                if(!keybinding.isDefault()) {
//                    flag = true;
//                    break;
//                }
//            }
//        }
//        search.render(stack, mouseX, mouseY, partialTicks);
//        this.buttonReset.active = flag;
//        if(!flag) {
//            confirmingReset = false;
//            buttonReset.setMessage(ControllingConstants.COMPONENT_CONTROLS_RESET_ALL);
//        }
//
//        font.draw(stack, ControllingConstants.COMPONENT_OPTIONS_SEARCH, this.width / 2f - (155 / 2f) - (font.width(ControllingConstants.COMPONENT_OPTIONS_SEARCH.getString())) - 5, this.height - 29 - 42, 16777215);
//
//        for(Renderable renderable : getScreenAccess().controlling$getRenderables()) {
//            renderable.render(stack, mouseX, mouseY, partialTicks);
//        }
//    }
    
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
    public boolean mouseScrolled(double xpos, double ypos, double delta) {
        
        if(search.autoComplete().mouseScrolled(xpos, ypos, delta)) {
            return true;
        }
        return super.mouseScrolled(xpos, ypos, delta);
    }
    
    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        
        if(!search.isFocused() && this.selectedKey == null) {
            if(hasControlDown()) {
                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_F)) {
                    search.setFocus(true);
                    return true;
                }
            }
        }
        if(search.isFocused()) {
            if(key == GLFW.GLFW_KEY_ESCAPE) {
                search.setFocus(false);
                return true;
            }
        }
        if(search.isFocused()) {
            if(key == 256) {
                search.setFocus(false);
                return true;
            }
        }
        if(this.selectedKey != null) {
            if(key == GLFW.GLFW_KEY_ESCAPE) {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.UNKNOWN);
            } else {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.getKey(key, scancode));
            }
            if(!Services.PLATFORM.isKeyCodeModifier(((AccessKeyMapping) this.selectedKey).controlling$getKey())) {
                this.selectedKey = null;
            }
            this.lastKeySelection = Util.getMillis();
            KeyMapping.resetMapping();
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
    }
    
    private AccessScreen getScreenAccess() {
        
        return ((AccessScreen) this);
    }
    
    private AccessKeyBindsScreen getAccess() {
        
        return ((AccessKeyBindsScreen) this);
    }
    
    
    private final Button.OnPress PRESS_RESET = btn -> {
        NewKeyBindsScreen screen = NewKeyBindsScreen.this;
        Minecraft minecraft = Objects.requireNonNull(screen.minecraft);
        
        if(!confirmingReset.toggle()) {
            for(KeyMapping keybinding : minecraft.options.keyMappings) {
                Services.PLATFORM.setToDefault(minecraft.options, keybinding);
            }
            
            for(KeyMapping keybinding : Objects.requireNonNull(minecraft).options.keyMappings) {
                Services.PLATFORM.setToDefault(minecraft.options, keybinding);
                KeyMapping.resetMapping();
            }
            KeyMapping.releaseAll();
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
            resetButton().active = true;
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
        addWidget(getKeyBindsList());
        setFocused(getKeyBindsList());
        showFree = !showFree;
    };
    
}