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
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

public class FabricEventHandler implements IEventHelper {
    
    @Override
    public Either<IKeyEntryListenersEvent, List<GuiEventListener>> fireKeyEntryListenersEvent(NewKeyBindsList.KeyEntry entry) {
        return Either.right(ControllingEvents.KEY_ENTRY_LISTENERS_EVENT.invoker().handle(new KeyEntryListenersEvent(entry)));
    }
    
    @Override
    public Either<IKeyEntryMouseClickedEvent, Boolean> fireKeyEntryMouseClickedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        return Either.right(ControllingEvents.KEY_ENTRY_MOUSE_CLICKED_EVENT.invoker().handle(new KeyEntryMouseClickedEvent(entry, mouseX, mouseY, buttonId)));
    }
    
    @Override
    public Either<IKeyEntryMouseReleasedEvent, Boolean> fireKeyEntryMouseReleasedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        return Either.right(ControllingEvents.KEY_ENTRY_MOUSE_RELEASED_EVENT.invoker().handle(new KeyEntryMouseReleasedEvent(entry, mouseX, mouseY, buttonId)));
    }
    
    @Override
    public Either<IKeyEntryRenderEvent, Void> fireKeyEntryRenderEvent(NewKeyBindsList.KeyEntry entry, PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        return Either.right(ControllingEvents.KEY_ENTRY_RENDER_EVENT.invoker().handle(new KeyEntryRenderEvent(entry, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks)));
    }
}
