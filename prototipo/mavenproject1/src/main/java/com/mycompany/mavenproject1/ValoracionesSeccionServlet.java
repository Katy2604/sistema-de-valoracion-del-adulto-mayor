package com.mycompany.mavenproject1;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 * Guarda el contenido íntegro de cada sección como JSON versionado.
 * La tabla se crea mediante la migración SQL incluida en database/migrations.
 */
@WebServlet("/api/valoraciones/seccion")
public class ValoracionesSeccionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            responder(res, 401, false, "Sesión no válida.");
            return;
        }

        try {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
            }
            JSONObject json = new JSONObject(body.toString());
            int valoracionId = json.getInt("valoracionId");
            int seccion = json.getInt("seccion");

            if (valoracionId <= 0 || seccion < 1 || seccion > 6) {
                responder(res, 400, false, "Identificador o sección inválidos.");
                return;
            }
            if (!esPropietario(valoracionId, usuario.getId())) {
                responder(res, 403, false, "No tienes permiso para modificar esta valoración.");
                return;
            }

            // El payload se conserva completo, incluidos radios, listas y respuestas
            // dinámicas de MMSE, GDS-15 y Katz. El upsert permite guardar varias veces.
            JSONObject payload = new JSONObject(json.toString());
            payload.remove("valoracionId");
            payload.remove("seccion");

            String sql = "INSERT INTO valoracion_secciones "
                    + "(valoracion_id, seccion, payload) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE payload=VALUES(payload), "
                    + "fecha_actualizacion=CURRENT_TIMESTAMP";
            try (Connection con = Dbconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, valoracionId);
                ps.setInt(2, seccion);
                ps.setString(3, payload.toString());
                if (ps.executeUpdate() != 1) {
                    throw new IllegalStateException("MySQL no confirmó el guardado de la sección.");
                }
            }

            responder(res, 200, true, "Sección guardada.");
        } catch (org.json.JSONException e) {
            responder(res, 400, false, "El contenido enviado no es JSON válido.");
        } catch (Exception e) {
            getServletContext().log("Error guardando sección de valoración", e);
            responder(res, 500, false, "No se pudo guardar la sección. Intenta nuevamente.");
        }
    }

    private boolean esPropietario(int valoracionId, int usuarioId)
            throws Exception {
        String sql = "SELECT 1 FROM valoraciones WHERE id=? AND usuario_id=? AND completada=0";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valoracionId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void responder(HttpServletResponse res, int status, boolean ok, String mensaje)
            throws IOException {
        res.setStatus(status);
        res.getWriter().print(new JSONObject()
                .put("ok", ok).put("mensaje", mensaje).toString());
    }
}
