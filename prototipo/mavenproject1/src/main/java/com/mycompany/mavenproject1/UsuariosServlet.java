package com.mycompany.mavenproject1;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/usuarios")
public class UsuariosServlet extends HttpServlet {

    private final Usuariodao dao = new Usuariodao();

    // ── GET: listar todos ────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException{
        try {
            res.setContentType("application/json;charset=UTF-8");

        if (!esAdmin(req)) { res.setStatus(403); return; }

        List<Usuario> lista = dao.listarTodos();
        JSONArray arr = new JSONArray();
        for (Usuario u : lista) {
            JSONObject o = new JSONObject();
            o.put("id",     u.getId());
            o.put("nombre", u.getNombre());
            o.put("correo", u.getCorreo());
            o.put("rol",    u.getRol());
            o.put("activo", u.isActivo());
            arr.put(o);
        }
        imprimir(res, arr.toString()); 
        }
        catch (SQLException | ClassNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
       
    }

    // ── POST: crear usuario ──────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        try {
            res.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        if (!esAdmin(req)) { res.setStatus(403); return; }

        String nombre     = req.getParameter("nombre");
        String correo     = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        String rol        = req.getParameter("rol");

        JSONObject json = new JSONObject();

        if (nombre == null || correo == null || contrasena == null || contrasena.length() < 8) {
            res.setStatus(400);
            json.put("ok", false);
            json.put("mensaje", "Datos inválidos.");
            imprimir(res, json.toString());
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setCorreo(correo.trim());
        u.setRol(rol != null ? rol : "estudiante");
        
        dao.insertar(u, contrasena);
        json.put("ok", true);
        imprimir(res, json.toString());
        }catch (SQLException | ClassNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } 
        
    }

    // ── PUT: editar usuario ──────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        if (!esAdmin(req)) { res.setStatus(403); return; }

        // Tomcat no parsea body de PUT automáticamente, hay que leerlo
        StringBuilder sb = new StringBuilder();
        String line;
        try (java.io.BufferedReader br = req.getReader()) {
            while ((line = br.readLine()) != null) sb.append(line);
        }
        java.util.Map<String,String> params = parseParams(sb.toString());

        JSONObject json = new JSONObject();
        try {
            int id = Integer.parseInt(params.getOrDefault("id", "0"));
            if (id == 0) throw new NumberFormatException();

            Usuario u = new Usuario();
            u.setId(id);
            u.setNombre(params.getOrDefault("nombre", "").trim());
            u.setCorreo(params.getOrDefault("correo", "").trim());
            u.setRol(params.getOrDefault("rol", "estudiante"));

            String contrasena = params.getOrDefault("contrasena", "");
            dao.actualizar(u, contrasena);
            json.put("ok", true);
        } catch (Exception e) {
            res.setStatus(400);
            json.put("ok", false);
            json.put("mensaje", "Datos inválidos.");
        }
        imprimir(res, json.toString());
    }

    // ── DELETE: activar/desactivar usuario ───────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        if (!esAdmin(req)) { res.setStatus(403); return; }

        StringBuilder sb = new StringBuilder();
        String line;
        try (java.io.BufferedReader br = req.getReader()) {
            while ((line = br.readLine()) != null) sb.append(line);
        }
        java.util.Map<String,String> params = parseParams(sb.toString());

        JSONObject json = new JSONObject();
        try {
            int id      = Integer.parseInt(params.getOrDefault("id", "0"));
            boolean act = "1".equals(params.getOrDefault("activar", "0"));

            if (act) dao.activar(id);
            else     dao.desactivar(id);

            json.put("ok", true);
        } catch (Exception e) {
            res.setStatus(400);
            json.put("ok", false);
            json.put("mensaje", "Error al cambiar estado.");
        }
        imprimir(res, json.toString());
    }

    // ── Helpers ──────────────────────────────────────────
    private boolean esAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return false;
        Usuario u = (Usuario) s.getAttribute("usuario");
        return u != null && "admin".equals(u.getRol());
    }

    private void imprimir(HttpServletResponse res, String texto) throws IOException {
        PrintWriter out = res.getWriter();
        out.print(texto);
        out.flush();
    }

    private java.util.Map<String,String> parseParams(String body) {
        java.util.Map<String,String> map = new java.util.LinkedHashMap<>();
        if (body == null || body.trim().isEmpty()) return map;
        for (String par : body.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                try {
                    map.put(
                        java.net.URLDecoder.decode(kv[0], "UTF-8"),
                        java.net.URLDecoder.decode(kv[1], "UTF-8")
                    );
                } catch (Exception ignored) {}
            }
        }
        return map;
    }
}