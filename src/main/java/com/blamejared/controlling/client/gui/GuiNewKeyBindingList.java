package com.blamejared.controlling.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiNewKeyBindingList extends GuiKeyBindingList {

    private final GuiControls controlsScreen;
    private final Minecraft mc;
    public List<IGuiListEntry> listEntries;
    public List<IGuiListEntry> allEntries = new ArrayList<>();
    private int maxListLabelWidth;

    public GuiNewKeyBindingList(GuiControls controls, Minecraft mcIn) {
        super(controls, mcIn);
        this.width = controls.width + 45;
        this.height = controls.height;
        this.top = 63;
        this.bottom = controls.height - 80;
        this.right = controls.width + 45;
        this.controlsScreen = controls;
        this.mc = mcIn;

        KeyBinding[] keyBindings = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        Arrays.sort(keyBindings);
        String s = null;

        for (KeyBinding keybinding : keyBindings) {
            String s1 = keybinding.getKeyCategory();
            if (!s1.equals(s)) {
                s = s1;
                if (!s1.endsWith(".hidden")) {
                    allEntries.add(new CategoryEntry(s1));
                }
            }

            int i = mcIn.fontRendererObj.getStringWidth(I18n.format(keybinding.getKeyDescription()));
            if (i > this.maxListLabelWidth) {
                this.maxListLabelWidth = i;
            }
            if (!s1.endsWith(".hidden")) {
                allEntries.add(new KeyEntry(keybinding));
            }
        }
        
        listEntries = allEntries;
    }

    @Override
    protected int getSize() {
        return this.listEntries.size();
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.listEntries.get(index);
    }

    public List<IGuiListEntry> getAllEntries() {
        return allEntries;
    }
    
    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 15 + 20;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements IGuiListEntry {

        private final String labelText;
        private final int labelWidth;
        private final String name;

        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = mc.fontRendererObj.getStringWidth(this.labelText);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void setSelected(int i, int i1, int i2) {
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            mc.fontRendererObj.drawString(
                this.labelText,
                mc.currentScreen.width / 2 - this.labelWidth / 2,
                y + slotHeight - mc.fontRendererObj.FONT_HEIGHT - 1,
                16777215
            );
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }
    }

    @SideOnly(Side.CLIENT)
    public class KeyEntry implements IGuiListEntry {

        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyBinding keybinding;
        /**
         * The localized key description for this KeyEntry
         */
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnResetKeyBinding;

        private KeyEntry(final KeyBinding name) {
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(2000, 0, 0, 75 + 20, 20, this.keyDesc);
            this.btnResetKeyBinding = new GuiButton(2001, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        @Override
        public void setSelected(int i, int i1, int i2) {
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            boolean flag = controlsScreen.buttonId == this.keybinding;
            mc.fontRendererObj.drawString(
                this.keyDesc,
                x + 90 - maxListLabelWidth,
                y + slotHeight / 2 - mc.fontRendererObj.FONT_HEIGHT / 2,
                16777215
            );
            this.btnResetKeyBinding.xPosition = x + 190 + 20;
            this.btnResetKeyBinding.yPosition = y;
            this.btnResetKeyBinding.enabled = !(this.keybinding.getKeyCode() == this.keybinding.getKeyCodeDefault());
            this.btnResetKeyBinding.drawButton(mc, mouseX, mouseY);

            this.btnChangeKeyBinding.xPosition = x + 105;
            this.btnChangeKeyBinding.yPosition = y;
            this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());

            boolean flag1 = false;

            if (this.keybinding.getKeyCode() != 0) {
                for (KeyBinding keybinding : mc.gameSettings.keyBindings) {
                    if (keybinding != this.keybinding && this.keybinding.getKeyCode() == keybinding.getKeyCode()) {
                        flag1 = true;
                        break;
                    }
                }
            }

            if (flag) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
            } else if (flag1) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.drawButton(mc, mouseX, mouseY);
            
            if (mouseY >= y && mouseY <= y + slotHeight) {
                mc.fontRendererObj.drawString(I18n.format(keybinding.getKeyCategory()), mouseX + 10, mouseY, 0xFFFFFF);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (this.btnChangeKeyBinding.mousePressed(mc, mouseX, mouseY)) {
                controlsScreen.buttonId = this.keybinding;
                return true;
            } else if (this.btnResetKeyBinding.mousePressed(mc, mouseX, mouseY)) {
                this.keybinding.setKeyCode(this.keybinding.getKeyCodeDefault());
                mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            }

            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnResetKeyBinding.mouseReleased(x, y);
        }

        public KeyBinding getKeybinding() {
            return keybinding;
        }

        public String getKeyDesc() {
            return keyDesc;
        }
    }

    public void setListEntries(List<IGuiListEntry> listEntries) {
        this.listEntries = listEntries;
    }
}
