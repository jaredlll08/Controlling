package us.getfluxed.controlsearch.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@SideOnly(Side.CLIENT)
public class GuiNewKeyBindingList extends GuiKeyBindingList {
    
    private final GuiNewControls controlsScreen;
    private final Minecraft mc;
    private LinkedList<IGuiListEntry> listEntries;
    private LinkedList<IGuiListEntry> listEntriesAll;
    
    private int maxListLabelWidth;
    
    public GuiNewKeyBindingList(GuiNewControls controls, Minecraft mcIn) {
        super(controls, mcIn);
        this.controlsScreen = controls;
        this.mc = mcIn;
        this.width = controls.width + 45;
        this.height = controls.height + 80;
        this.top = 63;
        this.bottom = controls.height - 80;
        KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        listEntries = new LinkedList<>();
        listEntriesAll = new LinkedList<>();
        
        Arrays.sort(akeybinding);
        int i = 0;
        String s = null;
        
        for(KeyBinding keybinding : akeybinding) {
            String s1 = keybinding.getKeyCategory();
            
            if(!s1.equals(s)) {
                s = s1;
                if(!s1.endsWith(".hidden")) {
                    this.listEntries.add(new GuiNewKeyBindingList.CategoryEntry(s1));
                    this.listEntriesAll.add(new GuiNewKeyBindingList.CategoryEntry(s1));
                }
            }
            
            int j = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
            
            if(j > this.maxListLabelWidth) {
                this.maxListLabelWidth = j;
            }
            if(!s1.endsWith(".hidden")) {
                this.listEntries.add(new GuiNewKeyBindingList.KeyEntry(keybinding));
                this.listEntriesAll.add(new GuiNewKeyBindingList.KeyEntry(keybinding));
            }
        }
    }
    
    protected int getSize() {
        return this.listEntries.size();
    }
    
    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries.get(index);
    }
    
    protected int getScrollBarX() {
        return GuiNewKeyBindingList.this.mc.currentScreen.width - 20;
    }
    
    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return super.getListWidth() + 32;
    }
    
    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements GuiListExtended.IGuiListEntry {
        
        public final String labelText;
        private final int labelWidth;
        
        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = GuiNewKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }


        @Override
        public void updatePosition(int i, int i1, int i2, float v) {

        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float p_192634_9_) {
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(this.labelText, GuiNewKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - GuiNewKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
        }
        
        /**
         * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
         * clicked and the list should not be dragged.
         */
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            return false;
        }
        
        /**
         * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
         */
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }
        
    }
    
    @SideOnly(Side.CLIENT)
    public class KeyEntry implements GuiListExtended.IGuiListEntry {
        
        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyBinding keybinding;
        /**
         * The localized key description for this KeyEntry
         */
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;
        
        private KeyEntry(KeyBinding name) {
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, I18n.format(name.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }
        
        @Override
        public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
        }
        
        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float p_192634_9_) {
            boolean flag = GuiNewKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            int halfScreen = GuiNewKeyBindingList.this.mc.currentScreen.width / 2;
            boolean extendFlag = GuiNewKeyBindingList.this.mc.fontRenderer.getStringWidth(this.keyDesc) > halfScreen;
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(this.keyDesc, Math.max(x + 90 - GuiNewKeyBindingList.this.maxListLabelWidth, 20), y + slotHeight / 2 - GuiNewKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            //            GuiNewKeyBindingList.this.mc.fontRendererObj.drawString(String.format("(%s)", I18n.format(keybinding.getKeyCategory())), x - 45 - GuiNewKeyBindingList.this.maxListLabelWidth, y + slotHeight / 2 - GuiNewKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2, 16777215);
            btnReset.visible = !keybinding.isSetToDefaultValue();
            this.btnReset.x = halfScreen + 110;
            this.btnReset.y = y;
            this.btnReset.enabled = !this.keybinding.isSetToDefaultValue();
            
            this.btnChangeKeyBinding.x = halfScreen + 5;
            this.btnChangeKeyBinding.y = y;
            this.btnChangeKeyBinding.displayString = this.keybinding.getDisplayName();
            
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
            
            if(this.keybinding.getKeyCode() != 0) {
                for(KeyBinding keybinding : GuiNewKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if(keybinding != this.keybinding && keybinding.conflicts(this.keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }
            
            if(flag) {
                this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
            } else if(flag1) {
                this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.displayString;
            }
            this.btnChangeKeyBinding.drawButton(GuiNewKeyBindingList.this.mc, mouseX, mouseY, p_192634_9_);
            this.btnReset.drawButton(GuiNewKeyBindingList.this.mc, mouseX, mouseY, p_192634_9_);
            //            if(mouseX >= x + 90 - GuiNewKeyBindingList.this.maxListLabelWidth && mouseX <= x + listWidth) {
            if(mouseY >= y && mouseY <= y + slotHeight) {
                if(extendFlag) {
                    mc.fontRenderer.drawString(I18n.format(keybinding.getKeyCategory()) + ": " + this.keyDesc, mouseX + 10, mouseY, 0xFFFFFF);
                }else{
                    mc.fontRenderer.drawString(I18n.format(keybinding.getKeyCategory()), mouseX + 10, mouseY, 0xFFFFFF);
                }            }
            //            }
            
            
        }
        
        /**
         * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
         * clicked and the list should not be dragged.
         */
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if(this.btnChangeKeyBinding.mousePressed(GuiNewKeyBindingList.this.mc, mouseX, mouseY)) {
                GuiNewKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
                return true;
            } else if(this.btnReset.mousePressed(GuiNewKeyBindingList.this.mc, mouseX, mouseY)) {
                this.keybinding.setToDefault();
                GuiNewKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
         */
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
        }
        
        public KeyBinding getKeybinding() {
            return keybinding;
        }
    }
    
    public LinkedList<IGuiListEntry> getListEntries() {
        return listEntries;
    }
    
    public LinkedList<IGuiListEntry> getListEntriesAll() {
        return listEntriesAll;
    }
    
    public void setListEntries(LinkedList<IGuiListEntry> listEntries) {
        this.listEntries = listEntries;
    }
}
