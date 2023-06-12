package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.IKeyEntryRenderEvent;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

/**
 * RenderKeyEntryEvent is called at the top of {@link NewKeyBindsList.KeyEntry#render(GuiGraphics, int, int, int, int, int, int, int, boolean, float)}
 * is called, allowing mods to render additional info.
 */
public class KeyEntryRenderEvent implements IKeyEntryRenderEvent {
    
    private final NewKeyBindsList.KeyEntry entry;
    
    private final GuiGraphics guiGraphics;
    private final int slotIndex;
    private final int y;
    private final int x;
    private final int rowLeft;
    private final int rowWidth;
    private final int mouseX;
    private final int mouseY;
    private final boolean hovered;
    private final float partialTicks;
    
    public KeyEntryRenderEvent(NewKeyBindsList.KeyEntry entry, GuiGraphics guiGraphics, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        
        this.entry = entry;
        this.guiGraphics = guiGraphics;
        this.slotIndex = slotIndex;
        this.y = y;
        this.x = x;
        this.rowLeft = rowLeft;
        this.rowWidth = rowWidth;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.hovered = hovered;
        this.partialTicks = partialTicks;
    }
    
    public NewKeyBindsList.KeyEntry getEntry() {
        
        return entry;
    }
    
    public GuiGraphics getGuiGraphics() {
        
        return guiGraphics;
    }
    
    public int getSlotIndex() {
        
        return slotIndex;
    }
    
    public int getY() {
        
        return y;
    }
    
    public int getX() {
        
        return x;
    }
    
    public int getRowLeft() {
        
        return rowLeft;
    }
    
    public int getRowWidth() {
        
        return rowWidth;
    }
    
    public int getMouseX() {
        
        return mouseX;
    }
    
    public int getMouseY() {
        
        return mouseY;
    }
    
    public boolean isHovered() {
        
        return hovered;
    }
    
    public float getPartialTicks() {
        
        return partialTicks;
    }
    
}
