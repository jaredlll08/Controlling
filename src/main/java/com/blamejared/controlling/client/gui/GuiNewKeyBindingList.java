package com.blamejared.controlling.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class GuiNewKeyBindingList extends KeyBindingList {

    private final ControlsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    public List<Entry> allEntries;

    public GuiNewKeyBindingList(ControlsScreen controls, Minecraft mcIn) {
        super(controls, mcIn);
        this.width = controls.width + 45;
        this.height = controls.height;
        this.y0 = 43;
        this.y1 = controls.height - 80;
        this.x1 = controls.width + 45;
        this.controlsScreen = controls;
        this.mc = mcIn;
        children().clear();
        allEntries = new ArrayList<>();
        KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        Arrays.sort(akeybinding);
        String s = null;

        for (KeyBinding keybinding : akeybinding) {
            String s1 = keybinding.getKeyCategory();
            if (!s1.equals(s)) {
                s = s1;
                if (!s1.endsWith(".hidden")) {
                    add(new GuiNewKeyBindingList.CategoryEntry(s1));
                }
            }

            int i = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
            if (i > this.maxListLabelWidth) {
                this.maxListLabelWidth = i;
            }
            if (!s1.endsWith(".hidden")) {
                add(new GuiNewKeyBindingList.KeyEntry(keybinding));
            }
        }

    }


    public List<Entry> getAllEntries() {
        return allEntries;
    }

    public void add(Entry ent) {
        children().add(ent);
        allEntries.add(ent);
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15 + 20;
    }

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
        public List<? extends IGuiEventListener> children() {
            return ImmutableList.of();
        }

        public void render(MatrixStack stack, int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            GuiNewKeyBindingList.this.minecraft.fontRenderer.drawString(stack, this.labelText, (float) (GuiNewKeyBindingList.this.minecraft.currentScreen.width / 2 - this.labelWidth / 2), (float) (p_render_2_ + p_render_5_ - 9 - 1), 16777215);
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
                protected IFormattableTextComponent getNarrationMessage() {
                    return name.isInvalid() ? new TranslationTextComponent("narrator.controls.unbound", GuiNewKeyBindingList.KeyEntry.this.keyDesc) : new TranslationTextComponent("narrator.controls.bound", GuiNewKeyBindingList.KeyEntry.this.keyDesc, super.getNarrationMessage());
                }
            };
            this.btnResetKeyBinding = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), (p_214387_2_) -> {
                keybinding.setToDefault();
                GuiNewKeyBindingList.this.minecraft.gameSettings.setKeyBindingCode(name, name.getDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
            }) {
                protected IFormattableTextComponent getNarrationMessage() {
                    return new TranslationTextComponent("narrator.controls.reset", GuiNewKeyBindingList.KeyEntry.this.keyDesc);
                }
            };
        }

        @Override
        public void render(MatrixStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            int i = y;
            int j = x;
            boolean flag = GuiNewKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(stack, this.keyDesc, (float) (j + 90 - GuiNewKeyBindingList.this.maxListLabelWidth), (float) (y + p_render_5_ / 2 - 9 / 2), 16777215);
            this.btnResetKeyBinding.x = x + 190 + 20;
            this.btnResetKeyBinding.y = y;
            this.btnResetKeyBinding.active = !this.keybinding.isDefault();
            this.btnResetKeyBinding.render(stack, mouseX, mouseY, p_render_9_);


            this.btnChangeKeyBinding.x = j + 105;
            this.btnChangeKeyBinding.y = i;
            this.btnChangeKeyBinding.setMessage(this.keybinding.func_238171_j_());

            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
            if (!this.keybinding.isInvalid()) {
                for (KeyBinding keybinding : GuiNewKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if (keybinding != this.keybinding && this.keybinding.conflicts(keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }
            ITextComponent message = this.btnChangeKeyBinding.getMessage();
            if (flag) {
                this.btnChangeKeyBinding.setMessage(new StringTextComponent(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + message.getString() + TextFormatting.WHITE + " <"));
            } else if (flag1) {
                IFormattableTextComponent modConflict = TextComponentUtils.func_240648_a_(message.copyRaw(), message.getStyle().setColor(Color.func_240743_a_(16755200)));
                IFormattableTextComponent keyConflict = TextComponentUtils.func_240648_a_(message.copyRaw(), message.getStyle().setColor(Color.func_240743_a_(16755200)));

                this.btnChangeKeyBinding.setMessage(keyCodeModifierConflict ? modConflict : keyConflict);
            }

            this.btnChangeKeyBinding.render(stack, mouseX, mouseY, p_render_9_);
            if (mouseY >= y && mouseY <= y + p_render_5_) {
                mc.fontRenderer.drawString(stack, I18n.format(keybinding.getKeyCategory()), mouseX + 10, mouseY, 0xFFFFFF);
            }
        }

        public List<? extends IGuiEventListener> children() {
            return ImmutableList.of(this.btnChangeKeyBinding, this.btnResetKeyBinding);
        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (this.btnChangeKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
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