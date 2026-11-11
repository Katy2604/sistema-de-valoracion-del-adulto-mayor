package com.mycompany.mavenproject1;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Endpoint legado deshabilitado. El antiguo código asignaba usuario_id=1
 * cuando no había sesión, lo que permitía atribuir registros al usuario equivocado.
 * El flujo vigente utiliza /api/valoraciones y valida la sesión.
 */
@WebServlet(name = "GuardarValoracionServlet", urlPatterns = {"/GuardarValoracionServlet"})
public class GuardarValoracionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_GONE);
        response.getWriter().print(new JSONObject()
                .put("success", false)
                .put("message", "Este endpoint fue deshabilitado. Utiliza el flujo actual de valoraciones.")
                .toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
