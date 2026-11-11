package com.mycompany.mavenproject1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RetroalimentacionDao {

    /** Inserta una retroalimentación nueva */
    public void insertar(Retroalimentacion r) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO retroalimentacion (valoracion_id, maestro_id, comentario, leida) "
                   + "VALUES (?, ?, ?, 0)";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getValoracionId());
            ps.setInt(2, r.getMaestroId());
            ps.setString(3, r.getComentario());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("MySQL no confirmó el guardado de la retroalimentación.");
            }
        }
    }

    /** Lista retroalimentaciones de una valoración */
    public List<Retroalimentacion> listarPorValoracion(int valoracionId) {
        List<Retroalimentacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM retroalimentacion WHERE valoracion_id = ? ORDER BY fecha DESC";

        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, valoracionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Verifica si una valoración ya tiene al menos una retroalimentación */
    public boolean tieneRetro(int valoracionId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT 1 FROM retroalimentacion WHERE valoracion_id = ? LIMIT 1";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valoracionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Retroalimentacion mapear(ResultSet rs) throws SQLException {
        Retroalimentacion r = new Retroalimentacion();
        r.setId(rs.getInt("id"));
        r.setValoracionId(rs.getInt("valoracion_id"));
        r.setMaestroId(rs.getInt("maestro_id"));
        r.setComentario(rs.getString("comentario"));
        r.setFecha(rs.getTimestamp("fecha"));
        r.setLeida(rs.getBoolean("leida"));
        return r;
    }
}
