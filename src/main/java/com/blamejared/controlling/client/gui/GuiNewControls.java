package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.Controlling;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class GuiNewControls extends KeyBindsScreen {
    
    private Button buttonReset;
    private final Screen parentScreen;
    private final Options options;
    
    private String lastSearch;
    private EditBox search;
    
    private DisplayMode displayMode;
    private SearchType searchType;
    private SortOrder sortOrder;
    
    private Button buttonNone;
    private Button buttonConflicting;
    private GuiCheckBox buttonKey;
    private GuiCheckBox buttonCat;
    private Button buttonFree;
    private boolean confirmingReset = false;
    private boolean showFree = false;
    private String name;
    
    private KeyBindsList customKeyList;
    private GuiFreeKeysList freeKeyList;
    
    public GuiNewControls(Screen screen, Options settings) {
        
        super(screen, settings);
        this.parentScreen = screen;
        this.options = settings;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void init() {
        
        this.addRenderableWidget(new Button(this.width / 2 - 155, 18, 150, 20, new TranslatableComponent("options.mouse_settings"), (p_213126_1_) -> {
            this.minecraft.setScreen(new MouseSettingsScreen(this, options));
        }));
        this.addRenderableWidget(Option.AUTO_JUMP.createButton(this.minecraft.options, this.width / 2 - 155 + 160, 18, 150));
        customKeyList = new GuiNewKeyBindingList(this, this.minecraft);
        freeKeyList = new GuiFreeKeysList(this, this.minecraft);
        this.keyBindsList = customKeyList;
        this.addWidget(this.keyBindsList);
        this.setFocused(this.keyBindsList);
        this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, new TranslatableComponent("gui.done"), (p_213126_1_) -> GuiNewControls.this.minecraft
                .setScreen(GuiNewControls.this.parentScreen)));
        
        this.buttonReset = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 29, 74, 20, new TranslatableComponent("controls.resetAll"), (p_213126_1_) -> {
            
            if(!confirmingReset) {
                confirmingReset = true;
                p_213126_1_.setMessage(new TranslatableComponent("options.confirmReset"));
                return;
            }
            confirmingReset = false;
            p_213126_1_.setMessage(new TranslatableComponent("controls.resetAll"));
            for(KeyMapping keybinding : GuiNewControls.this.minecraft.options.keyMappings) {
                keybinding.setToDefault();
            }
            
            KeyMapping.releaseAll();
        }));
        this.buttonNone = this.addRenderableWidget(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, new TranslatableComponent("options.showNone"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.NONE) {
                buttonNone.setMessage(new TranslatableComponent("options.showNone"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                buttonNone.setMessage(new TranslatableComponent("options.showAll"));
                buttonConflicting.setMessage(new TranslatableComponent("options.showConflicts"));
            }
            filterKeys();
        }));
        this.buttonConflicting = this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, new TranslatableComponent("options.showConflicts"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.CONFLICTING) {
                buttonConflicting.setMessage(new TranslatableComponent("options.showConflicts"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                buttonConflicting.setMessage(new TranslatableComponent("options.showAll"));
                buttonNone.setMessage(new TranslatableComponent("options.showNone"));
            }
            filterKeys();
        }));
        search = new EditBox(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, new TextComponent(""));
        addWidget(search);
        this.buttonKey = this.addRenderableWidget(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 37, new TranslatableComponent("options.key")
                .getString(), false) {
            @Override
            public void onPress() {
                
                super.onPress();
                buttonCat.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.KEY : SearchType.NAME;
                filterKeys();
            }
        });
        this.buttonCat = this.addRenderableWidget(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 50, new TranslatableComponent("options.category")
                .getString(), false) {
            
            @Override
            public void onPress() {
                
                super.onPress();
                buttonKey.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
                filterKeys();
            }
        });
        name = Controlling.PATRON_LIST.stream()
                .skip(Controlling.PATRON_LIST.isEmpty() ? 0 : new Random().nextInt(Controlling.PATRON_LIST.size()))
                .findFirst()
                .orElse("");
        sortOrder = SortOrder.NONE;
        Button buttonSort = this.addRenderableWidget(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20, new TranslatableComponent("options.sort")
                .append(": " + sortOrder.getName()), (p_213126_1_) -> {
            sortOrder = sortOrder.cycle();
            p_213126_1_.setMessage(new TranslatableComponent("options.sort").append(": " + sortOrder.getName()));
            filterKeys();
        }));
        
        this.buttonFree = this.addRenderableWidget(new Button(this.width / 2 - 155 + 76, this.height - 29, 74, 20, new TranslatableComponent("options.toggleFree"), (p_213126_1_) -> {
            this.removeWidget(this.keyBindsList);
            if(showFree) {
                buttonSort.active = true;
                buttonCat.active = true;
                buttonKey.active = true;
                buttonNone.active = true;
                buttonConflicting.active = true;
                buttonReset.active = true;
                keyBindsList = customKeyList;
            } else {
                freeKeyList.recalculate();
                buttonSort.active = false;
                buttonCat.active = false;
                buttonKey.active = false;
                buttonNone.active = false;
                buttonConflicting.active = false;
                buttonReset.active = false;
                keyBindsList = freeKeyList;
            }
            this.addWidget(this.keyBindsList);
            this.setFocused(this.keyBindsList);
            showFree = !showFree;
        }));
        
        lastSearch = "";
        displayMode = DisplayMode.ALL;
        searchType = SearchType.NAME;
    }
    
    @Override
    public boolean charTyped(char var1, int var2) {
        
        return search.charTyped(var1, var2);
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
        keyBindsList.children().clear();
        if(keyBindsList instanceof GuiNewKeyBindingList) {
            
            if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
                keyBindsList.children().addAll(((GuiCustomList) keyBindsList).getAllEntries());
                return;
            }
            this.keyBindsList.setScrollAmount(0);
            Predicate<GuiNewKeyBindingList.KeyEntry> filters = displayMode.getPredicate();
            
            
            switch(searchType) {
                case NAME:
                    filters = filters.and(keyEntry -> keyEntry.getKeyDesc()
                            .toLowerCase()
                            .contains(lastSearch.toLowerCase()));
                    break;
                case CATEGORY:
                    filters = filters.and(keyEntry -> new TranslatableComponent(keyEntry.getKeybinding()
                            .getCategory()).getString().toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case KEY:
                    filters = filters.and(keyEntry -> keyEntry.getKeybinding()
                            .getTranslatedKeyMessage()
                            .getString()
                            .toLowerCase()
                            .contains(lastSearch.toLowerCase()));
                    break;
            }
            
            for(GuiNewKeyBindingList.Entry entry : ((GuiCustomList) keyBindsList).getAllEntries()) {
                if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                    if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
                        GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                        if(filters.test(keyEntry)) {
                            keyBindsList.children().add(entry);
                        }
                    } else {
                        keyBindsList.children().add(entry);
                    }
                } else {
                    if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
                        GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                        if(filters.test(keyEntry)) {
                            keyBindsList.children().add(entry);
                        }
                    }
                }
                
            }
            if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                Set<GuiNewKeyBindingList.CategoryEntry> categories = new LinkedHashSet<>();
                
                for(KeyBindsList.Entry entry : keyBindsList.children()) {
                    if(entry instanceof GuiNewKeyBindingList.CategoryEntry) {
                        GuiNewKeyBindingList.CategoryEntry centry = (GuiNewKeyBindingList.CategoryEntry) entry;
                        categories.add(centry);
                        for(KeyBindsList.Entry child : keyBindsList.children()) {
                            if(child instanceof GuiNewKeyBindingList.KeyEntry) {
                                GuiNewKeyBindingList.KeyEntry childEntry = (GuiNewKeyBindingList.KeyEntry) child;
                                if(childEntry.getKeybinding().getCategory().equals(centry.getName())) {
                                    categories.remove(centry);
                                }
                            }
                        }
                    }
                }
                keyBindsList.children().removeAll(categories);
            }
            sortOrder.sort(keyBindsList.children());
            
        } else if(keyBindsList instanceof GuiFreeKeysList) {
            if(lastSearch.isEmpty()) {
                keyBindsList.children().addAll(((GuiCustomList) keyBindsList).getAllEntries());
                return;
            }
            this.keyBindsList.setScrollAmount(0);
            
            for(GuiFreeKeysList.Entry entry : ((GuiCustomList) keyBindsList).getAllEntries()) {
                if(entry instanceof GuiFreeKeysList.InputEntry) {
                    GuiFreeKeysList.InputEntry inputEntry = (GuiFreeKeysList.InputEntry) entry;
                    if(inputEntry.getInput().toString().toLowerCase().contains(lastSearch.toLowerCase())) {
                        keyBindsList.children().add(entry);
                    }
                } else {
                    keyBindsList.children().add(entry);
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
        this.keyBindsList.render(stack, mouseX, mouseY, partialTicks);
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
            buttonReset.setMessage(new TranslatableComponent("controls.resetAll"));
        }
        
        
        Component text = new TranslatableComponent("options.search");
        font.draw(stack, text, this.width / 2f - (155 / 2f) - (font.width(text.getString())) - 5, this.height - 29 - 42, 16777215);
        
        for(Widget widget : this.renderables) {
            widget.render(stack, mouseX, mouseY, partialTicks);
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
        } else if(mb == 0 && this.keyBindsList.mouseClicked(mx, my, mb)) {
            this.setDragging(true);
            this.setFocused(this.keyBindsList);
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
            
            for(GuiEventListener iguieventlistener : this.children()) {
                if(iguieventlistener.mouseClicked(mx, my, mb)) {
                    this.setFocused(iguieventlistener);
                    if(mb == 0) {
                        this.setDragging(true);
                    }
                    
                    return true;
                }
            }
            
            valid = true;
        }
        
        
        return valid;
    }
    
    @Override
    public boolean mouseReleased(double mx, double my, int mb) {
        
        if(mb == 0 && this.keyBindsList.mouseReleased(mx, my, mb)) {
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
                this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputConstants.UNKNOWN);
                this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
            } else {
                this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputConstants
                        .getKey(keyCode, scanCode));
                this.options.setKey(this.selectedKey, InputConstants.getKey(keyCode, scanCode));
            }
            
            if(!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey())) {
                this.selectedKey = null;
            }
            this.lastKeySelection = Util.getMillis();
            KeyMapping.resetMapping();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifier);
        }
    }
    
    
}