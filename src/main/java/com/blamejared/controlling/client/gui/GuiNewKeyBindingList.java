package com.blamejared.controlling.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class GuiNewKeyBindingList extends GuiListExtended<GuiNewKeyBindingList.Entry> {
    
    private final GuiControls controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    public List<Entry> allEntries;
    
    public GuiNewKeyBindingList(GuiControls controls, Minecraft mcIn) {
        super(mcIn, controls.width + 45, controls.height, 63, controls.height - 80, 20);
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
    
    protected int getScrollBarX() {
        return super.getScrollBarX() + 35;
    }
    
    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return super.getListWidth() + 32;
    }
    
    @OnlyIn(Dist.CLIENT)
    public class CategoryEntry extends GuiNewKeyBindingList.Entry {
        
        private final String labelText;
        private final int labelWidth;
        
        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = GuiNewKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }
        
        public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(this.labelText, (float) (GuiNewKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2), (float) (this.getY() + entryHeight - GuiNewKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1), 16777215);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry extends GuiListExtended.IGuiListEntry<GuiNewKeyBindingList.Entry> {}
    
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
        private final GuiButton btnChangeKeyBinding;
        
        private KeyEntry(final KeyBinding name) {
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, I18n.format(name.func_197978_k())) {
                /**
                 * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                 */
                public void onClick(double mouseX, double mouseY) {
                    GuiNewKeyBindingList.this.controlsScreen.buttonId = name;
                }
            };
        }
        
        public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            int i = this.getY();
            int j = this.getX();
            boolean flag = GuiNewKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(this.keyDesc, (float) (j + 90 - GuiNewKeyBindingList.this.maxListLabelWidth), (float) (i + entryHeight / 2 - GuiNewKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2), 16777215);
            this.btnChangeKeyBinding.x = j + 105;
            this.btnChangeKeyBinding.y = i;
            this.btnChangeKeyBinding.displayString = this.keybinding.func_197978_k();
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
            if(!this.keybinding.isInvalid()) {
                for(KeyBinding keybinding : GuiNewKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if(keybinding != this.keybinding && this.keybinding.func_197983_b(keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }
            //TODO replace with text formatting when it's fixed
            if(flag) {
                this.btnChangeKeyBinding.displayString = "§f" + "> " + "§e" + this.btnChangeKeyBinding.displayString + "§f" + " <";
            } else if(flag1) {
                this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? "§6" : "§c") + this.btnChangeKeyBinding.displayString;
            }
            
            this.btnChangeKeyBinding.render(mouseX, mouseY, partialTicks);
        }
        
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if(this.btnChangeKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                return true;
            }
            return false;
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
        
        public GuiButton getBtnChangeKeyBinding() {
            return btnChangeKeyBinding;
        }
    }
}