package com.blamejared.controlling.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.client.gui.screens.controls.ControlsScreen;

import java.util.ArrayList;
import java.util.List;

public class GuiCustomList extends ControlList {
    
    public List<Entry> allEntries;
    
    public GuiCustomList(ControlsScreen controls, Minecraft mcIn) {
        
        super(controls, mcIn);
    }
    
    public List<Entry> getAllEntries() {
        
        return allEntries;
    }
    
    @Override
    protected int addEntry(Entry ent) {
        
        if(allEntries == null) {
            allEntries = new ArrayList<>();
        }
        allEntries.add(ent);
        this.children().add(ent);
        return this.children().size() - 1;
    }
    
}
