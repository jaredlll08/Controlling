package com.blamejared.controlling.api;

import com.blamejared.controlling.api.entries.IKeyEntry;

import java.util.List;

public interface ISort {
    
    void sort(List<IKeyEntry> entries);
    
}
