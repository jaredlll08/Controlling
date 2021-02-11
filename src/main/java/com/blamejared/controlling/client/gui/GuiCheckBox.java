package com.blamejared.controlling.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.*;

/**
 * This class provides a checkbox style control.
 */
public class GuiCheckBox extends Button {
    
    private boolean isChecked;
    private int boxWidth;
    
    public GuiCheckBox(int xPos, int yPos, String displayString, boolean isChecked) {
        super(xPos, yPos, Minecraft.getInstance().fontRenderer.getStringWidth(displayString) + 2 + 11, 11, new StringTextComponent(displayString), b -> {
        });
        this.isChecked = isChecked;
        this.boxWidth = 11;
        this.height = 11;
        this.width = this.boxWidth + 2 + Minecraft.getInstance().fontRenderer.getStringWidth(displayString);
    }
    
    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partial) {
        if(this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = active && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if(isHovered()) {
                GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 86, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, 500);
                GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x+1, this.y+1, 2, 48, this.boxWidth-2, this.height-2, 200, 20, 1, 0, 1, 0, 500);
            } else {
                GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, 500);
            }
            int color = 14737632;
            
            if(packedFGColor != 0) {
                color = packedFGColor;
            }
            if(!this.active) {
                color = 10526880;
            }
            
            if(this.isChecked)
                drawCenteredString(stack, mc.fontRenderer, "x", this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);
            mc.fontRenderer.func_238407_a_(stack, getMessage().func_241878_f(), this.x + this.boxWidth + 2, this.y + 2, color);
        }
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