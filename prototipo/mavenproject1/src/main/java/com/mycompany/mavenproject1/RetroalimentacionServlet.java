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
            Retroalimentacion r = new Retroalimentacion();
            r.setValoracionId(Integer.parseInt(valoracionIdStr));
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

    private void imprimir(HttpServletResponse res, String texto) throws IOException {
        PrintWriter out = res.getWriter();
        out.print(texto);
        out.flush();
    }
}