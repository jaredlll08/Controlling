package com.blamejared.controlling.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.list.KeyBindingList;

import java.util.List;

public class GuiCustomList  extends KeyBindingList {
    
    public List<Entry> allEntries;
    
    public GuiCustomList(ControlsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
    }
    
    public List<Entry> getAllEntries() {
        
        return allEntries;
    }
    
    public void add(Entry ent) {
        
        getEventListeners().add(ent);
        allEntries.add(ent);
    }
}
