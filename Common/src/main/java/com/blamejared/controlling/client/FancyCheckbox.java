package com.blamejared.controlling.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FancyCheckbox extends AbstractButton {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private static final int TEXT_COLOR = 14737632;
    private boolean selected;
    private final OnPress pressFunction;
    
    public FancyCheckbox(int x, int y, int width, int height, Component label, boolean selected, OnPress pressFunction) {
        super(x, y, Minecraft.getInstance().font.width(label) + 2 + width, height, label);
        this.selected = selected;
        this.pressFunction = pressFunction;
    }
    
    public void onPress() {
        this.selected = !this.selected;
        if(pressFunction != null) {
            pressFunction.onPress(this);
        }
    }
    
    public void selected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean selected() {
        return this.selected;
    }
    
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if(this.active) {
            if(this.isFocused()) {
                elementOutput.add(NarratedElementType.USAGE, new TranslatableComponent("narration.checkbox.usage.focused"));
            } else {
                elementOutput.add(NarratedElementType.USAGE, new TranslatableComponent("narration.checkbox.usage.hovered"));
            }
        }
        
    }
    
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(stack, this.x, this.y, this.isHoveredOrFocused() ? 11 : 0.0F, this.selected ? 11 : 0.0F, 11, this.height, (int) (64f * (11f / 20f)), (int) (64f * (11f / 20f)));
        this.renderBg(stack, mc, mouseX, mouseY);
        drawString(stack, mc.font, this.getMessage(), this.x + 11 + 2, this.y + (this.height - 8) / 2, TEXT_COLOR | Mth.ceil(this.alpha * 255.0F) << 24);
        
    }
    
    public interface OnPress {
        
        void onPress(FancyCheckbox button);
    }
}
