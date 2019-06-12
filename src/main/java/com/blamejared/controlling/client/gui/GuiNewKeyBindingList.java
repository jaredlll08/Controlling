package com.blamejared.controlling.client.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class GuiNewKeyBindingList extends AbstractOptionList<GuiNewKeyBindingList.Entry> {
    
    private final ControlsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    public List<Entry> allEntries;
    
    public GuiNewKeyBindingList(ControlsScreen controls, Minecraft mcIn) {
        super(mcIn, controls.width + 45, controls.height, 43, controls.height - 80, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;
        allEntries = new ArrayList<>();
        KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        Arrays.sort(akeybinding);
        String s = null;
        
        for(KeyBinding keybinding : akeybinding) {
            String s1 = keybinding.getKeyCategory();
            if(!s1.equals(s)) {
                s = s1;
                add(new GuiNewKeyBindingList.CategoryEntry(s1));
            }
            
            int i = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
            if(i > this.maxListLabelWidth) {
                this.maxListLabelWidth = i;
            }
            
            add(new GuiNewKeyBindingList.KeyEntry(keybinding));
        }
        
    }
    
    
    public List<Entry> getAllEntries() {
        return allEntries;
    }
    
    public void add(Entry ent) {
        this.addEntry(ent);
        allEntries.add(ent);
    }
    
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15 + 20;
    }
    
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }
    
    @OnlyIn(Dist.CLIENT)
    public class CategoryEntry extends GuiNewKeyBindingList.Entry {
        
        private final String labelText;
        private final int labelWidth;
        
        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = GuiNewKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }
        
        
        @Override
        public List<? extends IGuiEventListener> children() {
            return ImmutableList.of();
        }
        
        public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            GuiNewKeyBindingList.this.minecraft.fontRenderer.drawString(this.labelText, (float) (GuiNewKeyBindingList.this.minecraft.field_71462_r.width / 2 - this.labelWidth / 2), (float) (p_render_2_ + p_render_5_ - 9 - 1), 16777215);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry extends AbstractOptionList.Entry<GuiNewKeyBindingList.Entry> {}
    
    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends GuiNewKeyBindingList.Entry {
        
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
            this.btnChangeKeyBinding = new Button(0, 0, 75 + 20 /*Forge: add space*/, 20, this.keyDesc, (p_214386_2_) -> {
                GuiNewKeyBindingList.this.controlsScreen.buttonId = name;
            }) {
                protected String getNarrationMessage() {
                    return name.isInvalid() ? I18n.format("narrator.controls.unbound", GuiNewKeyBindingList.KeyEntry.this.keyDesc) : I18n.format("narrator.controls.bound", GuiNewKeyBindingList.KeyEntry.this.keyDesc, super.getNarrationMessage());
                }
            };
            this.btnResetKeyBinding = new Button(0, 0, 50, 20, I18n.format("controls.reset"), (p_214387_2_) -> {
                keybinding.setToDefault();
                GuiNewKeyBindingList.this.minecraft.gameSettings.setKeyBindingCode(name, name.getDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
            }) {
                protected String getNarrationMessage() {
                    return I18n.format("narrator.controls.reset", GuiNewKeyBindingList.KeyEntry.this.keyDesc);
                }
            };
        }
        
        @Override
        public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            int i = p_render_2_;
            int j = p_render_3_;
            boolean flag = GuiNewKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(this.keyDesc, (float) (j + 90 - GuiNewKeyBindingList.this.maxListLabelWidth), (float)(p_render_2_ + p_render_5_ / 2 - 9 / 2), 16777215);
            this.btnResetKeyBinding.x = p_render_3_ + 190 + 20;
            this.btnResetKeyBinding.y = p_render_2_;
            this.btnResetKeyBinding.active = !this.keybinding.isDefault();
            this.btnResetKeyBinding.render(p_render_6_, p_render_7_, p_render_9_);
            
            
            this.btnChangeKeyBinding.x = j + 105;
            this.btnChangeKeyBinding.y = i;
            this.btnChangeKeyBinding.setMessage(this.keybinding.getLocalizedName());
            
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
            if(flag) {
                this.btnChangeKeyBinding.setMessage(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.getMessage() + TextFormatting.WHITE + " <");
            } else if(flag1) {
                this.btnChangeKeyBinding.setMessage((keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.getMessage());
            }
            
            this.btnChangeKeyBinding.render(p_render_6_, p_render_7_, p_render_9_);
        }
        
        public List<? extends IGuiEventListener> children() {
            return ImmutableList.of(this.btnChangeKeyBinding, this.btnResetKeyBinding);
        }
        
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if(this.btnChangeKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                return true;
            } else {
                return this.btnResetKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
            }
        }
        
        public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
            return this.btnChangeKeyBinding.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
        
        public KeyBinding getKeybinding() {
            return keybinding;
        }
        
        public String getKeyDesc() {
            return keyDesc;
        }
        
        public Button getBtnChangeKeyBinding() {
            return btnChangeKeyBinding;
        }
        
        
    }
}