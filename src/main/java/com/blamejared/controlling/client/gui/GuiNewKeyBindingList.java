package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.api.events.KeyEntryListenersEvent;
import com.blamejared.controlling.api.events.KeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.KeyEntryMouseReleasedEvent;
import com.blamejared.controlling.api.events.KeyEntryRenderEvent;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        children().clear();
        allEntries = new ArrayList<>();
        KeyMapping[] akeybinding = ArrayUtils.clone(mcIn.options.keyMappings);
        Arrays.sort(akeybinding);
        String s = null;
        
        for(KeyMapping keybinding : akeybinding) {
            String s1 = keybinding.getCategory();
            if(!s1.equals(s)) {
                s = s1;
                if(!s1.endsWith(".hidden")) {
                    addEntry(new GuiNewKeyBindingList.CategoryEntry(s1));
                }
            }
            
            int i = mcIn.font.width(I18n.get(keybinding.getName()));
            if(i > this.maxListLabelWidth) {
                this.maxListLabelWidth = i;
            }
            if(!s1.endsWith(".hidden")) {
                addEntry(new GuiNewKeyBindingList.KeyEntry(keybinding));
            }
        }
        
    }
    
    @Override
    protected void renderDecorations(PoseStack matrixStack, int mouseX, int mouseY) {
        
        Entry entry = this.getEntryAtPos(mouseY);
        if(!(entry instanceof KeyEntry)) {
            return;
        }
        KeyEntry keyEntry = (KeyEntry) entry;
        GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(new TranslatableComponent(keyEntry
                .getKeybinding()
                .getCategory())), mouseX, mouseY, mc.screen.width, mc.screen.height, 0, mc.font);
    }
    
    public Entry getEntryAtPos(double mouseY) {
        
        if(mouseY <= getTop() || mouseY >= getBottom()) {
            return null;
        }
        int i1 = Mth.floor(mouseY - (double) this.y0) - this.headerHeight + (int) this
                .getScrollAmount() - 4;
        int j1 = i1 / this.itemHeight;
        return i1 >= 0 && j1 < this.getItemCount() ? this.children()
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
    public class CategoryEntry extends ControlList.Entry {
        
        private final String labelText;
        private final int labelWidth;
        private final String name;
        
        public CategoryEntry(String name) {
            
            this.labelText = I18n.get(name);
            this.labelWidth = GuiNewKeyBindingList.this.mc.font.width(this.labelText);
            this.name = name;
        }
        
        public String getName() {
            
            return name;
        }
        
        public void render(PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            GuiNewKeyBindingList.this.minecraft.font.draw(stack, this.labelText, (float) (GuiNewKeyBindingList.this.minecraft.screen.width / 2 - this.labelWidth / 2), (float) (y + rowWidth - 9 - 1), 16777215);
        }
        
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of(new NarratableEntry() {
                public NarrationPriority narrationPriority() {
                    
                    return NarrationPriority.HOVERED;
                }
                
                public void updateNarration(NarrationElementOutput neo) {
                    
                    neo.add(NarratedElementType.TITLE, labelText);
                }
            });
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            
            return ImmutableList.of();
        }
        
    }
    
    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends ControlList.Entry {
        
        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyMapping keybinding;
        /**
         * The localized key description for this KeyEntry
         */
        private final String keyDesc;
        private final Button btnChangeKeyBinding;
        private final Button btnResetKeyBinding;
        
        
        private KeyEntry(final KeyMapping name) {
            
            this.keybinding = name;
            this.keyDesc = I18n.get(name.getName());
            this.btnChangeKeyBinding = new Button(0, 0, 75 + 20 /*Forge: add space*/, 20, new TextComponent(this.keyDesc), (p_214386_2_) -> {
                GuiNewKeyBindingList.this.controlsScreen.selectedKey = name;
            }) {
                
                @Override
                protected MutableComponent createNarrationMessage() {
                    
                    return name.isUnbound() ? new TranslatableComponent("narrator.controls.unbound", GuiNewKeyBindingList.KeyEntry.this.keyDesc) : new TranslatableComponent("narrator.controls.bound", GuiNewKeyBindingList.KeyEntry.this.keyDesc, super
                            .createNarrationMessage());
                }
                
            };
            this.btnResetKeyBinding = new Button(0, 0, 50, 20, new TranslatableComponent("controls.reset"), (p_214387_2_) -> {
                keybinding.setToDefault();
                GuiNewKeyBindingList.this.minecraft.options.setKey(name, name.getDefaultKey());
                KeyMapping.resetMapping();
            }) {
                
                @Override
                protected MutableComponent createNarrationMessage() {
                    
                    return new TranslatableComponent("narrator.controls.reset", GuiNewKeyBindingList.KeyEntry.this.keyDesc);
                }
            };
        }
        
        @Override
        public void render(PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            MinecraftForge.EVENT_BUS.post(new KeyEntryRenderEvent(this, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks));
            int i = y;
            int j = x;
            boolean flag = GuiNewKeyBindingList.this.controlsScreen.selectedKey == this.keybinding;
            int length = Math.max(0, j + 90 - GuiNewKeyBindingList.this.maxListLabelWidth);
            GuiNewKeyBindingList.this.mc.font.draw(stack, this.keyDesc, (float) (length), (float) (y + rowWidth / 2 - 9 / 2), 16777215);
            this.btnResetKeyBinding.x = x + 190 + 20;
            this.btnResetKeyBinding.y = y;
            this.btnResetKeyBinding.active = !this.keybinding.isDefault();
            this.btnResetKeyBinding.render(stack, mouseX, mouseY, partialTicks);
            
            
            this.btnChangeKeyBinding.x = j + 105;
            this.btnChangeKeyBinding.y = i;
            this.btnChangeKeyBinding.setMessage(this.keybinding.getTranslatedKeyMessage());
            
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
            if(!this.keybinding.isUnbound()) {
                for(KeyMapping keybinding : GuiNewKeyBindingList.this.mc.options.keyMappings) {
                    if(keybinding != this.keybinding && this.keybinding.same(keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }
            Component message = this.btnChangeKeyBinding.getMessage();
            if(flag) {
                this.btnChangeKeyBinding.setMessage(new TextComponent(ChatFormatting.WHITE + "> " + ChatFormatting.YELLOW + message
                        .getString() + ChatFormatting.WHITE + " <"));
            } else if(flag1) {
                MutableComponent modConflict = ComponentUtils.mergeStyles(message.copy(), message.getStyle()
                        .withColor(16755200));
                MutableComponent keyConflict = ComponentUtils.mergeStyles(message.copy(), message.getStyle()
                        .withColor(16755200));
                
                this.btnChangeKeyBinding.setMessage(keyCodeModifierConflict ? modConflict : keyConflict);
            }
            
            this.btnChangeKeyBinding.render(stack, mouseX, mouseY, partialTicks);
        }
        
        public List<GuiEventListener> children() {
            
            KeyEntryListenersEvent event = new KeyEntryListenersEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getListeners();
        }
        
        @Override
        public Optional<GuiEventListener> getChildAt(double p_94730_, double p_94731_) {
            
            return super.getChildAt(p_94730_, p_94731_);
        }
        
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of(this.btnChangeKeyBinding, this.btnResetKeyBinding);
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
        
        public KeyMapping getKeybinding() {
            
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