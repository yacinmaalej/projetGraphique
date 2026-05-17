package com.drawingapp.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogger implements Logger {
    private static volatile ConsoleLogger instance;
    private LoggingStrategy loggingStrategy;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ConsoleLogger() {
        this.loggingStrategy = LoggingStrategy.CONSOLE;
    }

    public static ConsoleLogger getInstance() {
        if (instance == null) {
            synchronized (ConsoleLogger.class) {
                if (instance == null) {
                    instance = new ConsoleLogger();
                }
            }
        }
        return instance;
    }

    @Override
    public void log(String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] %s", timestamp, action);

        switch (loggingStrategy) {
            case CONSOLE:
                System.out.println(logMessage);
                break;
            case FILE:
                // À implémenter plus tard
                System.out.println("[FILE] " + logMessage);
                break;
            case DATABASE:
                // À implémenter plus tard
                System.out.println("[DB] " + logMessage);
                break;
        }
    }

    @Override
    public void setLoggingStrategy(LoggingStrategy strategy) {
        this.loggingStrategy = strategy;
        log("Stratégie de logging changée vers: " + strategy);
    }
}