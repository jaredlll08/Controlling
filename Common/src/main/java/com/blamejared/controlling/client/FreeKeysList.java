package com.blamejared.controlling.client;

import com.blamejared.controlling.mixin.AccessInputConstantsKey;
import com.blamejared.controlling.mixin.AccessKeyMapping;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FreeKeysList extends CustomList {
    
    private final KeyBindsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    List<KeyMapping> keyBindings;
    
    public FreeKeysList(KeyBindsScreen controls, Minecraft mcIn) {
        
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
        AccessInputConstantsKey.getNAME_MAP().values().stream().filter(input -> !input.toString().startsWith("key.keyboard.world")).sorted(Comparator.comparing(o -> o.getDisplayName().getString())).forEach(input -> {
            if(keyBindings.stream().noneMatch(keyBinding -> ((AccessKeyMapping) keyBinding).getKey().equals(input))) {
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
        public void render(PoseStack stack, int slotIndex, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            String str = this.input.toString() + " - " + input.getValue();
            int length = mc.font.width(input.getDisplayName().getString());
            
            FreeKeysList.this.mc.font.draw(stack, str, x, (float) (y + height / 2 - 9 / 2), 16777215);
            controlsScreen.renderTooltip(stack, input.getDisplayName(), x + width - (length), y + height);
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
        public void render(PoseStack stack, int slotIndex, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            
            drawCenteredString(stack, mc.font, new TranslatableComponent("options.availableKeys"), (Objects.requireNonNull(mc.screen).width / 2 - this.text.length() / 2), (y + height - 9 - 1), 16777215);
        }
        
    }
    
}