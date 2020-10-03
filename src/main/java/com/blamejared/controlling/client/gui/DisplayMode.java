package com.blamejared.controlling.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.function.Predicate;

public enum DisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> keyEntry.getKeybinding().getKeyCode() == 0), CONFLICTING(keyEntry -> {
        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
            if (!key.getKeyDescription()
                .equals(keyEntry.getKeybinding().getKeyDescription()) && key.getKeyCode() != 0) {
                if (key.getKeyCode() == keyEntry.getKeybinding().getKeyCode()) {
                    return true;
                }
            }
        }
        return false;
    });

    private final Predicate<GuiNewKeyBindingList.KeyEntry> predicate;

    DisplayMode(Predicate<GuiNewKeyBindingList.KeyEntry> predicate) {
        this.predicate = predicate;
    }

    public Predicate<GuiNewKeyBindingList.KeyEntry> getPredicate() {
        return predicate;
    }
}
