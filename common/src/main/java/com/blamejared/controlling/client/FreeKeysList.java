package com.blamejared.controlling.client;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.entries.IInputEntry;
import com.blamejared.controlling.mixin.AccessInputConstantsKey;
import com.blamejared.controlling.mixin.AccessKeyMapping;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FreeKeysList extends CustomList {
    
    private final KeyBindsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    private final List<KeyMapping> keyBindings;
    
    public FreeKeysList(KeyBindsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
        this.height -= 52;
        this.setY(48);
        this.controlsScreen = controls;
        this.mc = mcIn;
        children().clear();
        this.allEntries = new ArrayList<>();
        this.keyBindings = Arrays.stream(mc.options.keyMappings).collect(Collectors.toList());
        
        recalculate();
    }
    
    @Override
    public int getBottom() {
        
        return this.controlsScreen.height - 56;
    }
    
    @Override
    public int getRight() {
        
        return this.controlsScreen.width + 45;
    }
    
    public void recalculate() {
        
        children().clear();
        allEntries.clear();
        
        addEntry(new HeaderEntry("Available Keys"));
        AccessInputConstantsKey.controlling$getNAME_MAP()
                .values()
                .stream()
                .filter(input -> !input.getName().startsWith("key.keyboard.world"))
                .filter(Predicate.not(InputConstants.UNKNOWN::equals))
                .sorted(Comparator.comparing(o -> o.getDisplayName().getString()))
                .forEach(input -> {
                    if(keyBindings.stream()
                            .noneMatch(keyBinding -> ((AccessKeyMapping) keyBinding).controlling$getKey()
                                    .equals(input))) {
                        int i = mc.font.width(input.getDisplayName().getString());
                        if(i > this.maxListLabelWidth) {
                            this.maxListLabelWidth = i;
                        }
                        addEntry(new InputEntry(input));
                    }
                });
    }
    
    @Override
    protected int getScrollbarPosition() {
        
        return super.getScrollbarPosition() + 15 + 20;
    }
    
    @Override
    public int getRowWidth() {
        
        return super.getRowWidth() + 32;
    }
    
    public class InputEntry extends Entry implements IInputEntry {
        private final InputConstants.Key input;
        
        public InputEntry(InputConstants.Key input) {
            
            this.input = input;
        }
        
        public InputConstants.Key getInput() {
            
            return input;
        }
        
        @Override
        public void render(GuiGraphics guiGraphics, int slotIndex, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            String str = this.input.toString() + " - " + input.getValue();
            int length = mc.font.width(input.getDisplayName().getString());
            
            guiGraphics.drawString(FreeKeysList.this.mc.font, str, x,  y + height / 2 - 9 / 2, 16777215);
            guiGraphics.renderTooltip(FreeKeysList.this.mc.font, input.getDisplayName(), x + width - (length), y + height);
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of();
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            
            return ImmutableList.of();
        }
    
        @Override
        protected void refreshEntry() {
        
        }
    
    }
    
    public class HeaderEntry extends Entry {
        private final String text;
        
        public HeaderEntry(String text) {
            
            this.text = text;
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of();
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            
            return ImmutableList.of();
        }
        
        @Override
        public void render(GuiGraphics guiGraphics, int slotIndex, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            guiGraphics.drawCenteredString(mc.font, ControllingConstants.COMPONENT_OPTIONS_AVAILABLE_KEYS, (Objects.requireNonNull(mc.screen).width / 2 - this.text.length() / 2), (y + height - 9 - 1), 16777215);
        }
    
        @Override
        protected void refreshEntry() {
        
        }
    
    }
    
}