/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.mavenproject1;

import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
@WebServlet("/api/valoraciones/seccion")
public class ValoracionesSeccionServlet extends HttpServlet {

    private final ValoracionDao dao = new ValoracionDao();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        JSONObject respuesta = new JSONObject();

        try {

            StringBuilder sb = new StringBuilder();
            String linea;

            try (BufferedReader br = req.getReader()) {
                while ((linea = br.readLine()) != null) {
                    sb.append(linea);
                }
            }

            JSONObject json = new JSONObject(sb.toString());

            int valoracionId = json.getInt("valoracionId");
            int seccion      = json.getInt("seccion");

            switch (seccion) {

                case 1:
                    guardarEntorno(json);
                    guardarPatronVida(json);
                    break;

                case 2:
                    // guardar sección 3
                    break;

                case 3:
                    // guardar sección 4
                    break;

                case 4:
                    // guardar sección 5
                    break;

                case 5:
                    // guardar sección 6
                    break;

                case 6:
                    // guardar sección 7
                    break;
            }

            respuesta.put("ok", true);

        } catch (Exception e) {

            e.printStackTrace();

            res.setStatus(500);

            respuesta.put("ok", false);
            respuesta.put("mensaje", e.getMessage());
        }

        res.getWriter().print(respuesta.toString());
    }

    
    private void guardarEntorno(JSONObject json) {

    entorno e = new entorno();

    e.setValoracionId(json.getInt("valoracionId"));

    e.setTipoPiso(
        json.getJSONArray("tipoPiso").toString()
    );

    e.setTipoPared(
        json.getJSONArray("tipoPared").toString()
    );

    e.setTipoTecho(
        json.getJSONArray("tipoTecho").toString()
    );

    e.setTipoLuz(
        json.getJSONArray("tipoLuz").toString()
    );

    e.setAbastecimientoAgua(
        json.getJSONArray("abastecimientoAgua").toString()
    );

    e.setPurificacionAgua(
        json.getJSONArray("purificacionAgua").toString()
    );

    e.setDrenaje(
        json.getJSONArray("drenaje").toString()
    );

    e.setTratamientoBasura(
        json.getJSONArray("tratamientoBasura").toString()
    );

    e.setFaunaNociva(
        json.getJSONArray("faunaNociva").toString()
    );

    e.setAnimalesDomesticos(
        json.getJSONArray("animalesDomesticos").toString()
    );

    e.setNumAnimales(
        json.optString("numAnimales", null)
    );

    e.setAnimalesVacunados(
        json.optString("animalesVacunados", null)
    );

    dao.insertarEntorno(e);
}
    
    private void guardarPatronVida(JSONObject json) {

    PatronVida p = new PatronVida();

    p.setValoracionId(json.getInt("valoracionId"));

    p.setRelacionFamiliar(json.optString("relacionFamiliar"));
    p.setIngresoEconomico(json.optString("ingresoEconomico"));
    p.setDependenciaEconomica(json.optString("dependenciaEconomica"));

    p.setEstadoNutricional(json.optString("estadoNutricional"));

    p.setCabello(json.optString("cabello"));
    p.setMucosas(json.optString("mucosas"));
    p.setPiel(json.optString("piel"));
    p.setLabios(json.optString("labios"));
    p.setEncias(json.optString("encias"));

    p.setNarizOrejas(json.optString("narizOrejas"));
    p.setUnas(json.optString("unas"));
    p.setSistemaOseo(json.optString("sistemaOseo"));
    p.setEstadoGeneral(json.optString("estadoGeneral"));

    p.setKgSubidos(json.optString("kgSubidos"));
    p.setKgPerdidos(json.optString("kgPerdidos"));

    p.setDentadura(json.optString("dentadura"));
    p.setGuisaAlimentos(json.optString("guisaAlimentos"));

    p.setProblemaCavidadOral(json.optString("problemaCavidadOral"));
    p.setProblemaDentalComer(json.optString("problemaDentalComer"));
    p.setProblemaDigestion(json.optString("problemaDigestion"));

    p.setAlimentosPuedeComer(json.optString("alimentosPuedeComer"));

    p.setDesayuno(json.getJSONArray("desayuno").toString());
    p.setComida(json.getJSONArray("comida").toString());
    p.setCena(json.getJSONArray("cena").toString());

    p.setCepilladoDientes(json.optString("cepilladoDientes"));
    p.setBano(json.optString("bano"));
    p.setCambioRopa(json.optString("cambioRopa"));

    p.setLavadoManos(
        json.getJSONArray("lavadoManos").toString()
    );

    p.setEnfermedadPresente(json.optString("enfermedadPresente"));
    p.setTieneTratamiento(json.optString("tieneTratamiento"));

    dao.insertarPatronVida(p);
}
    
}
