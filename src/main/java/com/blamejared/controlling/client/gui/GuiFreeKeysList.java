package com.blamejared.controlling.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiFreeKeysList extends GuiCustomList {
    
    private final KeyBindsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    List<KeyMapping> keyBindings;
    
    public GuiFreeKeysList(KeyBindsScreen controls, Minecraft mcIn) {
        
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
        keyBindings = Arrays.stream(mc.options.keyMappings).collect(Collectors.toList());
        
        recalculate();
        
    }
    
    public void recalculate() {
        
        children().clear();
        allEntries.clear();
        
        addEntry(new HeaderEntry("Available Keys"));
        InputConstants.Key.NAME_MAP.values().stream().filter(input ->
            !input.toString().startsWith("key.keyboard.world")
        ).sorted(Comparator.comparing(o -> o.getDisplayName().getString())).forEach(input -> {
            if(keyBindings.stream().noneMatch(keyBinding -> keyBinding.getKey().equals(input))) {
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
    
    public class InputEntry extends Entry {
        
        private final InputConstants.Key input;
        
        public InputEntry(InputConstants.Key input) {
            
            this.input = input;
        }
        
        public InputConstants.Key getInput() {
            
            return input;
        }
        
        @Override
        public void render(PoseStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            
            String str = this.input.toString() + " - " + input.getValue();
            int length = mc.font.width(input.getDisplayName().getString());
            
            GuiFreeKeysList.this.mc.font.draw(stack, str, x, (float) (y + p_render_5_ / 2 - 9 / 2), 16777215);
            controlsScreen.renderComponentTooltip(stack, Collections.singletonList(input.getDisplayName()), x + p_render_4_ - (length), y + p_render_5_, mc.font);
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            
            return ImmutableList.of();
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            
            return ImmutableList.of();
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
        public void render(PoseStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            
            drawCenteredString(stack, mc.font, new TranslatableComponent("options.availableKeys"), (mc.screen.width / 2 - this.text
                    .length() / 2), (y + p_render_5_ - 9 - 1), 16777215);
        }
        
    }
    
}