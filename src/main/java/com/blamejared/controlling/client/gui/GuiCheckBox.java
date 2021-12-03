package com.blamejared.controlling.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;

/**
 * This class provides a checkbox style control.
 */
public class GuiCheckBox extends Button {
    
    private boolean isChecked;
    private int boxWidth;
    
    public GuiCheckBox(int xPos, int yPos, String displayString, boolean isChecked) {
        
        super(xPos, yPos, Minecraft.getInstance().font.width(displayString) + 2 + 11, 11, new TextComponent(displayString), b -> {
        });
        this.isChecked = isChecked;
        this.boxWidth = 11;
        this.height = 11;
        this.width = this.boxWidth + 2 + Minecraft.getInstance().font.width(displayString);
    }
    
    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partial) {
        
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        this.isHovered = active && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if(isHoveredOrFocused()) {
            GuiUtils.drawContinuousTexturedBox(stack, this.x, this.y, 0, 86, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, 0);
            GuiUtils.drawContinuousTexturedBox(stack, this.x + 1, this.y + 1, 2, 48, this.boxWidth - 2, this.height - 2, 200, 20, 1, 0, 1, 0, 0);
        } else {
            GuiUtils.drawContinuousTexturedBox(stack, this.x, this.y, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, 0);
        }
        if(this.isChecked) {
            drawCenteredString(stack, font, "x", this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);
        }
        int color = 14737632;
        if(packedFGColor != 0) {
            color = packedFGColor;
        }
        if(!this.active) {
            color = 10526880;
        }
        minecraft.font.draw(stack, getMessage(), this.x + this.boxWidth + 2, this.y + 2, color);
    }
    
    @Override
    public void onPress() {
        
        this.isChecked = !this.isChecked;
    }
    
    public boolean isChecked() {
        
        return this.isChecked;
    }
    
    public void setIsChecked(boolean isChecked) {
        
        this.isChecked = isChecked;
    }
    
}