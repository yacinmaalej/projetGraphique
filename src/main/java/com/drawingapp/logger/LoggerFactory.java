package com.drawingapp.logger;

public class LoggerFactory {

    public static Logger getLogger(LoggingStrategy strategy) {
        return switch (strategy) {
            case CONSOLE -> ConsoleLogger.getInstance();
            case FILE -> FileLogger.getInstance();
            case DATABASE -> DatabaseLogger.getInstance();
        };
    }

    public static Logger getLogger(String strategyName) {
        try {
            LoggingStrategy strategy = LoggingStrategy.valueOf(strategyName.toUpperCase());
            return getLogger(strategy);
        } catch (IllegalArgumentException e) {
            System.err.println("Stratégie inconnue: " + strategyName + ", utilisation de CONSOLE");
            return ConsoleLogger.getInstance();
        }
    }
}