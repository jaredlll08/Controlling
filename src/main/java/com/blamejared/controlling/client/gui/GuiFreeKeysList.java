package com.blamejared.controlling.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import sun.security.jca.GetInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GuiFreeKeysList extends GuiCustomList {
    
    private final ControlsScreen controlsScreen;
    private final Minecraft mc;
    private int maxListLabelWidth;
    
    List<KeyBinding> keyBindings;
    
    public GuiFreeKeysList(ControlsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
        this.width = controls.width + 45;
        this.height = controls.height;
        this.y0 = 43;
        this.y1 = controls.height - 80;
        this.x1 = controls.width + 45;
        this.controlsScreen = controls;
        this.mc = mcIn;
        getEventListeners().clear();
        allEntries = new ArrayList<>();
        keyBindings = Arrays.stream(mc.gameSettings.keyBindings).collect(Collectors.toList());
        
        recalculate();
        
    }
    
    public void recalculate() {
        
        getEventListeners().clear();
        allEntries.clear();
        
        add(new HeaderEntry("Available Keys"));
        InputMappings.Input.REGISTRY.values().stream().filter(input -> {
            return !input.toString().startsWith("key.keyboard.world");
        }).sorted(Comparator.comparing(o -> o.func_237520_d_().getString())).forEach(input -> {
            if(keyBindings.stream().noneMatch(keyBinding -> keyBinding.getKey().equals(input))) {
                int i = mc.fontRenderer.getStringWidth(input.func_237520_d_().getString());
                if(i > this.maxListLabelWidth) {
                    this.maxListLabelWidth = i;
                }
                add(new InputEntry(input));
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
    
    @OnlyIn(Dist.CLIENT)
    public class InputEntry extends Entry {
        
        private final InputMappings.Input input;
        
        public InputEntry(InputMappings.Input input) {
            
            this.input = input;
        }
        
        @Override
        public List<? extends IGuiEventListener> getEventListeners() {
            
            return ImmutableList.of();
        }
    
        public InputMappings.Input getInput() {
        
            return input;
        }
    
        @Override
        public void render(MatrixStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            
            String str = this.input.toString() + " - " + input.getKeyCode();// + " - " + input.func_237520_d_().getString() + " - " + input.getKeyCode();
            int length = mc.fontRenderer.getStringWidth(input.func_237520_d_().getString());
            
            GuiFreeKeysList.this.mc.fontRenderer.drawString(stack, str, x, (float) (y + p_render_5_ / 2 - 9 / 2), 16777215);
            GuiUtils.drawHoveringText(stack, Collections.singletonList(input.func_237520_d_()), x + p_render_4_ - (length), y + p_render_5_, mc.currentScreen.width, mc.currentScreen.height, -1, mc.fontRenderer);
            
        }
        
    }
    
    @OnlyIn(Dist.CLIENT)
    public class HeaderEntry extends Entry {
        
        private final String text;
        
        public HeaderEntry(String text) {
            
            this.text = text;
        }
        
        @Override
        public List<? extends IGuiEventListener> getEventListeners() {
            
            return ImmutableList.of();
        }
        
        @Override
        public void render(MatrixStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            
            drawString(stack, mc.fontRenderer, new TranslationTextComponent("options.availableKeys"),  (mc.currentScreen.width / 2 - this.text.length() / 2), (y + p_render_5_ - 9 - 1), 16777215);
        }
        
    }
    
}