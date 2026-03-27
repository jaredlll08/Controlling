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
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public class NeoForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IKeyEntryListenersEvent, List<GuiEventListener>> fireKeyEntryListenersEvent(IKeyEntry entry) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryListenersEvent(entry)));
    }
    
    @Override
    public Either<IKeyEntryMouseClickedEvent, Boolean> fireKeyEntryMouseClickedEvent(IKeyEntry entry, MouseButtonEvent event, boolean doubleClick) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryMouseClickedEvent(entry, event, doubleClick)));
    }
    
    @Override
    public Either<IKeyEntryMouseReleasedEvent, Boolean> fireKeyEntryMouseReleasedEvent(IKeyEntry entry, MouseButtonEvent event) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryMouseReleasedEvent(entry, event)));
    }
    
    @Override
    public Either<IKeyEntryRenderEvent, Unit> fireKeyEntryRenderEvent(IKeyEntry entry, GuiGraphicsExtractor graphics, int x, int y, int rowLeft, int rowWidth, boolean hovered, float partialTicks) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new KeyEntryRenderEvent(entry, graphics, x, y, rowLeft, rowWidth, hovered, partialTicks)));
    }
    
}
