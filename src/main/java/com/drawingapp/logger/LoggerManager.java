package com.drawingapp.logger;

public class LoggerManager {
    private static LoggerManager instance;
    private Logger currentLogger;
    private LoggingStrategy currentStrategy;

    private LoggerManager() {
        this.currentStrategy = LoggingStrategy.CONSOLE;
        this.currentLogger = LoggerFactory.getLogger(currentStrategy);
    }

    public static LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    public void setStrategy(LoggingStrategy strategy) {
        this.currentStrategy = strategy;
        this.currentLogger = LoggerFactory.getLogger(strategy);
        this.currentLogger.log("Stratégie de logging changée vers: " + strategy);
    }

    public Logger getLogger() {
        return currentLogger;
    }

    public LoggingStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    // Méthode statique pratique pour logger rapidement
    public static void log(String action) {
        getInstance().getLogger().log(action);
    }
}