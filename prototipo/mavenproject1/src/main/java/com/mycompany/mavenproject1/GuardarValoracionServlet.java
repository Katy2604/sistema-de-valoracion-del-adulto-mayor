/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.mycompany.mavenproject1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 *
 * @author lifek
 */
@WebServlet(name = "GuardarValoracionServlet", urlPatterns = {"/GuardarValoracionServlet"})
public class GuardarValoracionServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP GET and POST methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("application/json;charset=UTF-8");

    try (PrintWriter out = response.getWriter()) {

        // 1. Leer el JSON enviado desde JavaScript
        StringBuilder jsonBuffer = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {

            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject json = new JSONObject(jsonBuffer.toString());

        // 2. Obtener el ID del usuario logueado en sesión
        HttpSession session = request.getSession(false);

        int usuarioId = 1; // Valor de prueba

        if (session != null && session.getAttribute("usuarioId") != null) {
            usuarioId = (int) session.getAttribute("usuarioId");
        }

        // 3. Crear la valoración principal
        Valoracion valoracion = new Valoracion();

        valoracion.setUsuarioId(usuarioId);
        valoracion.setCompletada(false);

        // Crear DAO
        ValoracionDao dao = new ValoracionDao();

        // Insertar valoración y obtener su ID
        int valoracionId = dao.insertar(valoracion);

        // Verificar que se haya creado correctamente
        if (valoracionId == 0) {
            throw new Exception("No se pudo crear la valoración.");
        }

        // 4. Crear los datos del paciente
        PacienteDatos pac = new PacienteDatos();

        pac.setValoracionId(valoracionId);

        pac.setNombre(
                json.optString("nombrePaciente", null)
        );

        // Edad
        if (json.has("edadPaciente")
                && !json.optString("edadPaciente").isEmpty()) {

            pac.setEdad(
                    json.getInt("edadPaciente")
            );

        } else {

            pac.setEdad(null);
        }

        pac.setGenero(
                json.optString("generoPaciente", null)
        );

        pac.setLugarNacimiento(
                json.optString("lugarNacimiento", null)
        );

        pac.setDomicilio(
                json.optString("domicilio", null)
        );

        pac.setFechaIngreso(
                json.optString("fechaIngreso", null)
        );

        pac.setReligion(
                json.optString("religion", null)
        );

        pac.setEscolaridad(
                json.optString("escolaridad", null)
        );

        pac.setEstadoCivil(
                json.optString("estadoCivil", null)
        );

        pac.setOcupacion(
                json.optString("ocupacion", null)
        );

        pac.setDependenciaInstitucion(
                json.optString("dependencia", null)
        );

        pac.setCuandoAcudeMedico(
                json.optString("cuandoAcude", null)
        );

        pac.setResponsable(
                json.optString("responsable", null)
        );

        // 5. Guardar los datos del paciente
        dao.insertarPacienteDatos(pac, valoracionId);

        // 6. Responder a JavaScript
        out.print(
                "{\"success\": true, " +
                "\"message\": \"Valoración guardada con éxito\", " +
                "\"valoracionId\": " + valoracionId + "}"
        );

    } catch (Exception e) {

        e.printStackTrace();

        response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );

        response.getWriter().print(
                "{\"success\": false, " +
                "\"message\": \"" + e.getMessage() + "\"}"
        );
    }
}

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            // Leer el cuerpo de la petición (JSON enviado desde JavaScript)
            StringBuilder jsonBuffer = new StringBuilder();

            try (BufferedReader reader = request.getReader()) {

                String line;

                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }

                String jsonData = jsonBuffer.toString();

                System.out.println("Datos recibidos del formulario: " + jsonData);

                // ---------------------------------------------------------
                // 2. AQUÍ CONECTAS CON TUS DAO
                // ---------------------------------------------------------

                // Ejemplo:
                // int usuarioId = 1;
                // ValoracionDao valDao = new ValoracionDao();
                // int idValoracion = valDao.insertarValoracionPrincipal(usuarioId);

                // PacienteDao pacDao = new PacienteDao();
                // pacDao.insertarDatosPaciente(idValoracion, jsonData);

                // ---------------------------------------------------------
                // 3. Responder a JavaScript
                // ---------------------------------------------------------

                out.print(
                    "{\"success\": true, \"message\": \"Valoración guardada correctamente\"}"
                );

            } catch (Exception e) {

                e.printStackTrace();

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                out.print(
                    "{\"success\": false, \"message\": \"" 
                    + e.getMessage() 
                    + "\"}"
                );
            }
        }
    }

    /**
     * Handles the HTTP GET method.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método GET no soportado.");
    }

    /**
     * Handles the HTTP POST method.
     */
    
    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Servlet para guardar valoraciones";
    }
}
