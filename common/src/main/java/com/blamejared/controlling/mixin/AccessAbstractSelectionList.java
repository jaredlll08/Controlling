package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AbstractSelectionList.class)
public interface AccessAbstractSelectionList {
    
    @Accessor("children")
    <E> List<E> controlling$getChildren();
    
}
