package com.mycompany.mavenproject1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;


@WebServlet("/api/login")
public class Loginservlet extends HttpServlet {

    private final Usuariodao dao = new Usuariodao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        String correo = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        String aceptaAviso = req.getParameter("aceptaConfidencialidad");

        JSONObject json = new JSONObject();

        if (correo == null || correo.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()
                || !"si".equals(aceptaAviso)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("ok", false);
            json.put("mensaje", "Completa tus credenciales y acepta el aviso de confidencialidad.");
            res.getWriter().print(json.toString());
            return;
        }

        try {
            Usuario u = dao.autenticar(correo.trim(), contrasena);

            if (u != null) {
                HttpSession session = req.getSession(true);
                req.changeSessionId();
                session.setAttribute("usuario", u);
                session.setMaxInactiveInterval(60 * 30); // 30 min

                json.put("ok",     true);
                json.put("rol",    u.getRol());
                json.put("nombre", u.getNombre());

                String ruta;
                switch (u.getRol()) {
                    case "admin":      ruta = "pages/admin/usuarios.html"; break;
                    case "maestro":    ruta = "pages/dashboard-maestro.html";    break;
                    case "estudiante": ruta = "pages/dashboard-estudiante.html"; break;
                    default:           ruta = "index.html";
                }
                json.put("redirect", ruta);

            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                json.put("ok",      false);
                json.put("mensaje", "Correo o contraseña incorrectos.");
            }

        } catch (SQLException | ClassNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("ok",      false);
            json.put("mensaje", "Error de servidor. Intenta más tarde.");
            e.printStackTrace();
        }

        PrintWriter out = res.getWriter();
        out.print(json);
        out.flush();
    }
}