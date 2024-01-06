package com.blamejared.controlling.platform;

import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryListenersEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseReleasedEvent;
import com.blamejared.controlling.api.events.IKeyEntryRenderEvent;
import com.blamejared.controlling.api.events.KeyEntryListenersEvent;
import com.blamejared.controlling.api.events.KeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.KeyEntryMouseReleasedEvent;
import com.blamejared.controlling.api.events.KeyEntryRenderEvent;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public class NeoForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IKeyEntryListenersEvent, List<GuiEventListener>> fireKeyEntryListenersEvent(IKeyEntry entry) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryListenersEvent(entry)));
    }
    
    @Override
    public Either<IKeyEntryMouseClickedEvent, Boolean> fireKeyEntryMouseClickedEvent(IKeyEntry entry, double mouseX, double mouseY, int buttonId) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryMouseClickedEvent(entry, mouseX, mouseY, buttonId)));
    }
    
    @Override
    public Either<IKeyEntryMouseReleasedEvent, Boolean> fireKeyEntryMouseReleasedEvent(IKeyEntry entry, double mouseX, double mouseY, int buttonId) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryMouseReleasedEvent(entry, mouseX, mouseY, buttonId)));
    }
    
    @Override
    public Either<IKeyEntryRenderEvent, Unit> fireKeyEntryRenderEvent(IKeyEntry entry, GuiGraphics guiGraphics, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryRenderEvent(entry, guiGraphics, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks)));
    }
    
}
