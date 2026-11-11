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

        String correo     = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");

        JSONObject json = new JSONObject();

        try {
            Usuario u = dao.autenticar(correo, contrasena);

            if (u != null) {
                HttpSession session = req.getSession(true);
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