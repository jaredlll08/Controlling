package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.entries.ICategoryEntry;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryListenersEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseReleasedEvent;
import com.blamejared.controlling.platform.Services;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.CommonColors;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class NewKeyBindsList extends CustomList {
    
    private final KeyBindsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    public NewKeyBindsList(KeyBindsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
        this.height -= 52;
        this.setY(48);
        this.controlsScreen = controls;
        this.mc = mcIn;
        this.clearEntries();
        allEntries = new ArrayList<>();
        KeyMapping[] bindings = ArrayUtils.clone(mcIn.options.keyMappings);
        Arrays.sort(bindings);
        KeyMapping.Category lastCategory = null;
        
        for(KeyMapping keybinding : bindings) {
            KeyMapping.Category category = keybinding.getCategory();
            if(!category.equals(lastCategory)) {
                lastCategory = category;
                if(!isHidden(category.label())) {
                    addEntry(new NewKeyBindsList.CategoryEntry(category));
                }
            }
            
            Component component = Services.PLATFORM.getKeyName(keybinding);
            int width = mcIn.font.width(component);
            if(width > this.maxListLabelWidth) {
                this.maxListLabelWidth = width;
            }
            if(!isHidden(category.label())) {
                addEntry(new NewKeyBindsList.KeyEntry(keybinding, component));
            }
        }
        
    }
    
    private boolean isHidden(Component component) {
        
        if(component.getContents() instanceof TranslatableContents tc) {
            return tc.getKey().endsWith(".hidden");
        }
        return false;
    }
    
    @Override
    public int getBottom() {
        
        return this.controlsScreen.height - 56;
    }
    
    public class CategoryEntry extends Entry implements ICategoryEntry {
        
        private final KeyMapping.Category category;
        private final FocusableTextWidget categoryName;
        
        public CategoryEntry(KeyMapping.Category category) {
            
            this.category = category;
            this.categoryName = new FocusableTextWidget(
                    NewKeyBindsList.this.getRowWidth(), category.label(), NewKeyBindsList.this.minecraft.font, false, FocusableTextWidget.BackgroundFill.ON_FOCUS, 4
            );
        }
        
        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            this.categoryName.setPosition(NewKeyBindsList.this.width / 2 - this.categoryName.getWidth() / 2, this.getContentBottom() - 9 - 1);
            this.categoryName.render(guiGraphics, mouseX, mouseY, partialTicks);
            //            guiGraphics.drawString(NewKeyBindsList.this.mc.font, this.name, Objects.requireNonNull(minecraft.screen).width / 2 - this.labelWidth / 2, this.getY() + getRowWidth() - 9 - 1, -1);
        }
        
        public List<? extends NarratableEntry> narratables() {
            
            return List.of(this.categoryName);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            
            return List.of(this.categoryName);
        }
        
        @Override
        protected void refreshEntry() {
            
        }
        
        public FocusableTextWidget categoryName() {
            
            return categoryName;
        }
        
        @Override
        public KeyMapping.Category category() {
            
            return this.category;
        }
        
    }
    
    public class KeyEntry extends KeyBindsList.Entry implements IKeyEntry {
        
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
        
        private final Component categoryName;
        
        public KeyEntry(final KeyMapping key, final Component keyDesc) {
            
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
            
            this.categoryName = this.key.getCategory().label();
            refreshEntry();
        }
        
        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            Services.EVENT.fireKeyEntryRenderEvent(this, guiGraphics, this.getContentX(), this.getContentY(), getRowLeft(), getRowWidth(), hovered, partialTicks);
            
            int resetKeyX = NewKeyBindsList.this.scrollBarX() - this.btnResetKeyBinding.getWidth() - 10;
            int top = this.getContentY() - 2;
            this.btnResetKeyBinding.setPosition(resetKeyX, top);
            this.btnResetKeyBinding.render(guiGraphics, mouseX, mouseY, partialTicks);
            
            this.btnChangeKeyBinding.setPosition(resetKeyX - 5 - this.btnChangeKeyBinding.getWidth(), top);
            this.btnChangeKeyBinding.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.drawString(NewKeyBindsList.this.mc.font, this.keyDesc, this.getContentX(), this.getContentYMiddle() - 9 / 2, -1);
            
            if(this.hasCollision) {
                int markerWidth = 3;
                int minX = this.btnChangeKeyBinding.getX() - 6;
                guiGraphics.fill(minX, this.getContentY() - 1, minX + markerWidth, this.getContentBottom(), CommonColors.YELLOW);
            }
        }
        
        public List<GuiEventListener> children() {
            
            return Services.EVENT.fireKeyEntryListenersEvent(this)
                    .map(IKeyEntryListenersEvent::getListeners, UnaryOperator.identity());
        }
        
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of(this.btnChangeKeyBinding, this.btnResetKeyBinding);
        }
        
        
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            
            if(Services.EVENT.fireKeyEntryMouseClickedEvent(this, event, doubleClick)
                    .map(IKeyEntryMouseClickedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }
        
        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            
            if(Services.EVENT.fireKeyEntryMouseReleasedEvent(this, event)
                    .map(IKeyEntryMouseReleasedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }
            
            return super.mouseReleased(event);
        }
        
        public KeyMapping getKey() {
            
            return key;
        }
        
        public Component getKeyDesc() {
            
            return keyDesc;
        }
        
        public Component categoryName() {
            
            return categoryName;
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
                        duplicates.append(Services.PLATFORM.getKeyName(mapping));
                    }
                }
            }
            MutableComponent tooltip = categoryName.copy();
            if(this.hasCollision) {
                this.btnChangeKeyBinding.setMessage(Component.literal("[ ")
                        .append(this.btnChangeKeyBinding.getMessage().copy().withStyle(ChatFormatting.WHITE))
                        .append(" ]")
                        .withStyle(ChatFormatting.YELLOW));
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