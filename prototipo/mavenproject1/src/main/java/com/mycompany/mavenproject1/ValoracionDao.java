package com.mycompany.mavenproject1;

import java.sql.*;
import org.json.JSONObject;

public class ValoracionDao {

    // ── Crea el registro raíz en valoraciones ────────────
    public int insertar(Valoracion v) {
        String sql = "INSERT INTO valoraciones (usuario_id, completada) VALUES (?, ?)";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, v.getUsuarioId());
            ps.setBoolean(2, v.isCompletada());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ── Marca una valoración como completada ─────────────
    public void marcarCompletada(int valoracionId) {
        String sql = "UPDATE valoraciones SET completada = 1 WHERE id = ?";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valoracionId);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // ── Inserta o actualiza paciente_datos ───────────────
    public void insertarPacienteDatos(PacienteDatos p, int valoracionId) {
        // Usa INSERT ... ON DUPLICATE KEY UPDATE para soportar guardado parcial
        String sql =
            "INSERT INTO paciente_datos " +
            "(valoracion_id, nombre, edad, genero, lugar_nacimiento, domicilio, " +
            " fecha_ingreso, religion, escolaridad, estado_civil, ocupacion, " +
            " dependencia_institucion, servicios_salud, cuando_acude_medico, " +
            " capaz_decisiones, responsable, lleva_tratamiento) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE " +
            " nombre=VALUES(nombre), edad=VALUES(edad), genero=VALUES(genero), " +
            " lugar_nacimiento=VALUES(lugar_nacimiento), domicilio=VALUES(domicilio), " +
            " fecha_ingreso=VALUES(fecha_ingreso), religion=VALUES(religion), " +
            " escolaridad=VALUES(escolaridad), estado_civil=VALUES(estado_civil), " +
            " ocupacion=VALUES(ocupacion), dependencia_institucion=VALUES(dependencia_institucion), " +
            " servicios_salud=VALUES(servicios_salud), cuando_acude_medico=VALUES(cuando_acude_medico), " +
            " capaz_decisiones=VALUES(capaz_decisiones), responsable=VALUES(responsable), " +
            " lleva_tratamiento=VALUES(lleva_tratamiento)";

        try (Connection con = Dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getValoracionId());
            ps.setString(2, p.getNombre());

            if (p.getEdad() != null) ps.setInt(3, p.getEdad());
            else                     ps.setNull(3, Types.TINYINT);

            ps.setString(4,  nullIfBlank(p.getGenero()));
            ps.setString(5, p.getLugarNacimiento());
            ps.setString(6, p.getDomicilio());

            if (p.getFechaIngreso() != null && !p.getFechaIngreso().isEmpty())
                ps.setDate(7, Date.valueOf(p.getFechaIngreso()));
            else
                ps.setNull(7, Types.DATE);

            ps.setString(8,  p.getReligion());
            ps.setString(9,  nullIfBlank(p.getEscolaridad()));
            ps.setString(10, nullIfBlank(p.getEstadoCivil()));
            ps.setString(11, p.getOcupacion());
            ps.setString(12, p.getDependenciaInstitucion());
            ps.setString(13, p.getServiciosSalud());
            ps.setString(14, nullIfBlank(p.getCuandoAcudeMedico()));

            if (p.getCapazDecisiones() != null) ps.setBoolean(15, p.getCapazDecisiones());
            else                                ps.setNull(15, Types.TINYINT);

            ps.setString(16, p.getResponsable());

            if (p.getLlevaTratamiento() != null) ps.setBoolean(17, p.getLlevaTratamiento());
            else                                 ps.setNull(17, Types.TINYINT);

            ps.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudieron guardar los datos del paciente.", e);
        }
    }
    
    // ── Inserta o actualiza entorno ──────────────────────
