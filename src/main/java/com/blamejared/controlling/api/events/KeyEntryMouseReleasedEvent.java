package com.blamejared.controlling.api.events;

import com.blamejared.controlling.client.gui.GuiNewKeyBindingList;
import net.minecraftforge.eventbus.api.Event;

/**
 * KeyEntryMouseReleasedEvent is called at the start of {@link GuiNewKeyBindingList.KeyEntry#mouseReleased(double, double, int)}.
 *
 * If you are consuming this event, call {@link KeyEntryMouseReleasedEvent#setHandled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseReleasedEvent extends Event {
    
    private final GuiNewKeyBindingList.KeyEntry entry;
    private final double mouseX;
    private final double mouseY;
    private final int buttonId;
    
    private boolean handled;
    
    public KeyEntryMouseReleasedEvent(GuiNewKeyBindingList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        
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
