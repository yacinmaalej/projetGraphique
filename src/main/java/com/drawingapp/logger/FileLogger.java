package com.drawingapp.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger implements Logger {
    private static volatile FileLogger instance;
    private LoggingStrategy loggingStrategy;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String logFilePath;

    private FileLogger() {
        this.loggingStrategy = LoggingStrategy.FILE;

        // Créer le dossier logs à la racine du projet
        String logDir = "logs";
        File dir = new File(logDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("📁 Dossier logs créé: " + dir.getAbsolutePath() + " - " + (created ? "OK" : "ÉCHEC"));
        }

        // Fichier de log avec date
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.logFilePath = logDir + File.separator + "drawingapp_" + date + ".log";

        System.out.println("📄 Fichier log: " + new File(logFilePath).getAbsolutePath());

        // Écrire l'en-tête
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println("========================================");
            writer.println("  LOG DRAWINGAPP - " + LocalDateTime.now().format(formatter));
            writer.println("========================================");
            writer.flush();
        } catch (IOException e) {
            System.err.println("❌ Erreur création fichier log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static FileLogger getInstance() {
        if (instance == null) {
            synchronized (FileLogger.class) {
                if (instance == null) {
                    instance = new FileLogger();
                }
            }
        }
        return instance;
    }

    @Override
    public void log(String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] %s", timestamp, action);

        // Toujours afficher dans la console pour debug
        System.out.println("[FILE-LOG] " + logMessage);

        switch (loggingStrategy) {
            case FILE:
                writeToFile(logMessage);
                break;
            case CONSOLE:
                System.out.println("[FILE-LOG] " + logMessage);
                break;
            case DATABASE:
                writeToFile(logMessage);
                break;
        }
    }

    private void writeToFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            System.err.println("❌ Erreur écriture fichier log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setLoggingStrategy(LoggingStrategy strategy) {
        this.loggingStrategy = strategy;
        log("Stratégie de logging changée vers: " + strategy);
    }

    public String getLogFilePath() {
        return new File(logFilePath).getAbsolutePath();
    }
}