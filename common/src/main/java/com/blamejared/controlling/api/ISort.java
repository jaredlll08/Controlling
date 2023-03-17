package com.blamejared.controlling.api;

import com.blamejared.controlling.client.NewKeyBindsList;

import java.util.List;

public interface ISort {
    
    void sort(List<NewKeyBindsList.Entry> entries);
    
}
