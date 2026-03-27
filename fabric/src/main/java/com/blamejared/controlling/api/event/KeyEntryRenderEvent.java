package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryRenderEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * RenderKeyEntryEvent is called at the top of {@link IKeyEntry#extractContent(GuiGraphicsExtractor, int, int, boolean, float)}
 * is called, allowing mods to render additional info.
 */
public record KeyEntryRenderEvent(IKeyEntry entry, GuiGraphicsExtractor graphics, int x, int y, int rowLeft,
                                  int rowWidth, boolean hovered, float partialTicks) implements IKeyEntryRenderEvent {
    
}
