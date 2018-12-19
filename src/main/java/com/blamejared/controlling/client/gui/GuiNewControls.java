package com.blamejared.controlling.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.util.Iterator;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class GuiNewControls extends GuiControls {
    
    private static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
    
    private GuiNewKeyBindingList keyBindingList;
    private GuiButton buttonReset;
    private final GuiScreen parentScreen;
    private final GameSettings options;
    
    private String lastSearch;
    private GuiTextField search;
    
    private DisplayMode displayMode;
    private SearchType searchType;
    private SortOrder sortOrder;
    
    private GuiButton buttonNone;
    private GuiButton buttonConflicting;
    private GuiCheckBox buttonKey;
    private GuiCheckBox buttonCat;
    
    private GuiButton buttonSort;
    
    public GuiNewControls(GuiScreen screen, GameSettings settings) {
        super(screen, settings);
        this.parentScreen = screen;
        this.options = settings;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void initGui() {
        this.keyBindingList = new GuiNewKeyBindingList(this, this.mc);
        this.children.add(this.keyBindingList);
        this.setFocused(this.keyBindingList);
        this.addButton(new GuiButton(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
                GuiNewControls.this.mc.displayGuiScreen(GuiNewControls.this.parentScreen);
            }
        });
        this.buttonReset = this.addButton(new GuiButton(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
                for(KeyBinding keybinding : GuiNewControls.this.mc.gameSettings.keyBindings) {
                    keybinding.setToDefault();
                }
                
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        });
        this.buttonNone = this.addButton(new GuiButton(2907, this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, "Show Unbound"/*I18n.translate("options.showNone")*/) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                if(displayMode == DisplayMode.NONE) {
                    buttonNone.displayString = I18n.format("options.showNone");
                    displayMode = DisplayMode.ALL;
                } else {
                    displayMode = DisplayMode.NONE;
                    buttonNone.displayString = I18n.format("options.showAll");
                    buttonConflicting.displayString = I18n.format("options.showConflicts");
                }
                filterKeys();
            }
        });
        this.buttonConflicting = this.addButton(new GuiButton(2906, this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, "Show Conflicts"/*I18n.translate("options.showConflicts")*/) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                if(displayMode == DisplayMode.CONFLICTING) {
                    buttonConflicting.displayString = I18n.format("options.showConflicts");
                    displayMode = DisplayMode.ALL;
                } else {
                    displayMode = DisplayMode.CONFLICTING;
                    buttonConflicting.displayString = "Show All";
                    buttonNone.displayString = I18n.format("options.showNone");
                }
                filterKeys();
            }
        });
        search = new GuiTextField(0, fontRenderer, this.width / 2 - 154, this.height - 29 - 23, 148, 18);
        this.buttonKey = this.addButton(new GuiCheckBox(2908, this.width / 2 - (155 / 2) + 20, this.height - 29 - 37, I18n.format("options.key"), false) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                buttonCat.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.KEY : SearchType.NAME;
                filterKeys();
            }
        });
        this.buttonCat = this.addButton(new GuiCheckBox(2909, this.width / 2 - (155 / 2) + 20, this.height - 29 - 50, I18n.format("options.category"), false) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                buttonKey.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
                filterKeys();
            }
        });
        sortOrder = SortOrder.NONE;
        this.buttonSort = this.addButton(new GuiButton(2910, this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20, I18n.format("options.sort") + ": " + sortOrder.getName()) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                sortOrder = sortOrder.cycle();
                this.displayString = I18n.format("options.sort") + ": " + sortOrder.getName();
                filterKeys();
            }
        });
        this.screenTitle = I18n.format("controls.title");
        int i = 0;
        
        for(GameSettings.Options gamesettings$options : GuiNewControls.OPTIONS_ARR) {
            if(gamesettings$options.isFloat()) {
                this.addButton(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
            } else {
                this.addButton(new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)) {
                    public void onClick(double mouseX, double mouseY) {
                        GuiNewControls.this.options.setOptionValue(this.getOption(), 1);
                        this.displayString = GuiNewControls.this.options.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
                    }
                });
            }
            
            ++i;
        }
        
        
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
        keyBindingList.getChildren().clear();
        if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            keyBindingList.getChildren().addAll(keyBindingList.getAllEntries());
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
                filters = filters.and(keyEntry -> keyEntry.getKeybinding().getKey().getName().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
        }
        
        for(GuiNewKeyBindingList.Entry entry : keyBindingList.getAllEntries()) {
            if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
                GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                if(filters.test(keyEntry)) {
                    keyBindingList.getChildren().add(entry);
                }
            }
        }
        sortOrder.sort(keyBindingList.getChildren());
        
        
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 16777215);
        boolean flag = false;
        
        for(KeyBinding keybinding : this.options.keyBindings) {
            if(!keybinding.func_197985_l()) {
                flag = true;
                break;
            }
        }
        search.drawTextField(mouseX, mouseY, partialTicks);
        this.buttonReset.enabled = flag;
        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(mouseX, mouseY, partialTicks);
        }
        
        for(int j = 0; j < this.labels.size(); ++j) {
            this.labels.get(j).render(mouseX, mouseY, partialTicks);
            
        }
        String text = I18n.format("options.search");
        fontRenderer.drawString(text, this.width / 2 - (155 / 2) - (fontRenderer.getStringWidth(text) / 2), this.height - 29 - 39, 16777215);
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
            
            Iterator childIter = this.getChildren().iterator();
            
            IGuiEventListener listener;
            boolean clicked;
            do
            {
                if(!childIter.hasNext()) {
                    return false;
                }
                
                listener = (IGuiEventListener) childIter.next();
                clicked = listener.mouseClicked(mx, my, mb);
            } while(!clicked);
            
            this.focusOn(listener);
            if(mb == 0) {
                this.setDragging(true);
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