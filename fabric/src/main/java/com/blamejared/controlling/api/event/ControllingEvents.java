package com.blamejared.controlling.api.event;

import net.fabricmc.fabric.api.event.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Unit;

import java.util.*;

public class ControllingEvents {
    
    public static final Event<IEventHandler<KeyEntryListenersEvent, List<GuiEventListener>>> KEY_ENTRY_LISTENERS_EVENT = EventFactory.createArrayBacked(IEventHandler.class, listeners -> event -> listeners.length == 0 ? event.getListeners() : Arrays.stream(listeners)
            .flatMap(handler -> handler.handle(event).stream())
            .toList());
    public static final Event<IEventHandler<KeyEntryMouseClickedEvent, Boolean>> KEY_ENTRY_MOUSE_CLICKED_EVENT = EventFactory.createArrayBacked(IEventHandler.class, listeners -> event -> listeners.length == 0 ? event.isHandled() : Arrays.stream(listeners)
            .anyMatch(handler -> handler.handle(event)));
    public static final Event<IEventHandler<KeyEntryMouseReleasedEvent, Boolean>> KEY_ENTRY_MOUSE_RELEASED_EVENT = EventFactory.createArrayBacked(IEventHandler.class, listeners -> event -> listeners.length == 0 ? event.isHandled() : Arrays.stream(listeners)
            .anyMatch(handler -> handler.handle(event)));
    public static final Event<IEventHandler<KeyEntryRenderEvent, Unit>> KEY_ENTRY_RENDER_EVENT = EventFactory.createArrayBacked(IEventHandler.class, listeners -> event -> {
        Arrays.stream(listeners).forEach(handler -> handler.handle(event));
        return Unit.INSTANCE;
    });
    
}
