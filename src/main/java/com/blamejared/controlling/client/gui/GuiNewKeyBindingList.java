package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.api.events.KeyEntryListenersEvent;
import com.blamejared.controlling.api.events.KeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.KeyEntryMouseReleasedEvent;
import com.blamejared.controlling.api.events.KeyEntryRenderEvent;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class GuiNewKeyBindingList extends GuiCustomList {
    
    private final ControlsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    public GuiNewKeyBindingList(ControlsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
        this.width = controls.width + 45;
        this.height = controls.height;
        this.y0 = 43;
        this.y1 = controls.height - 80;
        this.x1 = controls.width + 45;
        this.controlsScreen = controls;
        this.mc = mcIn;
        getEventListeners().clear();
        allEntries = new ArrayList<>();
        KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        Arrays.sort(akeybinding);
        String s = null;
        
        for(KeyBinding keybinding : akeybinding) {
            String s1 = keybinding.getKeyCategory();
            if(!s1.equals(s)) {
                s = s1;
                if(!s1.endsWith(".hidden")) {
                    add(new GuiNewKeyBindingList.CategoryEntry(s1));
                }
            }
            
            int i = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
            if(i > this.maxListLabelWidth) {
                this.maxListLabelWidth = i;
            }
            if(!s1.endsWith(".hidden")) {
                add(new GuiNewKeyBindingList.KeyEntry(keybinding));
            }
        }
        
    }
    
    @Override
    protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY) {
        
        Entry entry = this.getEntryAtPos(mouseY);
        if(!(entry instanceof KeyEntry)) {
            return;
        }
        KeyEntry keyEntry = (KeyEntry) entry;
        GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(new TranslationTextComponent(keyEntry
                .getKeybinding()
                .getKeyCategory())), mouseX, mouseY, mc.currentScreen.width, mc.currentScreen.height, 0, mc.fontRenderer);
    }
    
    public Entry getEntryAtPos(double mouseY) {
        
        if(mouseY <= getTop() || mouseY >= getBottom()) {
            return null;
        }
        int i1 = MathHelper.floor(mouseY - (double) this.y0) - this.headerHeight + (int) this
                .getScrollAmount() - 4;
        int j1 = i1 / this.itemHeight;
        return i1 >= 0 && j1 < this.getItemCount() ? this.getEventListeners()
                .get(j1) : null;
    }
    
    @Override
    protected int getScrollbarPosition() {
        
        return super.getScrollbarPosition() + 15 + 20;
    }
    
    @Override
    public int getRowWidth() {
        
        return super.getRowWidth() + 32;
    }
    
    @OnlyIn(Dist.CLIENT)
    public class CategoryEntry extends KeyBindingList.Entry {
        
        private final String labelText;
        private final int labelWidth;
        private final String name;
        
        public CategoryEntry(String name) {
            
            this.labelText = I18n.format(name);
            this.labelWidth = GuiNewKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
            this.name = name;
        }
        
        public String getName() {
            
            return name;
        }
        
        @Override
        public List<? extends IGuiEventListener> getEventListeners() {
            
            return ImmutableList.of();
        }
        
        public void render(MatrixStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            GuiNewKeyBindingList.this.minecraft.fontRenderer.drawString(stack, this.labelText, (float) (GuiNewKeyBindingList.this.minecraft.currentScreen.width / 2 - this.labelWidth / 2), (float) (y + rowWidth - 9 - 1), 16777215);
        }
        
    }
    
    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends KeyBindingList.Entry {
        
        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyBinding keybinding;
        /**
         * The localized key description for this KeyEntry
         */
        private final String keyDesc;
        private final Button btnChangeKeyBinding;
        private final Button btnResetKeyBinding;
        
        
        private KeyEntry(final KeyBinding name) {
            
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new Button(0, 0, 75 + 20 /*Forge: add space*/, 20, new StringTextComponent(this.keyDesc), (p_214386_2_) -> {
                GuiNewKeyBindingList.this.controlsScreen.buttonId = name;
            }) {
                @Override
                protected IFormattableTextComponent getNarrationMessage() {
                    
                    return name.isInvalid() ? new TranslationTextComponent("narrator.controls.unbound", GuiNewKeyBindingList.KeyEntry.this.keyDesc) : new TranslationTextComponent("narrator.controls.bound", GuiNewKeyBindingList.KeyEntry.this.keyDesc, super
                            .getNarrationMessage());
                }
            };
            this.btnResetKeyBinding = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), (p_214387_2_) -> {
                keybinding.setToDefault();
                GuiNewKeyBindingList.this.minecraft.gameSettings.setKeyBindingCode(name, name
                        .getDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
            }) {
                @Override
                protected IFormattableTextComponent getNarrationMessage() {
                    
                    return new TranslationTextComponent("narrator.controls.reset", GuiNewKeyBindingList.KeyEntry.this.keyDesc);
                }
            };
        }
        
        @Override
        public void render(MatrixStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            MinecraftForge.EVENT_BUS.post(new KeyEntryRenderEvent(this, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks));
            int i = y;
            int j = x;
            boolean flag = GuiNewKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            int length = Math.max(0, j + 90 - GuiNewKeyBindingList.this.maxListLabelWidth);
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(stack, this.keyDesc, (float) (length), (float) (y + rowWidth / 2 - 9 / 2), 16777215);
            this.btnResetKeyBinding.x = x + 190 + 20;
            this.btnResetKeyBinding.y = y;
            this.btnResetKeyBinding.active = !this.keybinding.isDefault();
            this.btnResetKeyBinding.render(stack, mouseX, mouseY, partialTicks);
            
            
            this.btnChangeKeyBinding.x = j + 105;
            this.btnChangeKeyBinding.y = i;
            this.btnChangeKeyBinding.setMessage(this.keybinding.func_238171_j_());
            
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
            if(!this.keybinding.isInvalid()) {
                for(KeyBinding keybinding : GuiNewKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if(keybinding != this.keybinding && this.keybinding.conflicts(keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }
            ITextComponent message = this.btnChangeKeyBinding.getMessage();
            if(flag) {
                this.btnChangeKeyBinding.setMessage(new StringTextComponent(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + message
                        .getString() + TextFormatting.WHITE + " <"));
            } else if(flag1) {
                IFormattableTextComponent modConflict = TextComponentUtils.func_240648_a_(message
                        .copyRaw(), message.getStyle()
                        .setColor(Color.fromInt(16755200)));
                IFormattableTextComponent keyConflict = TextComponentUtils.func_240648_a_(message
                        .copyRaw(), message.getStyle()
                        .setColor(Color.fromInt(16755200)));
                
                this.btnChangeKeyBinding.setMessage(keyCodeModifierConflict ? modConflict : keyConflict);
            }
            
            this.btnChangeKeyBinding.render(stack, mouseX, mouseY, partialTicks);
        }
        
        public List<IGuiEventListener> getEventListeners() {
            
            KeyEntryListenersEvent event = new KeyEntryListenersEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getListeners();
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
            
            KeyEntryMouseClickedEvent event = new KeyEntryMouseClickedEvent(this, mouseX, mouseY, buttonId);
            MinecraftForge.EVENT_BUS.post(event);
            if(event.isHandled()) {
                return true;
            }
            
            if(this.btnChangeKeyBinding.mouseClicked(mouseX, mouseY, buttonId)) {
                return true;
            } else {
                return this.btnResetKeyBinding.mouseClicked(mouseX, mouseY, buttonId);
            }
        }
        
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int buttonId) {
    
            KeyEntryMouseReleasedEvent event = new KeyEntryMouseReleasedEvent(this, mouseX, mouseY, buttonId);
            MinecraftForge.EVENT_BUS.post(event);
            if(event.isHandled()) {
                return true;
            }
            
            return this.btnChangeKeyBinding.mouseReleased(mouseX, mouseY, buttonId);
        }
        
        public KeyBinding getKeybinding() {
            
            return keybinding;
        }
        
        public String getKeyDesc() {
            
            return keyDesc;
        }
        
        public Button getBtnResetKeyBinding() {
            
            return btnResetKeyBinding;
        }
        
        public Button getBtnChangeKeyBinding() {
            
            return btnChangeKeyBinding;
        }
        
    }
    
}