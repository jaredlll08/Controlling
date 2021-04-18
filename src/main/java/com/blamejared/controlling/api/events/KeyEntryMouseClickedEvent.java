package com.blamejared.controlling.api.events;

import com.blamejared.controlling.client.gui.GuiNewKeyBindingList;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * KeyEntryMouseClickedEvent is called at the start of {@link GuiNewKeyBindingList.KeyEntry#mouseClicked(double, double, int)}.
 *
 * If you are consuming this event, call {@link KeyEntryMouseClickedEvent#setHandled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseClickedEvent extends Event {
    
    private final GuiNewKeyBindingList.KeyEntry entry;
    private final double mouseX;
    private final double mouseY;
    private final int buttonId;
    
    private boolean handled;
    
    public KeyEntryMouseClickedEvent(GuiNewKeyBindingList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        
        this.entry = entry;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.buttonId = buttonId;
    }
    
    public GuiNewKeyBindingList.KeyEntry getEntry() {
        
        return entry;
    }
    
    public double getMouseX() {
        
        return mouseX;
    }
    
    public double getMouseY() {
        
        return mouseY;
    }
    
    public int getButtonId() {
        
        return buttonId;
    }
    
    public boolean isHandled() {
        
        return handled;
    }
    
    public void setHandled(boolean handled) {
        
        this.handled = handled;
    }
    
}
