package com.mycompany.mavenproject1;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/api/valoraciones-maestro")
public class ValoracionesMaestroServlet extends HttpServlet {

    private final RetroalimentacionDao retroDao = new RetroalimentacionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json;charset=UTF-8");

        // Verificar sesión y rol maestro
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            res.setStatus(401);
            imprimir(res, "[]");
            return;
        }

        Usuario u = (Usuario) session.getAttribute("usuario");
        if (!"maestro".equals(u.getRol())) {
            res.setStatus(403);
            imprimir(res, "[]");
            return;
        }

        // Trae todas las valoraciones con datos del estudiante y del paciente
        String sql =
            "SELECT v.id, v.fecha_registro, v.completada, " +
            "       u.id AS estudiante_id, u.nombre AS estudiante_nombre, " +
            "       p.nombre AS nombre_paciente " +
            "FROM valoraciones v " +
            "JOIN usuarios u ON u.id = v.usuario_id " +
            "LEFT JOIN paciente_datos p ON p.valoracion_id = v.id " +
            "WHERE u.rol = 'estudiante' " +
            "ORDER BY v.fecha_registro DESC";

        JSONArray arr = new JSONArray();

        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int valoracionId = rs.getInt("id");
                JSONObject o = new JSONObject();
                o.put("id",               valoracionId);
                o.put("fechaRegistro",    rs.getTimestamp("fecha_registro").getTime());
                o.put("completada",       rs.getBoolean("completada"));
                o.put("estudianteId",     rs.getInt("estudiante_id"));
                o.put("estudianteNombre", rs.getString("estudiante_nombre"));
                o.put("nombrePaciente",   rs.getString("nombre_paciente") != null
                                          ? rs.getString("nombre_paciente") : "");
                o.put("tieneRetro",       retroDao.tieneRetro(valoracionId));
                arr.put(o);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        imprimir(res, arr.toString());
    }

    private void imprimir(HttpServletResponse res, String texto) throws IOException {
        PrintWriter out = res.getWriter();
        out.print(texto);
        out.flush();
    }
}