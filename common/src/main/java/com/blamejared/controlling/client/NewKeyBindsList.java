package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.events.*;
import com.blamejared.controlling.platform.Services;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.*;
import net.minecraft.client.gui.screens.controls.*;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.*;
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
        this.y0 = 20;
        this.y1 = controls.height - 80;
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
    
    @Override
    protected int getScrollbarPosition() {
        
        return super.getScrollbarPosition() + 15 + 20;
    }
    
    @Override
    public int getRowWidth() {
        
        return super.getRowWidth() + 32;
    }
    
    public class CategoryEntry extends Entry {
        
        private final String labelText;
        private final int labelWidth;
        private final String name;
        
        public CategoryEntry(String name) {
            
            this.labelText = I18n.get(name);
            this.labelWidth = NewKeyBindsList.this.mc.font.width(this.labelText);
            this.name = name;
        }
        
        public String getName() {
            
            return name;
        }
        
        public void render(PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            NewKeyBindsList.this.minecraft.font.draw(stack, this.labelText, (float) (Objects.requireNonNull(minecraft.screen).width / 2 - this.labelWidth / 2), (float) (y + rowWidth - 9 - 1), 16777215);
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
        
        @Override
        protected void refreshEntry() {
        
        }
        
    }
    
    public class KeyEntry extends KeyBindsList.Entry {
        
        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyMapping key;
        /**
         * The localized key description for this KeyEntry
         */
        private final Component keyDesc;
        private final Button btnChangeKeyBinding;
        private final Button btnResetKeyBinding;
        
        private boolean hasCollision;
        
        private KeyEntry(final KeyMapping key, final Component keyDesc) {
            
            this.key = key;
            this.keyDesc = keyDesc;
            this.btnChangeKeyBinding = Button.builder(this.keyDesc, (btn) -> {
                        NewKeyBindsList.this.controlsScreen.selectedKey = key;
                        NewKeyBindsList.this.resetMappingAndUpdateButtons();
                    })
                    .bounds(0, 0, 75, 20)
                    .createNarration(supp -> key.isUnbound() ? Component.translatable("narrator.controls.unbound", keyDesc) : Component.translatable("narrator.controls.bound", keyDesc, supp.get()))
                    .build();
            
            this.btnResetKeyBinding = Button.builder(ControllingConstants.COMPONENT_CONTROLS_RESET, btn -> {
                        Services.PLATFORM.setToDefault(minecraft.options, key);
                        NewKeyBindsList.this.resetMappingAndUpdateButtons();
                    }).bounds(0, 0, 50, 20)
                    .createNarration(supp -> Component.translatable("narrator.controls.reset", keyDesc))
                    .build();
            
            refreshEntry();
        }
        
        @Override
        public void render(PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            Services.EVENT.fireKeyEntryRenderEvent(this, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks);
            int length = Math.max(0, x + 90 - NewKeyBindsList.this.maxListLabelWidth);
            NewKeyBindsList.this.mc.font.draw(stack, this.keyDesc, (float) (length), (float) (y + rowWidth / 2 - 9 / 2), 16777215);
            this.btnResetKeyBinding.setX(x + 190 + 20);
            this.btnResetKeyBinding.setY(y);
            this.btnResetKeyBinding.render(stack, mouseX, mouseY, partialTicks);
            
            this.btnChangeKeyBinding.setX(x + 105);
            this.btnChangeKeyBinding.setY(y);
            
            if(this.hasCollision) {
                int markerWidth = 3;
                int minX = this.btnChangeKeyBinding.getX() - 6;
                GuiComponent.fill(stack, minX, y + 2, minX + markerWidth, y + rowWidth + 2, ChatFormatting.RED.getColor() | -16777216);
            }
            this.btnChangeKeyBinding.render(stack, mouseX, mouseY, partialTicks);
        }
        
        public List<GuiEventListener> children() {
            
            return Services.EVENT.fireKeyEntryListenersEvent(this)
                    .map(IKeyEntryListenersEvent::getListeners, UnaryOperator.identity());
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
            return super.mouseClicked(mouseX, mouseY, buttonId);
        }
        
        
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int buttonId) {
            
            if(Services.EVENT.fireKeyEntryMouseReleasedEvent(this, mouseX, mouseY, buttonId)
                    .map(IKeyEntryMouseReleasedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }
            
            return super.mouseReleased(mouseX, mouseY, buttonId);
        }
        
        public KeyMapping getKey() {
            
            return key;
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
        
        @Override
        protected void refreshEntry() {
            
            this.btnChangeKeyBinding.setMessage(this.key.getTranslatedKeyMessage());
            this.btnResetKeyBinding.active = !this.key.isDefault();
            this.hasCollision = false;
            MutableComponent duplicates = Component.empty();
            if(!this.key.isUnbound()) {
                KeyMapping[] mappings = NewKeyBindsList.this.minecraft.options.keyMappings;
                
                for(KeyMapping mapping : mappings) {
                    if(mapping != this.key && this.key.same(mapping) || Services.PLATFORM.hasConflictingModifier(key, mapping)) {
                        if(this.hasCollision) {
                            duplicates.append(", ");
                        }
                        
                        this.hasCollision = true;
                        duplicates.append(Component.translatable(mapping.getName()));
                    }
                }
            }
            MutableComponent tooltip = Component.translatable(key.getCategory());
            if(this.hasCollision) {
                this.btnChangeKeyBinding.setMessage(Component.literal("[ ")
                        .append(this.btnChangeKeyBinding.getMessage().copy().withStyle(ChatFormatting.WHITE))
                        .append(" ]")
                        .withStyle(ChatFormatting.RED));
                tooltip.append(CommonComponents.NEW_LINE);
                tooltip.append(Component.translatable("controls.keybinds.duplicateKeybinds", duplicates));
            }
            this.btnChangeKeyBinding.setTooltip(Tooltip.create(tooltip));
            
            if(NewKeyBindsList.this.controlsScreen.selectedKey == this.key) {
                this.btnChangeKeyBinding.setMessage(Component.literal("> ")
                        .append(this.btnChangeKeyBinding.getMessage()
                                .copy()
                                .withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                        .append(" <")
                        .withStyle(ChatFormatting.YELLOW));
            }
            
        }
        
    }
    
}