
package com.mycompany.mavenproject1;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

public class GenerarPassword {

    public static void main(String[] args) {

        JPasswordField campoPassword = new JPasswordField();

        int opcion = JOptionPane.showConfirmDialog(
            null,
            campoPassword,
            "Introduce la nueva contraseña del administrador",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            System.out.println("Operacion cancelada.");
            return;
        }

        char[] caracteres = campoPassword.getPassword();
        String password = new String(caracteres);

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(
                null,
                "La contraseña debe tener al menos 8 caracteres."
            );
            return;
        }

        String hash = BCrypt.hashpw(
            password,
            BCrypt.gensalt(12)
        );

        System.out.println("HASH PARA MYSQL:");
        System.out.println(hash);

        System.out.println(
            "Verificacion: " + BCrypt.checkpw(password, hash)
        );

        java.util.Arrays.fill(caracteres, '\0');
        password = null;
    }
}
