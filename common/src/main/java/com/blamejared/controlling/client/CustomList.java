package com.blamejared.controlling.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;

import java.util.ArrayList;
import java.util.Comparator;
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
    public void clearEntries() {
        
        super.clearEntries();
    }
    
    @Override
    public void sort(Comparator<Entry> comp) {
        
        super.sort(comp);
    }
    
    @Override
    protected int addEntry(Entry ent) {
        
        if(allEntries == null) {
            allEntries = new ArrayList<>();
        }
        allEntries.add(ent);
        return addEntryInternal(ent);
    }
    
    public int addEntryInternal(Entry ent) {
        
        return super.addEntry(ent);
    }
    
}
