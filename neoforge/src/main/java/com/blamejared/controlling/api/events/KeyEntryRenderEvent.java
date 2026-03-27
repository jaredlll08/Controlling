package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.bus.api.Event;

/**
 * RenderKeyEntryEvent is called at the top of {@link IKeyEntry#extractContent(GuiGraphicsExtractor, int, int, boolean, float)}}
 * is called, allowing mods to render additional info.
 */
public class KeyEntryRenderEvent extends Event implements IKeyEntryRenderEvent {
    
    private final IKeyEntry entry;
    
    private final GuiGraphicsExtractor extractor;
    private final int x;
    private final int y;
    private final int rowLeft;
    private final int rowWidth;
    private final boolean hovered;
    private final float partialTicks;
    
    public KeyEntryRenderEvent(IKeyEntry entry, GuiGraphicsExtractor graphics, int x, int y, int rowLeft, int rowWidth, boolean hovered, float partialTicks) {
        
        this.entry = entry;
        this.extractor = graphics;
        this.x = x;
        this.y = y;
        this.rowLeft = rowLeft;
        this.rowWidth = rowWidth;
        this.hovered = hovered;
        this.partialTicks = partialTicks;
    }
    
    public IKeyEntry entry() {
        
        return entry;
    }
    
    public GuiGraphicsExtractor graphics() {
        
        return extractor;
    }
    
    public int y() {
        
        return y;
    }
    
    public int x() {
        
        return x;
    }
    
    public int rowLeft() {
        
        return rowLeft;
    }
    
    public int rowWidth() {
        
        return rowWidth;
    }
    
    public boolean hovered() {
        
        return hovered;
    }
    
    public float partialTicks() {
        
        return partialTicks;
    }
    
}
