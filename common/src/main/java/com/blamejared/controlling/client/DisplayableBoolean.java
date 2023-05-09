package com.blamejared.controlling.client;

import net.minecraft.network.chat.Component;

public class DisplayableBoolean {
    
    private boolean state;
    private final Component whenTrue;
    private final Component whenFalse;
    
    public DisplayableBoolean(boolean initialState, Component whenTrue, Component whenFalse) {
        
        this.state = initialState;
        this.whenTrue = whenTrue;
        this.whenFalse = whenFalse;
    }
    
    public boolean state() {
        
        return state;
    }
    
    public boolean toggle(){
        state(!state());
        return state();
    }
    
    public void state(boolean state) {
        
        this.state = state;
    }
    
    public Component currentDisplay() {
        
        return state ? whenTrue() : whenFalse();
    }
    
    public Component whenTrue() {
        
        return whenTrue;
    }
    
    public Component whenFalse() {
        
        return whenFalse;
    }
    
}
