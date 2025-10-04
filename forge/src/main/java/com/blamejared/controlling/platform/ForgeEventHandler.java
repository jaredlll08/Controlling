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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IKeyEntryListenersEvent, List<GuiEventListener>> fireKeyEntryListenersEvent(IKeyEntry entry) {
        
        KeyEntryListenersEvent event = new KeyEntryListenersEvent(entry);
        KeyEntryListenersEvent.BUS.post(event);
        return Either.left(event);
    }
    
    @Override
    public Either<IKeyEntryMouseClickedEvent, Boolean> fireKeyEntryMouseClickedEvent(IKeyEntry entry, MouseButtonEvent event, boolean doubleClick) {
        
        KeyEntryMouseClickedEvent clickEvent = new KeyEntryMouseClickedEvent(entry, event, doubleClick);
        KeyEntryMouseClickedEvent.BUS.post(clickEvent);
        return Either.left(clickEvent);
    }
    
    @Override
    public Either<IKeyEntryMouseReleasedEvent, Boolean> fireKeyEntryMouseReleasedEvent(IKeyEntry entry, MouseButtonEvent event) {
        
        KeyEntryMouseReleasedEvent releaseEvent = new KeyEntryMouseReleasedEvent(entry, event);
        KeyEntryMouseReleasedEvent.BUS.post(releaseEvent);
        return Either.left(releaseEvent);
    }
    
    @Override
    public Either<IKeyEntryRenderEvent, Unit> fireKeyEntryRenderEvent(IKeyEntry entry, GuiGraphics guiGraphics, int x, int y, int rowLeft, int rowWidth, boolean hovered, float partialTicks) {
        
        KeyEntryRenderEvent event = new KeyEntryRenderEvent(entry, guiGraphics, y, x, rowLeft, rowWidth, hovered, partialTicks);
        KeyEntryRenderEvent.BUS.post(event);
        return Either.left(event);
    }
    
}
