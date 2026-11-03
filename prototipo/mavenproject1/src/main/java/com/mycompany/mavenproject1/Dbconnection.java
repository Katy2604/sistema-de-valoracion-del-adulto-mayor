
package com.mycompany.mavenproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dbconnection {

    // Datos de conexión obtenidos de las variables de entorno.
    // Si no existen, se utilizan valores para desarrollo local.
    private static final String URL = obtenerVariable(
            "DB_URL",
            "jdbc:mysql://localhost:3306/valoracion_adulto_mayor?serverTimezone=America/Mexico_City"
    );

    private static final String USER = obtenerVariable("DB_USER", "root");

    private static final String PASSWORD = obtenerVariable("DB_PASSWORD", "");

    // Método para obtener una variable de entorno.
    private static String obtenerVariable(String nombre, String valorPredeterminado) {
        String valor = System.getenv(nombre);

        if (valor == null || valor.trim().isEmpty()) {
            return valorPredeterminado;
        }

        return valor;
    }

    // Método para establecer la conexión con MySQL.
    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
