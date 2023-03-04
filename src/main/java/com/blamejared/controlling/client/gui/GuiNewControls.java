package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.Controlling;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.*;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class GuiNewControls extends ControlsScreen {
    
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
    private Button buttonFree;
    private boolean confirmingReset = false;
    private boolean showFree = false;
    
    private KeyBindingList customKeyList;
    private GuiFreeKeysList freeKeyList;
    
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
        
        this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, new TranslationTextComponent("options.mouse_settings"), (p_213126_1_) -> {
            this.minecraft.displayGuiScreen(new MouseSettingsScreen(this, options));
        }));
        this.addButton(AbstractOption.AUTO_JUMP.createWidget(this.minecraft.gameSettings, this.width / 2 - 155 + 160, 18, 150));
        customKeyList = new GuiNewKeyBindingList(this, this.minecraft);
        freeKeyList = new GuiFreeKeysList(this, this.minecraft);
        this.keyBindingList = customKeyList;
        this.children.add(this.keyBindingList);
        this.setListener(this.keyBindingList);
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, new TranslationTextComponent("gui.done"), (p_213126_1_) -> GuiNewControls.this.minecraft.displayGuiScreen(GuiNewControls.this.parentScreen)));
        
        this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 74, 20, new TranslationTextComponent("controls.resetAll"), (p_213126_1_) -> {
            
            if(!confirmingReset) {
                confirmingReset = true;
                p_213126_1_.setMessage(new TranslationTextComponent("options.confirmReset"));
                return;
            }
            confirmingReset = false;
            p_213126_1_.setMessage(new TranslationTextComponent("controls.resetAll"));
            for(KeyBinding keybinding : GuiNewControls.this.minecraft.gameSettings.keyBindings) {
                keybinding.setToDefault();
            }
            
            KeyBinding.resetKeyBindingArrayAndHash();
        }));
        this.buttonNone = this.addButton(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, new TranslationTextComponent("options.showNone"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.NONE) {
                buttonNone.setMessage(new TranslationTextComponent("options.showNone"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                buttonNone.setMessage(new TranslationTextComponent("options.showAll"));
                buttonConflicting.setMessage(new TranslationTextComponent("options.showConflicts"));
            }
            filterKeys();
        }));
        this.buttonConflicting = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, new TranslationTextComponent("options.showConflicts"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.CONFLICTING) {
                buttonConflicting.setMessage(new TranslationTextComponent("options.showConflicts"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                buttonConflicting.setMessage(new TranslationTextComponent("options.showAll"));
                buttonNone.setMessage(new TranslationTextComponent("options.showNone"));
            }
            filterKeys();
        }));
        search = new TextFieldWidget(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, new StringTextComponent(""));
        this.buttonKey = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 37, new TranslationTextComponent("options.key").getString(), false) {
            @Override
            public void onPress() {
                
                super.onPress();
                buttonCat.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.KEY : SearchType.NAME;
                filterKeys();
            }
        });
        this.buttonCat = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 50, new TranslationTextComponent("options.category").getString(), false) {
            
            @Override
            public void onPress() {
                
                super.onPress();
                buttonKey.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
                filterKeys();
            }
        });
        sortOrder = SortOrder.NONE;
        Button buttonSort = this.addButton(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20, new TranslationTextComponent("options.sort").appendString(": " + sortOrder.getName()), (p_213126_1_) -> {
            sortOrder = sortOrder.cycle();
            p_213126_1_.setMessage(new TranslationTextComponent("options.sort").appendString(": " + sortOrder.getName()));
            filterKeys();
        }));
        
        this.buttonFree = this.addButton(new Button(this.width / 2 - 155 + 76, this.height - 29, 74, 20, new TranslationTextComponent("options.toggleFree"), (p_213126_1_) -> {
            this.children.remove(this.keyBindingList);
            if(showFree) {
                buttonSort.active = true;
                buttonCat.active = true;
                buttonKey.active = true;
                buttonNone.active = true;
                buttonConflicting.active = true;
                buttonReset.active = true;
                keyBindingList = customKeyList;
            } else {
                freeKeyList.recalculate();
                buttonSort.active = false;
                buttonCat.active = false;
                buttonKey.active = false;
                buttonNone.active = false;
                buttonConflicting.active = false;
                buttonReset.active = false;
                keyBindingList = freeKeyList;
            }
            this.children.add(this.keyBindingList);
            this.setListener(this.keyBindingList);
            showFree = !showFree;
        }));
        
        lastSearch = "";
        displayMode = DisplayMode.ALL;
        searchType = SearchType.NAME;
        //        InputMappings.Input.REGISTRY.values().stream().forEach(input -> {
        //            System.out.println(input.func_237520_d_().getString() + " : " + input.getKeyCode());
        //        });
    }
    
    @Override
    public boolean charTyped(char var1, int var2) {
        
        return search.charTyped(var1, var2);
    }
    
    @Override
    public void tick() {
        
        this.search.tick();
        if(!lastSearch.equals(search.getText())) {
            filterKeys();
        }
    }
    
    public void filterKeys() {
        
        lastSearch = search.getText();
        keyBindingList.getEventListeners().clear();
        if(keyBindingList instanceof GuiNewKeyBindingList) {
            
            if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
                keyBindingList.getEventListeners().addAll(((GuiCustomList) keyBindingList).getAllEntries());
                return;
            }
            this.keyBindingList.setScrollAmount(0);
            Predicate<GuiNewKeyBindingList.KeyEntry> filters = displayMode.getPredicate();
            
            
            switch(searchType) {
                case NAME:
                    filters = filters.and(keyEntry -> keyEntry.getKeyDesc().toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case CATEGORY:
                    filters = filters.and(keyEntry -> new TranslationTextComponent(keyEntry.getKeybinding().getKeyCategory()).getString().toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case KEY:
                    filters = filters.and(keyEntry -> keyEntry.getKeybinding().func_238171_j_().getString().toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
            }
            
            for(GuiNewKeyBindingList.Entry entry : ((GuiCustomList) keyBindingList).getAllEntries()) {
                if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                    if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
                        GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                        if(filters.test(keyEntry)) {
                            keyBindingList.getEventListeners().add(entry);
                        }
                    } else {
                        keyBindingList.getEventListeners().add(entry);
                    }
                } else {
                    if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
                        GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                        if(filters.test(keyEntry)) {
                            keyBindingList.getEventListeners().add(entry);
                        }
                    }
                }
                
            }
            if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                Set<GuiNewKeyBindingList.CategoryEntry> categories = new LinkedHashSet<>();
                
                for(KeyBindingList.Entry entry : keyBindingList.getEventListeners()) {
                    if(entry instanceof GuiNewKeyBindingList.CategoryEntry) {
                        GuiNewKeyBindingList.CategoryEntry centry = (GuiNewKeyBindingList.CategoryEntry) entry;
                        categories.add(centry);
                        for(KeyBindingList.Entry child : keyBindingList.getEventListeners()) {
                            if(child instanceof GuiNewKeyBindingList.KeyEntry) {
                                GuiNewKeyBindingList.KeyEntry childEntry = (GuiNewKeyBindingList.KeyEntry) child;
                                if(childEntry.getKeybinding().getKeyCategory().equals(centry.getName())) {
                                    categories.remove(centry);
                                }
                            }
                        }
                    }
                }
                keyBindingList.getEventListeners().removeAll(categories);
            }
            sortOrder.sort(keyBindingList.getEventListeners());
            
        } else if(keyBindingList instanceof GuiFreeKeysList) {
            if(lastSearch.isEmpty()) {
                keyBindingList.getEventListeners().addAll(((GuiCustomList) keyBindingList).getAllEntries());
                return;
            }
            this.keyBindingList.setScrollAmount(0);
            
            for(GuiFreeKeysList.Entry entry : ((GuiCustomList) keyBindingList).getAllEntries()) {
                if(entry instanceof GuiFreeKeysList.InputEntry) {
                    GuiFreeKeysList.InputEntry inputEntry = (GuiFreeKeysList.InputEntry) entry;
                    if(inputEntry.getInput().toString().toLowerCase().contains(lastSearch.toLowerCase())) {
                        keyBindingList.getEventListeners().add(entry);
                    }
                } else {
                    keyBindingList.getEventListeners().add(entry);
                }
                
            }
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        
        this.renderBackground(stack);
        this.keyBindingList.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 16777215);
        boolean flag = false;
        
        if(!showFree) {
            for(KeyBinding keybinding : this.options.keyBindings) {
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
            buttonReset.setMessage(new TranslationTextComponent("controls.resetAll"));
        }
        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(stack, mouseX, mouseY, partialTicks);
        }
        
        ITextComponent text = new TranslationTextComponent("options.search");
        font.func_238407_a_(stack, text.func_241878_f(), this.width / 2f - (155 / 2f) - (font.getStringWidth(text.getString())) - 5, this.height - 29 - 42, 16777215);
    }
    
    @Override
    public boolean mouseClicked(double mx, double my, int mb) {
        
        boolean valid;
        if(this.buttonId != null) {
            this.options.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(mb));
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
            valid = true;
            search.setFocused2(false);
        } else if(mb == 0 && this.keyBindingList.mouseClicked(mx, my, mb)) {
            this.setDragging(true);
            this.setListener(this.keyBindingList);
            valid = true;
            search.setFocused2(false);
        } else {
            valid = search.mouseClicked(mx, my, mb);
            if(!valid && search.isFocused() && mb == 1) {
                search.setText("");
                valid = true;
            }
        }
        
        if(!valid) {
            
            for(IGuiEventListener iguieventlistener : this.getEventListeners()) {
                if(iguieventlistener.mouseClicked(mx, my, mb)) {
                    this.setListener(iguieventlistener);
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
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        
        if(!search.isFocused() && this.buttonId == null) {
            if(hasControlDown()) {
                if(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_F)) {
                    search.setFocused2(true);
                    return true;
                }
            }
        }
        if(search.keyPressed(keyCode, scanCode, modifier)) {
            return true;
        }
        if(search.isFocused()) {
            if(keyCode == 256) {
                search.setFocused2(false);
                return true;
            }
        }
        if(this.buttonId != null) {
            if(keyCode == 256) {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.INPUT_INVALID);
                this.options.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
            } else {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getInputByCode(keyCode, scanCode));
                this.options.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(keyCode, scanCode));
            }
            
            if(!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.buttonId.getKey())) {
                this.buttonId = null;
            }
            this.time = Util.milliTime();
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifier);
        }
    }
    
    
}