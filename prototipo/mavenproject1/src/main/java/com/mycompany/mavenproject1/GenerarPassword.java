/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author lifek
 */
public class GenerarPassword {
    public static void main(String[] args){
        String password = "Katri_26";
        
        String hash= BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        System.out.println("Contraseña: "+password);
        System.out.println("Hash BCrypt: "+hash);
        
        boolean correcta = BCrypt.checkpw(password, hash);

        System.out.println("¿La contraseña coincide? " + correcta);
    }
}
