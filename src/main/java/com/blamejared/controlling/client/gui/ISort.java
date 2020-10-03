package com.blamejared.controlling.client.gui;

import net.minecraft.client.gui.GuiListExtended;

import java.util.List;

public interface ISort {
    void sort(List<GuiListExtended.IGuiListEntry> entries);
}
