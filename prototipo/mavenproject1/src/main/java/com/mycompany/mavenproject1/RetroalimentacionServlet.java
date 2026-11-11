package com.mycompany.mavenproject1;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/retroalimentacion")
public class RetroalimentacionServlet extends HttpServlet {

    private final RetroalimentacionDao dao = new RetroalimentacionDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        JSONObject json = new JSONObject();

        // Verificar sesión y rol maestro
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            res.setStatus(401);
            json.put("ok", false);
            json.put("mensaje", "Sesión no válida.");
            imprimir(res, json.toString());
            return;
        }

        Usuario maestro = (Usuario) session.getAttribute("usuario");
        if (!"maestro".equals(maestro.getRol())) {
            res.setStatus(403);
            json.put("ok", false);
            json.put("mensaje", "Sin permisos.");
            imprimir(res, json.toString());
            return;
        }

        String valoracionIdStr = req.getParameter("valoracionId");
        String comentario      = req.getParameter("comentario");

        if (valoracionIdStr == null || comentario == null || comentario.trim().isEmpty()) {
            res.setStatus(400);
            json.put("ok", false);
            json.put("mensaje", "Datos incompletos.");
            imprimir(res, json.toString());
            return;
        }

        try {
            int valoracionId = Integer.parseInt(valoracionIdStr);
            if (valoracionId <= 0 || !esValoracionCompletada(valoracionId)) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                json.put("ok", false);
                json.put("mensaje", "La valoración no existe o todavía no está completada.");
                imprimir(res, json.toString());
                return;
            }

            Retroalimentacion r = new Retroalimentacion();
            r.setValoracionId(valoracionId);
            r.setMaestroId(maestro.getId());
            r.setComentario(comentario.trim());
            dao.insertar(r);
            json.put("ok", true);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            json.put("ok", false);
            json.put("mensaje", "Error al guardar.");
        }

        imprimir(res, json.toString());
    }

    private boolean esValoracionCompletada(int id) throws Exception {
        String sql = "SELECT 1 FROM valoraciones v "
                + "JOIN usuarios u ON u.id=v.usuario_id "
                + "WHERE v.id=? AND v.completada=1 AND u.rol='estudiante'";
        try (java.sql.Connection con = Dbconnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void imprimir(HttpServletResponse res, String texto) throws IOException {
        PrintWriter out = res.getWriter();
        out.print(texto);
        out.flush();
    }
}