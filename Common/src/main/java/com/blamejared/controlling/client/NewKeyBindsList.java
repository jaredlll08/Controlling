package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.events.IKeyEntryListenersEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseReleasedEvent;
import com.blamejared.controlling.platform.Services;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.UnaryOperator;

public class NewKeyBindsList extends CustomList {
    
    private final KeyBindsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    public NewKeyBindsList(KeyBindsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
        this.width = controls.width + 45;
        this.height = controls.height;
        this.y0 = 48;
        this.y1 = controls.height - 56;
        this.x1 = controls.width + 45;
        this.controlsScreen = controls;
        this.mc = mcIn;
        children().clear();
        allEntries = new ArrayList<>();
        KeyMapping[] bindings = ArrayUtils.clone(mcIn.options.keyMappings);
        Arrays.sort(bindings);
        String lastCategory = null;
        
        for(KeyMapping keybinding : bindings) {
            String category = keybinding.getCategory();
            if(!category.equals(lastCategory)) {
                lastCategory = category;
                if(!category.endsWith(".hidden")) {
                    addEntry(new NewKeyBindsList.CategoryEntry(category));
                }
            }
    
            Component component = Component.translatable(keybinding.getName());
            int width = mcIn.font.width(component);
            if(width > this.maxListLabelWidth) {
                this.maxListLabelWidth = width;
            }
            if(!category.endsWith(".hidden")) {
                addEntry(new NewKeyBindsList.KeyEntry(keybinding, component));
            }
        }
        
    }
    
    public Entry getEntryAtPos(double mouseY) {
        
        if(mouseY <= this.y0 || mouseY >= this.y1) {
            return null;
        }
        int i1 = Mth.floor(mouseY - (double) this.y0) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int itemIndex = i1 / this.itemHeight;
        return i1 >= 0 && itemIndex < this.getItemCount() ? this.children().get(itemIndex) : null;
    }
    
    @Override
    protected int getScrollbarPosition() {
        
        return super.getScrollbarPosition() + 15 + 20;
    }
    
    @Override
    public int getRowWidth() {
        
        return super.getRowWidth() + 32;
    }
    
    public class CategoryEntry extends KeyBindsList.Entry {
        
        private final Component name;
        private final int labelWidth;
        
        public CategoryEntry(String name) {
            
            this.name = Component.translatable(name);
            this.labelWidth = NewKeyBindsList.this.mc.font.width(this.name);
        }
        
        public Component name() {
            
            return name;
        }
        
        public void render(PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            NewKeyBindsList.this.minecraft.font.draw(stack, this.name, (float) (Objects.requireNonNull(minecraft.screen).width / 2 - this.labelWidth / 2), (float) (y + rowWidth - 9 - 1), 16777215);
        }
        
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of(new NarratableEntry() {
                public NarrationPriority narrationPriority() {
                    
                    return NarrationPriority.HOVERED;
                }
                
                public void updateNarration(NarrationElementOutput neo) {
                    
                    neo.add(NarratedElementType.TITLE, name);
                }
            });
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            
            return ImmutableList.of();
        }
        
    }
    
    public class KeyEntry extends KeyBindsList.Entry {
        
        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyMapping keybinding;
        /**
         * The localized key description for this KeyEntry
         */
        private final Component keyDesc;
        private final Button btnChangeKeyBinding;
        private final Button btnResetKeyBinding;
        
        private final Component categoryName;
        
        private KeyEntry(final KeyMapping name, final Component keyDesc) {
            
            this.keybinding = name;
            this.keyDesc = keyDesc;
            this.btnChangeKeyBinding = Button.builder(this.keyDesc, (btn) -> NewKeyBindsList.this.controlsScreen.selectedKey = name)
                    .bounds(0, 0, 75, 20)
                    .createNarration(supp -> name.isUnbound() ? Component.translatable("narrator.controls.unbound", keyDesc) : Component.translatable("narrator.controls.bound", keyDesc, supp.get()))
                    .build();
            
            this.btnResetKeyBinding = Button.builder(ControllingConstants.COMPONENT_CONTROLS_RESET, btn -> {
                        Services.PLATFORM.setToDefault(minecraft.options, name);
                        KeyMapping.resetMapping();
                    }).bounds(0, 0, 50, 20)
                    .createNarration(supp -> Component.translatable("narrator.controls.reset", keyDesc))
                    .build();
            this.categoryName = Component.translatable(this.keybinding.getCategory());
            this.btnChangeKeyBinding.setTooltip(Tooltip.create(Component.translatable(keybinding.getCategory())));
        }
        
        @Override
        public void render(PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            Services.EVENT.fireKeyEntryRenderEvent(this, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks);
            boolean flag = NewKeyBindsList.this.controlsScreen.selectedKey == this.keybinding;
            int length = Math.max(0, x + 90 - NewKeyBindsList.this.maxListLabelWidth);
            NewKeyBindsList.this.mc.font.draw(stack, this.keyDesc, (float) (length), (float) (y + rowWidth / 2 - 9 / 2), 16777215);
            this.btnResetKeyBinding.setX(x + 190 + 20);
            this.btnResetKeyBinding.setY(y);
            this.btnResetKeyBinding.active = !this.keybinding.isDefault();
            this.btnResetKeyBinding.render(stack, mouseX, mouseY, partialTicks);
            
            
            this.btnChangeKeyBinding.setX(x + 105);
            this.btnChangeKeyBinding.setY(y);
            this.btnChangeKeyBinding.setMessage(this.keybinding.getTranslatedKeyMessage());
            
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true;
            if(!this.keybinding.isUnbound()) {
                for(KeyMapping otherBinding : NewKeyBindsList.this.mc.options.keyMappings) {
                    if(otherBinding != this.keybinding && this.keybinding.same(otherBinding)) {
                        flag1 = true;
                        
                        keyCodeModifierConflict &= Services.PLATFORM.hasConflictingModifier(keybinding, otherBinding);
                    }
                }
            }
            Component message = this.btnChangeKeyBinding.getMessage();
            if(flag) {
                this.btnChangeKeyBinding.setMessage(Component.literal(ChatFormatting.WHITE + "> " + ChatFormatting.YELLOW + message.getString() + ChatFormatting.WHITE + " <"));
            } else if(flag1) {
                this.btnChangeKeyBinding.setMessage(message.copy()
                        .withStyle(keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED));
            }
            
            this.btnChangeKeyBinding.render(stack, mouseX, mouseY, partialTicks);
        }
        
        public List<GuiEventListener> children() {
            
            return Services.EVENT.fireKeyEntryListenersEvent(this)
                    .map(IKeyEntryListenersEvent::getListeners, UnaryOperator.identity());
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
            
            if(Services.EVENT.fireKeyEntryMouseClickedEvent(this, mouseX, mouseY, buttonId)
                    .map(IKeyEntryMouseClickedEvent::isHandled, UnaryOperator.identity())) {
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
            
            if(Services.EVENT.fireKeyEntryMouseReleasedEvent(this, mouseX, mouseY, buttonId)
                    .map(IKeyEntryMouseReleasedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }
            
            return this.btnChangeKeyBinding.mouseReleased(mouseX, mouseY, buttonId);
        }
        
        public KeyMapping getKeybinding() {
            
            return keybinding;
        }
        
        public Component getKeyDesc() {
            
            return keyDesc;
        }
        
        public Button getBtnResetKeyBinding() {
            
            return btnResetKeyBinding;
        }
        
        public Button getBtnChangeKeyBinding() {
            
            return btnChangeKeyBinding;
        }
        
        public Component categoryName() {
            
            return categoryName;
        }
        
    }
    
}