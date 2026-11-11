package com.mycompany.mavenproject1;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/valoraciones")
public class ValoracionesServlet extends HttpServlet {

    private final ValoracionDao dao = new ValoracionDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        Usuario usuario = usuarioSesion(req);
        if (usuario == null) {
            responder(res, 401, new JSONObject().put("ok", false).put("mensaje", "Sesión no válida."));
            return;
        }

        JSONObject json = new JSONObject();
        try {
            int valoracionId = 0;
            String vidParam = req.getParameter("valoracionId");
            if (vidParam != null && !vidParam.trim().isEmpty()) {
                valoracionId = Integer.parseInt(vidParam.trim());
                if (!esEditable(valoracionId, usuario.getId())) {
                    responder(res, 403, new JSONObject().put("ok", false)
                            .put("mensaje", "No tienes permiso para modificar esta valoración."));
                    return;
                }
            } else {
                Valoracion nueva = new Valoracion();
                nueva.setUsuarioId(usuario.getId());
                nueva.setCompletada(false);
                valoracionId = dao.insertar(nueva);
                if (valoracionId <= 0) throw new SQLException("No se pudo crear la valoración.");
            }

            PacienteDatos p = new PacienteDatos();
            p.setValoracionId(valoracionId);
            p.setNombre(req.getParameter("nombrePaciente"));
            String edad = req.getParameter("edadPaciente");
            if (edad != null && !edad.trim().isEmpty()) p.setEdad(Integer.parseInt(edad.trim()));
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
            if (capaz != null) p.setCapazDecisiones("Sí".equalsIgnoreCase(capaz));
            String tratamiento = req.getParameter("llevaTratamiento");
            if (tratamiento != null) p.setLlevaTratamiento("Sí".equalsIgnoreCase(tratamiento));

            JSONArray servicios = new JSONArray();
            String[] valores = req.getParameterValues("serviciosSalud");
            if (valores != null) for (String valor : valores) servicios.put(valor);
            p.setServiciosSalud(servicios.toString());

            dao.insertarPacienteDatos(p, valoracionId);
            json.put("ok", true).put("valoracionId", valoracionId);
        } catch (NumberFormatException e) {
            res.setStatus(400);
            json.put("ok", false).put("mensaje", "Edad o identificador inválido.");
        } catch (Exception e) {
            getServletContext().log("Error guardando datos generales de valoración", e);
            res.setStatus(500);
            json.put("ok", false).put("mensaje", "No se pudieron guardar los datos del paciente.");
        }
        imprimir(res, json.toString());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json;charset=UTF-8");
        Usuario usuario = usuarioSesion(req);
        if (usuario == null) {
            responder(res, 401, new JSONObject().put("ok", false).put("mensaje", "Sesión no válida."));
            return;
        }

        try {
            StringBuilder body = new StringBuilder();
            try (BufferedReader br = req.getReader()) {
                String line;
                while ((line = br.readLine()) != null) body.append(line);
            }
            int id = Integer.parseInt(parseParams(body.toString()).get("valoracionId"));
            if (id <= 0) {
                responder(res, 400, new JSONObject().put("ok", false).put("mensaje", "ID inválido."));
                return;
            }
            String sql = "UPDATE valoraciones SET completada=1 WHERE id=? AND usuario_id=? AND completada=0";
            try (Connection con = Dbconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setInt(2, usuario.getId());
                if (ps.executeUpdate() != 1) {
                    responder(res, 403, new JSONObject().put("ok", false)
                            .put("mensaje", "La valoración no existe, no te pertenece o ya fue finalizada."));
                    return;
                }
            }
            responder(res, 200, new JSONObject().put("ok", true));
        } catch (NumberFormatException e) {
            responder(res, 400, new JSONObject().put("ok", false).put("mensaje", "ID inválido."));
        } catch (Exception e) {
            getServletContext().log("Error finalizando valoración", e);
            responder(res, 500, new JSONObject().put("ok", false).put("mensaje", "No se pudo finalizar la valoración."));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json;charset=UTF-8");
        Usuario usuario = usuarioSesion(req);
        if (usuario == null) {
            responder(res, 401, new JSONObject().put("ok", false).put("mensaje", "Sesión no válida."));
            return;
        }
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            if (!esPropietario(id, usuario.getId())) {
                responder(res, 403, new JSONObject().put("ok", false).put("mensaje", "No tienes acceso a esta valoración."));
                return;
            }
            JSONObject datos = dao.obtenerDatosGuardados(id);
            if (datos == null) {
                responder(res, 500, new JSONObject().put("ok", false).put("mensaje", "No se pudieron recuperar los datos."));
            } else {
                datos.put("ok", true);
                responder(res, 200, datos);
            }
        } catch (NumberFormatException e) {
            responder(res, 400, new JSONObject().put("ok", false).put("mensaje", "ID inválido."));
        } catch (Exception e) {
            getServletContext().log("Error recuperando valoración", e);
            responder(res, 500, new JSONObject().put("ok", false).put("mensaje", "Error al recuperar la valoración."));
        }
    }

    private Usuario usuarioSesion(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object value = session == null ? null : session.getAttribute("usuario");
        return value instanceof Usuario ? (Usuario) value : null;
    }

    private boolean esPropietario(int id, int usuarioId) throws Exception {
        return existeRelacion(id, usuarioId, false);
    }

    private boolean esEditable(int id, int usuarioId) throws Exception {
        return existeRelacion(id, usuarioId, true);
    }

    private boolean existeRelacion(int id, int usuarioId, boolean soloIncompletas) throws Exception {
        String sql = "SELECT 1 FROM valoraciones WHERE id=? AND usuario_id=?"
                + (soloIncompletas ? " AND completada=0" : "");
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    private java.util.Map<String, String> parseParams(String body) throws IOException {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
        if (body == null || body.trim().isEmpty()) return map;
        for (String par : body.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                map.put(java.net.URLDecoder.decode(kv[0], "UTF-8"),
                        java.net.URLDecoder.decode(kv[1], "UTF-8"));
            }
        }
        return map;
    }

    private void responder(HttpServletResponse res, int status, JSONObject json) throws IOException {
        res.setStatus(status);
        imprimir(res, json.toString());
    }

    private void imprimir(HttpServletResponse res, String texto) throws IOException {
        PrintWriter out = res.getWriter();
        out.print(texto);
        out.flush();
    }
}
