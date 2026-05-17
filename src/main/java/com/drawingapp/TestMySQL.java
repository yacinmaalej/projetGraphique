package com.drawingapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestMySQL {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/", "root", ""
            );
            System.out.println("✅ Connexion MySQL réussie !");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            System.out.println("Vérifiez que XAMPP MySQL est démarré !");
        }
    }
}