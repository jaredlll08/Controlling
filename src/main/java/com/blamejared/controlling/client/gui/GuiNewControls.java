package com.blamejared.controlling.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.gui.widget.button.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.*;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.*;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class GuiNewControls extends ControlsScreen {
    
    private GuiNewKeyBindingList keyBindingList;
    private Button buttonReset;
    private final Screen parentScreen;
    private final GameSettings options;
    
    private String lastSearch;
    private TextFieldWidget search;
    
    private DisplayMode displayMode;
    private SearchType searchType;
    private SortOrder sortOrder;
    
    private Button buttonNone;
    private Button buttonConflicting;
    private GuiCheckBox buttonKey;
    private GuiCheckBox buttonCat;
    
    private Button buttonSort;
    
    public GuiNewControls(Screen screen, GameSettings settings) {
        super(screen, settings);
        this.parentScreen = screen;
        this.options = settings;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void init() {
        
        this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, I18n.format("options.mouse_settings"), (p_213126_1_) -> {
            this.minecraft.displayGuiScreen(new MouseSettingsScreen(this));
        }));
        this.addButton(AbstractOption.field_216719_z.func_216586_a(this.minecraft.gameSettings, this.width / 2 - 155 + 160, 18, 150));
        
        this.keyBindingList = new GuiNewKeyBindingList(this, this.minecraft);
        this.children.add(this.keyBindingList);
        this.setFocused(this.keyBindingList);
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done"), (p_213126_1_) -> GuiNewControls.this.minecraft.displayGuiScreen(GuiNewControls.this.parentScreen)));
        
        this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll"), (p_213126_1_) -> {
            
            for(KeyBinding keybinding : GuiNewControls.this.minecraft.gameSettings.keyBindings) {
                keybinding.setToDefault();
            }
            
            KeyBinding.resetKeyBindingArrayAndHash();
        }));
        this.buttonNone = this.addButton(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, I18n.format("options.showNone"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.NONE) {
                buttonNone.setMessage(I18n.format("options.showNone"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                buttonNone.setMessage(I18n.format("options.showAll"));
                buttonConflicting.setMessage(I18n.format("options.showConflicts"));
            }
            filterKeys();
        }));
        this.buttonConflicting = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, I18n.format("options.showConflicts"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.CONFLICTING) {
                buttonConflicting.setMessage(I18n.format("options.showConflicts"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                buttonConflicting.setMessage("Show All");
                buttonNone.setMessage(I18n.format("options.showNone"));
            }
            filterKeys();
        }));
        search = new TextFieldWidget(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, "");
        this.buttonKey = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2) + 20, this.height - 29 - 37, I18n.format("options.key"), false) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                buttonCat.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.KEY : SearchType.NAME;
                filterKeys();
            }
        });
        this.buttonCat = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2) + 20, this.height - 29 - 50, I18n.format("options.category"), false) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                buttonKey.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
                filterKeys();
            }
        });
        sortOrder = SortOrder.NONE;
        this.buttonSort = this.addButton(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20, I18n.format("options.sort") + ": " + sortOrder.getName(), (p_213126_1_) -> {
            sortOrder = sortOrder.cycle();
            p_213126_1_.setMessage(I18n.format("options.sort") + ": " + sortOrder.getName());
            filterKeys();
        }));
        lastSearch = "";
        displayMode = DisplayMode.ALL;
        searchType = SearchType.NAME;
        
    }
    
    @Override
    public boolean charTyped(char var1, int var2) {
        return search.charTyped(var1, var2);
    }
    
    public void tick() {
        this.search.tick();
        if(!lastSearch.equals(search.getText())) {
            filterKeys();
        }
    }
    
    public void filterKeys() {
        lastSearch = search.getText();
        keyBindingList.children().clear();
        if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            keyBindingList.children().addAll(keyBindingList.getAllEntries());
            return;
        }
        Predicate<GuiNewKeyBindingList.KeyEntry> filters = displayMode.getPredicate();
        
        
        switch(searchType) {
            case NAME:
                filters = filters.and(keyEntry -> keyEntry.getKeyDesc().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
            case CATEGORY:
                filters = filters.and(keyEntry -> keyEntry.getKeybinding().getKeyCategory().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
            case KEY:
                filters = filters.and(keyEntry -> keyEntry.getKeybinding().getKey().getTranslationKey().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
        }
        
        for(GuiNewKeyBindingList.Entry entry : keyBindingList.getAllEntries()) {
            if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
                GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                if(filters.test(keyEntry)) {
                    keyBindingList.children().add(entry);
                }
            }
        }
        sortOrder.sort(keyBindingList.children());
        
        
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.keyBindingList.render(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        boolean flag = false;
        
        for(KeyBinding keybinding : this.options.keyBindings) {
            if(!keybinding.isDefault()) {
                flag = true;
                break;
            }
        }
        search.render(mouseX, mouseY, partialTicks);
        this.buttonReset.active = flag;
        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(mouseX, mouseY, partialTicks);
        }
        
        String text = I18n.format("options.search");
        font.drawString(text, this.width / 2 - (155 / 2) - (font.getStringWidth(text) / 2), this.height - 29 - 39, 16777215);
    }
    
    public boolean mouseClicked(double mx, double my, int mb) {
        boolean valid;
        if(this.buttonId != null) {
            this.options.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(mb));
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
            valid = true;
            search.setFocused(false);
        } else if(mb == 0 && this.keyBindingList.mouseClicked(mx, my, mb)) {
            this.setDragging(true);
            this.setFocused(this.keyBindingList);
            valid = true;
            search.setFocused(false);
        } else {
            valid = search.mouseClicked(mx, my, mb);
            if(!valid && search.isFocused() && mb == 1) {
                search.setText("");
                valid = true;
            }
        }
        
        if(!valid) {
            
            for(IGuiEventListener iguieventlistener : this.children()) {
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
    
    public boolean mouseReleased(double mx, double my, int mb) {
        if(mb == 0 && this.keyBindingList.mouseReleased(mx, my, mb)) {
            this.setDragging(false);
            return true;
        } else if(search.isFocused()) {
            return search.mouseReleased(mx, my, mb);
        } else {
            this.setDragging(false);
            return false;
        }
    }
    
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if(search.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        if(this.buttonId != null) {
            if(p_keyPressed_1_ == 256) {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.INPUT_INVALID);
                this.options.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
            } else {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
                this.options.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
            }
            
            if(!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.buttonId.getKey()))
                this.buttonId = null;
            this.time = Util.milliTime();
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
    
    
}