package com.mycompany.mavenproject1;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/valoraciones")
public class ValoracionesServlet extends HttpServlet {

    private final ValoracionDao dao = new ValoracionDao();

    // ── POST: crear valoración + datos del paciente ──────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        JSONObject json = new JSONObject();

        // 1. Verificar sesión
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            res.setStatus(401);
            json.put("ok", false);
            json.put("mensaje", "Sesión no válida.");
            imprimir(res, json.toString());
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            // Después de verificar sesión, antes de crear Valoracion:
            String vidParam = req.getParameter("valoracionId");
            int valoracionId = 0;

            if (vidParam != null && !vidParam.trim().isEmpty()) {
                // Ya existe — solo actualizar paciente_datos
                try { valoracionId = Integer.parseInt(vidParam.trim()); }
                catch (NumberFormatException ignored) {}
            }

            if (valoracionId == 0) {
                // Crear nueva valoración
                Valoracion v = new Valoracion();
                v.setUsuarioId(usuario.getId());
                v.setCompletada(false);
                valoracionId = dao.insertar(v);
                            if (valoracionId == 0) {
                            res.setStatus(500);
                            json.put("ok", false);
                            json.put("mensaje", "Error al crear la valoración.");
                            imprimir(res, json.toString());
                            return;
                        }
            }

            if (valoracionId == 0) {
                res.setStatus(500);
                json.put("ok", false);
                json.put("mensaje", "Error al crear la valoración.");
                imprimir(res, json.toString());
                return;
            }

            // 3. Armar PacienteDatos desde los parámetros del form
            PacienteDatos p = new PacienteDatos();
            p.setValoracionId(valoracionId);
            p.setNombre(req.getParameter("nombrePaciente"));

            String edadStr = req.getParameter("edadPaciente");
            if (edadStr != null && !edadStr.trim().isEmpty()) {
                try { p.setEdad(Integer.parseInt(edadStr.trim())); }
                catch (NumberFormatException ignored) {}
            }

            p.setGenero(req.getParameter("generoPaciente"));
            p.setLugarNacimiento(req.getParameter("lugarNacimiento"));
            p.setDomicilio(req.getParameter("domicilio"));
            p.setFechaIngreso(req.getParameter("fechaIngreso"));
            p.setReligion(req.getParameter("religion"));
            p.setEscolaridad(req.getParameter("escolaridad"));
            p.setEstadoCivil(req.getParameter("estadoCivil"));
            p.setOcupacion(req.getParameter("ocupacion"));
            p.setDependenciaInstitucion(req.getParameter("dependencia"));
            p.setCuandoAcudeMedico(req.getParameter("cuandoAcude"));
            p.setResponsable(req.getParameter("responsable"));

            String capaz = req.getParameter("capazDecisiones");
            if (capaz != null) p.setCapazDecisiones("Sí".equals(capaz));

            String lleva = req.getParameter("llevaTratamiento");
            if (lleva != null) p.setLlevaTratamiento("Sí".equals(lleva));

            // Servicios de salud → JSON array
            String[] servicios = req.getParameterValues("serviciosSalud");
            JSONArray arr = new JSONArray();
            if (servicios != null) {
                for (String s : servicios) arr.put(s);
            }
            p.setServiciosSalud(arr.toString());

            // 5. Responder con el ID para que el JS lo use al guardar secciones siguientes
            dao.insertarPacienteDatos(p, valoracionId);
            json.put("ok", true);
            json.put("valoracionId", valoracionId);

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            json.put("ok", false);
            json.put("mensaje", "Error interno: " + e.getMessage());
        }

        imprimir(res, json.toString());
    }

    // ── PUT: marcar valoración como completada ───────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            res.setStatus(401);
            imprimir(res, "{\"ok\":false}");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            try (java.io.BufferedReader br = req.getReader()) {
                while ((line = br.readLine()) != null) sb.append(line);
            }
            java.util.Map<String,String> params = parseParams(sb.toString());
            int valoracionId = Integer.parseInt(params.getOrDefault("valoracionId", "0"));

            if (valoracionId > 0) {
                dao.marcarCompletada(valoracionId);
                json.put("ok", true);
            } else {
                res.setStatus(400);
                json.put("ok", false);
                json.put("mensaje", "ID inválido.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            json.put("ok", false);
        }
        imprimir(res, json.toString());
    }

    // ── Helpers ──────────────────────────────────────────
    private void imprimir(HttpServletResponse res, String texto) throws IOException {
        PrintWriter out = res.getWriter();
        out.print(texto);
        out.flush();
    }

    private java.util.Map<String, String> parseParams(String body) {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
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
    
    @Override
protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    res.setContentType("application/json;charset=UTF-8");

    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("usuario") == null) {
        res.setStatus(401); imprimir(res, "{\"ok\":false}"); return;
    }

    JSONObject json = new JSONObject();
    try {
        int id = Integer.parseInt(req.getParameter("id"));
        JSONObject datos = dao.obtenerDatosGuardados(id);
        if (datos == null) { json.put("ok", false); }
        else { datos.put("ok", true); json = datos; }
    } catch (Exception e) {
        e.printStackTrace(); res.setStatus(500); json.put("ok", false);
    }
    imprimir(res, json.toString());
}
}

