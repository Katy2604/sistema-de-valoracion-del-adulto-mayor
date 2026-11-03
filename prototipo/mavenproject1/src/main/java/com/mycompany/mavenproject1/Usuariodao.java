/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.mavenproject1;

import org.mindrot.jbcrypt.BCrypt;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class Usuariodao {
 
    /**
     * Autentica un usuario por correo + contraseña.
     * Devuelve el Usuario si las credenciales son correctas, null si no.
     */
    public Usuario autenticar(String correo, String contrasena) throws SQLException, ClassNotFoundException {
        String sql = "SELECT id, nombre, correo, contrasena_hash, rol, activo "
                   + "FROM usuarios WHERE correo = ? AND activo = 1";
 
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                String hash = rs.getString("contrasena_hash");
                if (BCrypt.checkpw(contrasena, hash)) {
                    return mapear(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("===== ERROR AL AUTENTICAR =====");
            e.printStackTrace();
            throw e;
        }
        return null;
    }
    /** Lista todos los usuarios (solo admin) */
    public List<Usuario> listarTodos() throws SQLException, ClassNotFoundException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, correo, rol, activo FROM usuarios ORDER BY nombre";
 
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    /** Inserta un usuario nuevo con contraseña hasheada */
    public void insertar(Usuario u, String contrasenaPlana) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO usuarios (nombre, correo, contrasena_hash, rol, activo) "
                   + "VALUES (?, ?, ?, ?, 1)";
        String hash = BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt(12));
 
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, hash);
            ps.setString(4, u.getRol());
            ps.executeUpdate();
        }
    }
 
    /** Actualiza nombre, correo y rol. Si contrasenaPlana no es vacía, también la actualiza */
    public void actualizar(Usuario u, String contrasenaPlana) throws SQLException, ClassNotFoundException {
        boolean cambiarPass = contrasenaPlana != null && !contrasenaPlana.trim().isEmpty();
 
        String sql = cambiarPass
            ? "UPDATE usuarios SET nombre=?, correo=?, rol=?, contrasena_hash=? WHERE id=?"
            : "UPDATE usuarios SET nombre=?, correo=?, rol=? WHERE id=?";
 
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getRol());
            if (cambiarPass) {
                ps.setString(4, BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt(12)));
                ps.setInt(5, u.getId());
            } else {
                ps.setInt(4, u.getId());
            }
            ps.executeUpdate();
        }
    }
 
    /** Desactiva un usuario (soft delete) */
    public void desactivar(int id) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id = ?";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
 
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
    public void activar(int id) {
    String sql = "UPDATE usuarios SET activo = 1 WHERE id = ?";
    try (Connection con = Dbconnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}
}
 