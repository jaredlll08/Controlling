package com.blamejared.controlling.platform;

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
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IKeyEntryListenersEvent, List<GuiEventListener>> fireKeyEntryListenersEvent(NewKeyBindsList.KeyEntry entry) {
        KeyEntryListenersEvent event = new KeyEntryListenersEvent(entry);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
    
    @Override
    public Either<IKeyEntryMouseClickedEvent, Boolean> fireKeyEntryMouseClickedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        KeyEntryMouseClickedEvent event = new KeyEntryMouseClickedEvent(entry, mouseX, mouseY, buttonId);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
    
    @Override
    public Either<IKeyEntryMouseReleasedEvent, Boolean> fireKeyEntryMouseReleasedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        KeyEntryMouseReleasedEvent event = new KeyEntryMouseReleasedEvent(entry, mouseX, mouseY, buttonId);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
    
    @Override
    public Either<IKeyEntryRenderEvent, Unit> fireKeyEntryRenderEvent(NewKeyBindsList.KeyEntry entry, PoseStack stack, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        KeyEntryRenderEvent event = new KeyEntryRenderEvent(entry, stack, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
}