public void insertarEntorno(entorno e) {

    String sql =
        "INSERT INTO entorno " +
        "(valoracion_id, tipo_piso, tipo_pared, tipo_techo, tipo_luz, " +
        " abastecimiento_agua, purificacion_agua, drenaje, tratamiento_basura, " +
        " fauna_nociva, animales_domesticos, num_animales, animales_vacunados) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) " +
        "ON DUPLICATE KEY UPDATE " +
        " tipo_piso=VALUES(tipo_piso), " +
        " tipo_pared=VALUES(tipo_pared), " +
        " tipo_techo=VALUES(tipo_techo), " +
        " tipo_luz=VALUES(tipo_luz), " +
        " abastecimiento_agua=VALUES(abastecimiento_agua), " +
        " purificacion_agua=VALUES(purificacion_agua), " +
        " drenaje=VALUES(drenaje), " +
        " tratamiento_basura=VALUES(tratamiento_basura), " +
        " fauna_nociva=VALUES(fauna_nociva), " +
        " animales_domesticos=VALUES(animales_domesticos), " +
        " num_animales=VALUES(num_animales), " +
        " animales_vacunados=VALUES(animales_vacunados)";

    try (Connection con = Dbconnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, e.getValoracionId());

        ps.setString(2,  e.getTipoPiso());
        ps.setString(3,  e.getTipoPared());
        ps.setString(4,  e.getTipoTecho());
        ps.setString(5,  e.getTipoLuz());

        ps.setString(6,  e.getAbastecimientoAgua());
        ps.setString(7,  e.getPurificacionAgua());
        ps.setString(8,  e.getDrenaje());
        ps.setString(9,  e.getTratamientoBasura());

        ps.setString(10, e.getFaunaNociva());
        ps.setString(11, e.getAnimalesDomesticos());

        ps.setString(12, nullIfBlank(e.getNumAnimales()));
        ps.setString(13, nullIfBlank(e.getAnimalesVacunados()));

        ps.executeUpdate();

    } catch (SQLException | ClassNotFoundException ex) {
        ex.printStackTrace();
    }
}
public void insertarPatronVida(PatronVida p) {
    String sql =
        "INSERT INTO patron_vida (" +
        "valoracion_id,relacion_familiar,ingreso_economico,dependencia_economica," +
        "estado_nutricional,peso,talla,imc,cabello,mucosas,piel,labios,encias," +
        "nariz_orejas,unas,sistema_oseo,estado_general," +
        "kg_subidos,kg_perdidos,dentadura,guisa_alimentos," +
        "problema_cavidad_oral,problema_dental_comer,problema_digestion," +
        "alimentos_puede_comer,desayuno,comida,cena," +
        "cepillado_dientes,bano,cambio_ropa,lavado_manos," +
        "enfermedad_presente,tiene_tratamiento" +
        ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
        "ON DUPLICATE KEY UPDATE " +
        "relacion_familiar=VALUES(relacion_familiar),ingreso_economico=VALUES(ingreso_economico)," +
        "dependencia_economica=VALUES(dependencia_economica),estado_nutricional=VALUES(estado_nutricional)," +
        "peso=VALUES(peso), talla=VALUES(talla), imc=VALUES(imc)," +
        "cabello=VALUES(cabello),mucosas=VALUES(mucosas),piel=VALUES(piel)," +
        "labios=VALUES(labios),encias=VALUES(encias),nariz_orejas=VALUES(nariz_orejas)," +
        "unas=VALUES(unas),sistema_oseo=VALUES(sistema_oseo),estado_general=VALUES(estado_general)," +
        "kg_subidos=VALUES(kg_subidos),kg_perdidos=VALUES(kg_perdidos)," +
        "dentadura=VALUES(dentadura),guisa_alimentos=VALUES(guisa_alimentos)," +
        "problema_cavidad_oral=VALUES(problema_cavidad_oral),problema_dental_comer=VALUES(problema_dental_comer)," +
        "problema_digestion=VALUES(problema_digestion),alimentos_puede_comer=VALUES(alimentos_puede_comer)," +
        "desayuno=VALUES(desayuno),comida=VALUES(comida),cena=VALUES(cena)," +
        "cepillado_dientes=VALUES(cepillado_dientes),bano=VALUES(bano),cambio_ropa=VALUES(cambio_ropa)," +
        "lavado_manos=VALUES(lavado_manos),enfermedad_presente=VALUES(enfermedad_presente)," +
        "tiene_tratamiento=VALUES(tiene_tratamiento)";

    try(Connection con = Dbconnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, p.getValoracionId());
        ps.setString(2, nullIfBlank(p.getRelacionFamiliar()));
        ps.setString(3, nullIfBlank(p.getIngresoEconomico()));
        ps.setString(4, nullIfBlank(p.getDependenciaEconomica()));
        ps.setString(5, nullIfBlank(p.getEstadoNutricional()));
        ps.setString(6, nullIfBlank(p.getPeso()));
        ps.setString(7, nullIfBlank(p.getTalla()));
        ps.setString(8, nullIfBlank(p.getImc()));
        ps.setString(9, nullIfBlank(p.getCabello()));
        ps.setString(10, nullIfBlank(p.getMucosas()));
        ps.setString(11, nullIfBlank(p.getPiel()));
        ps.setString(12, nullIfBlank(p.getLabios()));
        ps.setString(13, nullIfBlank(p.getEncias()));
        ps.setString(14, nullIfBlank(p.getNarizOrejas()));
        ps.setString(15, nullIfBlank(p.getUnas()));
        ps.setString(16, nullIfBlank(p.getSistemaOseo()));
        ps.setString(17, nullIfBlank(p.getEstadoGeneral()));
        ps.setString(18, nullIfBlank(p.getKgSubidos()));
        ps.setString(19, nullIfBlank(p.getKgPerdidos()));
        ps.setString(20, nullIfBlank(p.getDentadura()));
        ps.setString(21, nullIfBlank(p.getGuisaAlimentos()));
        ps.setString(22, nullIfBlank(p.getProblemaCavidadOral()));
        ps.setString(23, nullIfBlank(p.getProblemaDentalComer()));
        ps.setString(24, nullIfBlank(p.getProblemaDigestion()));
        ps.setString(25, nullIfBlank(p.getAlimentosPuedeComer()));
        ps.setString(26, p.getDesayuno());
        ps.setString(27, p.getComida());
        ps.setString(28, p.getCena());
        ps.setString(29, nullIfBlank(p.getCepilladoDientes()));
        ps.setString(30, nullIfBlank(p.getBano()));
        ps.setString(31, nullIfBlank(p.getCambioRopa()));
        ps.setString(32, p.getLavadoManos());
        ps.setString(33, nullIfBlank(p.getEnfermedadPresente()));
        ps.setString(34, nullIfBlank(p.getTieneTratamiento()));

        ps.executeUpdate();

    } catch(Exception e) {
        e.printStackTrace();
    }
}


    // ── Mapeo interno ─────────────────────────────────────
    private Valoracion mapear(ResultSet rs) throws SQLException {
        Valoracion v = new Valoracion();
        v.setId(rs.getInt("id"));
        v.setUsuarioId(rs.getInt("usuario_id"));
        v.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        v.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        v.setCompletada(rs.getBoolean("completada"));
        return v;
    }
    
    public JSONObject obtenerDatosGuardados(int valoracionId) {
    JSONObject out = new JSONObject();
    try (Connection con = Dbconnection.getConnection()) {

        // paciente_datos
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM paciente_datos WHERE valoracion_id=?")) {
            ps.setInt(1, valoracionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                out.put("nombrePaciente",   rs.getString("nombre"));
                out.put("edadPaciente",     rs.getObject("edad"));
                out.put("generoPaciente",   rs.getString("genero"));
                out.put("lugarNacimiento",  rs.getString("lugar_nacimiento"));
                out.put("domicilio",        rs.getString("domicilio"));
                out.put("fechaIngreso",     rs.getString("fecha_ingreso"));
                out.put("religion",         rs.getString("religion"));
                out.put("escolaridad",      rs.getString("escolaridad"));
                out.put("estadoCivil",      rs.getString("estado_civil"));
                out.put("ocupacion",        rs.getString("ocupacion"));
                out.put("dependencia",      rs.getString("dependencia_institucion"));
                out.put("serviciosSalud",   new org.json.JSONArray(rs.getString("servicios_salud") != null ? rs.getString("servicios_salud") : "[]"));
                out.put("cuandoAcude",      rs.getString("cuando_acude_medico"));
                out.put("capazDecisiones",  rs.getObject("capaz_decisiones"));
                out.put("responsable",      rs.getString("responsable"));
                out.put("llevaTratamiento", rs.getObject("lleva_tratamiento"));
            }
        }

        // entorno
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM entorno WHERE valoracion_id=?")) {
            ps.setInt(1, valoracionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] jsonCols = {"tipo_piso","tipo_pared","tipo_techo","tipo_luz",
                    "abastecimiento_agua","purificacion_agua","drenaje","tratamiento_basura",
                    "fauna_nociva","animales_domesticos"};
                String[] jsKeys   = {"tipoPiso","tipoPared","tipoTecho","tipoLuz",
                    "abastecimientoAgua","purificacionAgua","drenaje","tratamientoBasura",
                    "faunaNociva","animalesDomesticos"};
                for (int i = 0; i < jsonCols.length; i++) {
                    String raw = rs.getString(jsonCols[i]);
                    out.put(jsKeys[i], new org.json.JSONArray(raw != null ? raw : "[]"));
                }
                out.put("numAnimales",       rs.getString("num_animales"));
                out.put("animalesVacunados", rs.getString("animales_vacunados"));
            }
        }

        // patron_vida
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM patron_vida WHERE valoracion_id=?")) {
            ps.setInt(1, valoracionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                out.put("relacionFamiliar",    rs.getString("relacion_familiar"));
                out.put("ingresoEconomico",    rs.getString("ingreso_economico"));
                out.put("dependenciaEconomica",rs.getString("dependencia_economica"));
                out.put("estadoNutricional",   rs.getString("estado_nutricional"));
                out.put("peso", rs.getString("peso"));
                out.put("talla", rs.getString("talla"));
                out.put("imc",     rs.getString("imc"));
                out.put("cabello", rs.getString("cabello"));
                out.put("mucosas",  rs.getString("mucosas"));
                out.put("piel",     rs.getString("piel"));
                out.put("labios",   rs.getString("labios"));
                out.put("encias",   rs.getString("encias"));
                out.put("narizOrejas",  rs.getString("nariz_orejas"));
                out.put("unas",         rs.getString("unas"));
                out.put("sistemaOseo",  rs.getString("sistema_oseo"));
                out.put("estadoGeneral",rs.getString("estado_general"));
                out.put("kgSubidos",    rs.getString("kg_subidos"));
                out.put("kgPerdidos",   rs.getString("kg_perdidos"));
                out.put("dentadura",    rs.getString("dentadura"));
                out.put("guisaAlimentos",       rs.getString("guisa_alimentos"));
                out.put("problemaCavidad",      rs.getString("problema_cavidad_oral"));
                out.put("problemaDental",       rs.getString("problema_dental_comer"));
                out.put("problemaDigestion",    rs.getString("problema_digestion"));
                out.put("alimentosPuedeComer",  rs.getString("alimentos_puede_comer"));
                String[] jsonCols2 = {"desayuno","comida","cena","lavado_manos"};
                String[] jsKeys2   = {"desayuno","comida","cena","lavadoManos"};
                for (int i = 0; i < jsonCols2.length; i++) {
                    String raw = rs.getString(jsonCols2[i]);
                    out.put(jsKeys2[i], new org.json.JSONArray(raw != null ? raw : "[]"));
                }
                out.put("cepillado",        rs.getString("cepillado_dientes"));
                out.put("bano",             rs.getString("bano"));
                out.put("cambioRopa",       rs.getString("cambio_ropa"));
                out.put("enfermedadPresente",rs.getString("enfermedad_presente"));
                out.put("tieneTratamiento", rs.getString("tiene_tratamiento"));
            }
        }
        
                // Las secciones nuevas se recuperan desde el JSON íntegro guardado.
        // Esto incluye las 30 respuestas MMSE, las 15 GDS y las 6 de Katz.
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT seccion, payload FROM valoracion_secciones WHERE valoracion_id=? ORDER BY seccion")) {
            ps.setInt(1, valoracionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JSONObject payload = new JSONObject(rs.getString("payload"));
                    for (String key : payload.keySet()) {
                        out.put(key, payload.get(key));
                    }
                    out.put("seccionGuardada" + rs.getInt("seccion"), true);
                }
            }
        }

        // Indica el último bloque con datos persistidos; no presupone respuestas.
        int seccionActual = 0;
        for (int i = 1; i <= 6; i++) {
            if (out.optBoolean("seccionGuardada" + i, false)) seccionActual = Math.min(i + 1, 6);
        }
        if (seccionActual == 0 && out.has("tipoPiso")) seccionActual = 2;
        out.put("seccionActual", seccionActual);
    } catch (Exception e) { e.printStackTrace(); return null; }
    return out;
}
    
    private String nullIfBlank(String s) {
    return (s == null || s.trim().isEmpty()) ? null : s.trim();
}
}


