package com.blamejared.controlling.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;

import java.util.ArrayList;
import java.util.List;

public class CustomList extends KeyBindsList {
    
    public List<Entry> allEntries;
    
    public CustomList(KeyBindsScreen controls, Minecraft mcIn) {
        
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
