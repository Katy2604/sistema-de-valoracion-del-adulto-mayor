/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.mavenproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class Dbconnection {
 
    private static final String URL      = "jdbc:mysql://localhost:3306/valoracion_adulto_mayor"
                                         + "?useSSL=false&serverTimezone=America/Mexico_City&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";       
    private static final String PASSWORD = "si55."; 
 
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
