package com.drawingapp.logger;

public interface Logger {
    void log(String action);
    void setLoggingStrategy(LoggingStrategy strategy);
}
