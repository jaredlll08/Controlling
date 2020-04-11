package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.Controlling;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.*;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.*;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.glfw.GLFW;

import java.util.Random;
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
    
    private Button buttonUnboundConflictingAll;
    private Button buttonShowAvailableKeys;

    private GuiCheckBox buttonKey;
    private GuiCheckBox buttonCat;
    private Button patreonButton;
    private boolean confirmingReset = false;
    
    private String name;
    
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
            this.minecraft.displayGuiScreen(new MouseSettingsScreen(this, options));
        }));
        this.addButton(AbstractOption.AUTO_JUMP.createWidget(this.minecraft.gameSettings, this.width / 2 - 155 + 160, 18, 150));
        
        this.keyBindingList = new GuiNewKeyBindingList(this, this.minecraft);
        this.children.add(this.keyBindingList);
        this.setFocused(this.keyBindingList);
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done"), (p_213126_1_) -> GuiNewControls.this.minecraft.displayGuiScreen(GuiNewControls.this.parentScreen)));
        
        this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll"), (p_213126_1_) -> {
            
            if(!confirmingReset) {
                confirmingReset = true;
                p_213126_1_.setMessage(I18n.format("options.confirmReset"));
                return;
            }
            confirmingReset = false;
            p_213126_1_.setMessage(I18n.format("controls.resetAll"));
            for(KeyBinding keybinding : GuiNewControls.this.minecraft.gameSettings.keyBindings) {
                keybinding.setToDefault();
            }
            
            KeyBinding.resetKeyBindingArrayAndHash();
        }));
        this.buttonShowAvailableKeys = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24 - 24, 20, 20, "?", (p_213126_1_) -> {

        }));
        this.buttonUnboundConflictingAll = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24, 150, 20, I18n.format("options.showUnbound"), (p_213126_1_) -> {
            if(displayMode == DisplayMode.ALL) {
                buttonUnboundConflictingAll.setMessage(I18n.format("options.showConflicts"));
                displayMode = DisplayMode.UNBOUND;
            } else if(displayMode == DisplayMode.UNBOUND) {
                buttonUnboundConflictingAll.setMessage(I18n.format("options.showAll"));
                displayMode = DisplayMode.CONFLICTING;
            } else if(displayMode == DisplayMode.CONFLICTING) {
                buttonUnboundConflictingAll.setMessage(I18n.format("options.showUnbound"));
                displayMode = DisplayMode.ALL;
            }
            filterKeys();
        }));
        search = new TextFieldWidget(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, "");
        this.buttonKey = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 37, I18n.format("options.key"), false) {
            @Override
            public void onPress() {
                super.onPress();
                buttonCat.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.KEY : SearchType.NAME;
                filterKeys();
            }
        });
        this.buttonCat = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 50, I18n.format("options.category"), false) {
            
            @Override
            public void onPress() {
                super.onPress();
                buttonKey.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
                filterKeys();
            }
        });
        sortOrder = SortOrder.NONE;
        Button buttonSort = this.addButton(new Button(this.width / 2 - 155 + 160 + 24, this.height - 29 - 24 - 24, 150 - 24, 20, I18n.format("options.sort") + ": " + sortOrder.getName(), (p_213126_1_) -> {
            sortOrder = sortOrder.cycle();
            p_213126_1_.setMessage(I18n.format("options.sort") + ": " + sortOrder.getName());
            filterKeys();
        }));
        // name = Controlling.PATRON_LIST.stream().skip(Controlling.PATRON_LIST.isEmpty() ? 0 : new Random().nextInt(Controlling.PATRON_LIST.size())).findFirst().orElse("");
        // patreonButton = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24 - 24, 150 / 2, 20, "Patreon", p_onPress_1_ -> {
        //     Util.getOSType().openURI("https://patreon.com/jaredlll08?s=controllingmod");
        // }) {
        //     private boolean wasHovered;
            
        //     @Override
        //     public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        //         if(this.visible) {
        //             this.isHovered = p_render_1_ >= this.x && p_render_2_ >= this.y && p_render_1_ < this.x + this.width && p_render_2_ < this.y + this.height;
        //             if(this.wasHovered != this.isHovered()) {
        //                 if(this.isHovered()) {
        //                     if(this.isFocused()) {
        //                         this.nextNarration = Util.milliTime() + 200L;
        //                     } else {
        //                         this.nextNarration = Util.milliTime() + 750L;
        //                     }
        //                 } else {
        //                     this.nextNarration = Long.MAX_VALUE;
        //                 }
        //             }
                    
        //             if(this.visible) {
        //                 this.renderButton(p_render_1_, p_render_2_, p_render_3_);
        //             }
                    
        //             this.narrate();
        //             this.wasHovered = this.isHovered();
        //         }
        //     }
        // });
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
            keyBindingList.children().addAll(((GuiNewKeyBindingList) keyBindingList).getAllEntries());
            return;
        }
        this.keyBindingList.setScrollAmount(0);
        Predicate<GuiNewKeyBindingList.KeyEntry> filters = displayMode.getPredicate();
        
        
        switch(searchType) {
            case NAME:
                filters = filters.and(keyEntry -> keyEntry.getKeyDesc().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
            case CATEGORY:
                filters = filters.and(keyEntry -> keyEntry.getKeybinding().getKeyCategory().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
            case KEY:
                filters = filters.and(keyEntry -> keyEntry.getKeybinding().getLocalizedName().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
        }
        
        for(GuiNewKeyBindingList.Entry entry : ((GuiNewKeyBindingList) keyBindingList).getAllEntries()) {
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
        if(!flag) {
            confirmingReset = false;
            buttonReset.setMessage(I18n.format("controls.resetAll"));
        }
        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(mouseX, mouseY, partialTicks);
        }
        
        String text = I18n.format("options.searchBy");
        GlStateManager.disableLighting();
        font.drawString(text, this.width / 2 - (155 / 2) - (font.getStringWidth(text)) - 5, this.height - 29 - 42, 16777215);
        GlStateManager.enableLighting();
        
        // if(patreonButton.isHovered()) {
        //     GlStateManager.disableLighting();
        //     String str = "Join " + name + " and other patrons!";
        //     renderTooltip(str, mouseX, mouseY);
        //     GlStateManager.enableLighting();
        // }
    }
    
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
            this.setFocused(this.keyBindingList);
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
        if(!search.isFocused() && this.buttonId == null) {
            if(hasControlDown()) {
                if(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_F)) {
                    search.setFocused2(true);
                    return true;
                }
            }
        }
        if(search.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        if(search.isFocused()) {
            if(p_keyPressed_1_ == 256) {
                search.setFocused2(false);
                return true;
            }
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