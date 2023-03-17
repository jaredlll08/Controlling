package com.blamejared.controlling.platform;

import com.blamejared.controlling.api.event.ControllingEvents;
import com.blamejared.controlling.api.event.KeyEntryListenersEvent;
import com.blamejared.controlling.api.event.KeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.event.KeyEntryMouseReleasedEvent;
import com.blamejared.controlling.api.event.KeyEntryRenderEvent;
import com.blamejared.controlling.api.events.IKeyEntryListenersEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseReleasedEvent;
import com.blamejared.controlling.api.events.IKeyEntryRenderEvent;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Unit;

import java.util.List;

public class FabricEventHandler implements IEventHelper {
    
    @Override
    public Either<IKeyEntryListenersEvent, List<GuiEventListener>> fireKeyEntryListenersEvent(NewKeyBindsList.KeyEntry entry) {
        KeyEntryListenersEvent event = new KeyEntryListenersEvent(entry);
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            return Either.right(ControllingEvents.KEY_ENTRY_LISTENERS_EVENT.invoker().handle(event));
        }
    
        return Either.right(event.getListeners());
    }
    
    @Override
    public Either<IKeyEntryMouseClickedEvent, Boolean> fireKeyEntryMouseClickedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        KeyEntryMouseClickedEvent event = new KeyEntryMouseClickedEvent(entry, mouseX, mouseY, buttonId);
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            return Either.right(ControllingEvents.KEY_ENTRY_MOUSE_CLICKED_EVENT.invoker().handle(event));
        }
        
        return Either.right(event.isHandled());
    }
    
    @Override
    public Either<IKeyEntryMouseReleasedEvent, Boolean> fireKeyEntryMouseReleasedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        KeyEntryMouseReleasedEvent event = new KeyEntryMouseReleasedEvent(entry, mouseX, mouseY, buttonId);
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            return Either.right(ControllingEvents.KEY_ENTRY_MOUSE_RELEASED_EVENT.invoker().handle(event));
        }
        
        return Either.right(event.isHandled());
    }
    
    @Override
    public Either<IKeyEntryRenderEvent, Unit> fireKeyEntryRenderEvent(NewKeyBindsList.KeyEntry entry, PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            return Either.right(ControllingEvents.KEY_ENTRY_RENDER_EVENT.invoker().handle(new KeyEntryRenderEvent(entry, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks)));
        }
        return Either.right(Unit.INSTANCE);
    }
}
