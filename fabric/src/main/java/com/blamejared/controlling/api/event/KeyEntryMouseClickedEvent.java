package com.blamejared.controlling.api.event;

import com.blamejared.controlling.api.events.IKeyEntryMouseClickedEvent;
import com.blamejared.controlling.client.NewKeyBindsList;


/**
 * KeyEntryMouseClickedEvent is called at the start of {@link NewKeyBindsList.KeyEntry#mouseClicked(double, double, int)}.
 * <p>
 * If you are consuming this event, call {@link KeyEntryMouseClickedEvent#setHandled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseClickedEvent implements IKeyEntryMouseClickedEvent {
    
    private final NewKeyBindsList.KeyEntry entry;
    private final double mouseX;
    private final double mouseY;
    private final int buttonId;
    
    private boolean handled;
    
    public KeyEntryMouseClickedEvent(NewKeyBindsList.KeyEntry entry, double mouseX, double mouseY, int buttonId) {
        
        this.entry = entry;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.buttonId = buttonId;
    }
    
    public NewKeyBindsList.KeyEntry getEntry() {
        
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
