package com.blamejared.controlling.api.events;

import com.blamejared.controlling.api.entries.IKeyEntry;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;


/**
 * KeyEntryMouseClickedEvent is called at the start of {@link IKeyEntry#mouseClicked(double, double, int)}.
 * <p>
 * If you are consuming this event, call {@link KeyEntryMouseClickedEvent#setHandled(boolean)} with a value of {@code true}.
 */
public class KeyEntryMouseClickedEvent extends MutableEvent implements IKeyEntryMouseClickedEvent {
    
    public static final EventBus<KeyEntryMouseClickedEvent> BUS = EventBus.create(KeyEntryMouseClickedEvent.class);
    
    private final IKeyEntry entry;
    private final double mouseX;
    private final double mouseY;
    private final int buttonId;
    
    private boolean handled;
    
    public KeyEntryMouseClickedEvent(IKeyEntry entry, double mouseX, double mouseY, int buttonId) {
        
        this.entry = entry;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.buttonId = buttonId;
    }
    
    public IKeyEntry getEntry() {
        
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
