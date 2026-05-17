package com.drawingapp.observer;

public interface Observer {
    void update(String eventType, Object data);
}