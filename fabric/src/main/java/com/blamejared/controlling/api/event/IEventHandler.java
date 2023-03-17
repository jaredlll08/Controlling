package com.blamejared.controlling.api.event;

public interface IEventHandler<T, U> {
    
    U handle(T event);
    
}
