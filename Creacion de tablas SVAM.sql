-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema svam
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema valoracion_adulto_mayor
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema valoracion_adulto_mayor
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `valoracion_adulto_mayor` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `valoracion_adulto_mayor` ;

-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(150) NOT NULL,
  `correo` VARCHAR(150) NOT NULL,
  `contrasena_hash` VARCHAR(255) NOT NULL,
  `rol` ENUM('estudiante', 'maestro', 'admin') NOT NULL DEFAULT 'estudiante',
  `activo` TINYINT(1) NOT NULL DEFAULT '1',
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `correo_UNIQUE` (`correo` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`valoraciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`valoraciones` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `fecha_registro` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `completada` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_valoraciones_usuario_idx` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `valoraciones_ibfk_usuario`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `valoracion_adulto_mayor`.`usuarios` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_actividad_reposo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_actividad_reposo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `horas_sueno_nocturno` TINYINT UNSIGNED NULL DEFAULT NULL,
  `realiza_siestas` TINYINT(1) NULL DEFAULT NULL,
  `horas_siesta` DECIMAL(3,1) NULL DEFAULT NULL,
  `dificultad_sueno` TINYINT(1) NULL DEFAULT NULL,
  `estado_al_despertar` ENUM('Cansado', 'Descansado', 'Otro') NULL DEFAULT NULL,
  `despierta_noche` TINYINT(1) NULL DEFAULT NULL,
  `motivo_despertar` VARCHAR(255) NULL DEFAULT NULL,
  `apoyos_dormir` VARCHAR(255) NULL DEFAULT NULL,
  `presencia_sueno` JSON NULL DEFAULT NULL,
  `actividad_fisica` TINYINT(1) NULL DEFAULT NULL,
  `actividad_frecuencia` TINYINT UNSIGNED NULL DEFAULT NULL,
  `actividad_tipo` VARCHAR(100) NULL DEFAULT NULL,
  `restriccion_actividad` TINYINT(1) NULL DEFAULT NULL,
  `estado_alerta` VARCHAR(100) NULL DEFAULT NULL,
  `capacidad_funcional_ext` ENUM('Adecuada', 'Limitada', 'Alterada moderadamente', 'Inexistente') NULL DEFAULT NULL,
  `apoyo_necesita` JSON NULL DEFAULT NULL,
  `dolor_en` JSON NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_actividad_reposo_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_agua`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_agua` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `litros_dia` DECIMAL(4,1) NULL DEFAULT NULL,
  `origen_agua` ENUM('Garrafón', 'Hervida', 'Clorada', 'Potable (llave)', 'Otro') NULL DEFAULT NULL,
  `liquidos_24hrs` DECIMAL(4,1) NULL DEFAULT NULL,
  `deshidratacion` JSON NULL DEFAULT NULL,
  `consume_alcohol` TINYINT(1) NULL DEFAULT NULL,
  `alcohol_frecuencia` VARCHAR(100) NULL DEFAULT NULL,
  `alcohol_cantidad` VARCHAR(100) NULL DEFAULT NULL,
  `alcohol_tipo` JSON NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_agua_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_aire`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_aire` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `frecuencia_respiratoria` TINYINT UNSIGNED NULL DEFAULT NULL,
  `llenado_capilar` ENUM('Normal (< 2 seg)', 'Prolongado (> 2 seg)') NULL DEFAULT NULL,
  `cianosis` TINYINT(1) NULL DEFAULT NULL,
  `cianosis_especificacion` VARCHAR(255) NULL DEFAULT NULL,
  `factores_resp` VARCHAR(255) NULL DEFAULT NULL,
  `fuma` TINYINT(1) NULL DEFAULT NULL,
  `cigarrillos_dia` TINYINT UNSIGNED NULL DEFAULT NULL,
  `convive_fumadores` TINYINT(1) NULL DEFAULT NULL,
  `cocina_lena` TINYINT(1) NULL DEFAULT NULL,
  `tiempo_lena` ENUM('Menos de 1 año', '1–3 años', 'Más de 3 años', 'No aplica') NULL DEFAULT NULL,
  `oxigenoterapia` ENUM('Ninguna', 'Puntas nasales', 'Mascarilla', 'Ventilador mecánico', 'Otro') NULL DEFAULT NULL,
  `enf_respiratorias_cronicas` VARCHAR(255) NULL DEFAULT NULL,
  `frecuencia_cardiaca` TINYINT UNSIGNED NULL DEFAULT NULL,
  `tension_arterial` VARCHAR(10) NULL DEFAULT NULL,
  `pvc` VARCHAR(50) NULL DEFAULT NULL,
  `varices` TINYINT(1) NULL DEFAULT NULL,
  `varices_clasificacion` VARCHAR(100) NULL DEFAULT NULL,
  `pulso_ext` VARCHAR(100) NULL DEFAULT NULL,
  `temperatura_ext` VARCHAR(100) NULL DEFAULT NULL,
  `coloracion_ext` VARCHAR(100) NULL DEFAULT NULL,
  `llenado_capilar_ext` VARCHAR(50) NULL DEFAULT NULL,
  `edema` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_aire_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_alimentos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_alimentos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `peso_kg` DECIMAL(5,2) NULL DEFAULT NULL,
  `talla_mts` DECIMAL(4,2) NULL DEFAULT NULL,
  `imc` DECIMAL(5,2) NULL DEFAULT NULL,
  `cintura_cm` DECIMAL(5,1) NULL DEFAULT NULL,
  `perimetro_abdominal_cm` DECIMAL(5,1) NULL DEFAULT NULL,
  `comidas_dia` TINYINT UNSIGNED NULL DEFAULT NULL,
  `come_familia` TINYINT(1) NULL DEFAULT NULL,
  `suplementos` TINYINT(1) NULL DEFAULT NULL,
  `suplementos_cuales` VARCHAR(255) NULL DEFAULT NULL,
  `sintomas_digestivos` JSON NULL DEFAULT NULL,
  `freq_cereales` TINYINT UNSIGNED NULL DEFAULT NULL,
  `freq_vegetales` TINYINT UNSIGNED NULL DEFAULT NULL,
  `freq_frutas` TINYINT UNSIGNED NULL DEFAULT NULL,
  `freq_carnes` TINYINT UNSIGNED NULL DEFAULT NULL,
  `freq_lacteos` TINYINT UNSIGNED NULL DEFAULT NULL,
  `freq_leguminosas` TINYINT UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_alimentos_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_eliminacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_eliminacion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `veces_orinar_dia` TINYINT UNSIGNED NULL DEFAULT NULL,
  `caracteristicas_orina` VARCHAR(255) NULL DEFAULT NULL,
  `alteraciones_vesicales` JSON NULL DEFAULT NULL,
  `evacuaciones_24hrs` TINYINT UNSIGNED NULL DEFAULT NULL,
  `caracteristicas_evacuacion` VARCHAR(255) NULL DEFAULT NULL,
  `alteraciones_intestinales` JSON NULL DEFAULT NULL,
  `auxiliares_evacuar` VARCHAR(255) NULL DEFAULT NULL,
  `secrecion_transvaginal` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_eliminacion_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_interaccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_interaccion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `problemas_auditivos` TINYINT(1) NULL DEFAULT NULL,
  `auditivo_especificacion` VARCHAR(255) NULL DEFAULT NULL,
  `apoyo_escuchar` TINYINT(1) NULL DEFAULT NULL,
  `alteraciones_auditivas` JSON NULL DEFAULT NULL,
  `problemas_visuales` TINYINT(1) NULL DEFAULT NULL,
  `visual_especificacion` VARCHAR(255) NULL DEFAULT NULL,
  `usa_lentes` ENUM('Sí — Armazón', 'Sí — Contacto', 'No') NULL DEFAULT NULL,
  `lentes_tiempo_uso` VARCHAR(100) NULL DEFAULT NULL,
  `actitud_observada` JSON NULL DEFAULT NULL,
  `tiempo_pasa_en` ENUM('En casa solo/a', 'Con la familia', 'Trabajo', 'Amistades', 'Actividades recreativas', 'Otro') NULL DEFAULT NULL,
  `calidad_relaciones` ENUM('Buena', 'Regular', 'Mala') NULL DEFAULT NULL,
  `relacion_personal_salud` ENUM('Buena', 'Regular', 'Mala') NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_interaccion_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_prevencion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_prevencion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `tipo_rh_sanguineo` VARCHAR(10) NULL DEFAULT NULL,
  `presencia_heridas` TINYINT(1) NULL DEFAULT NULL,
  `heridas_tipo_caracteristicas` VARCHAR(255) NULL DEFAULT NULL,
  `acceso_venoso` VARCHAR(255) NULL DEFAULT NULL,
  `cateterismo_vesical` TINYINT(1) NULL DEFAULT NULL,
  `otros_prevencion` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_prevencion_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`autocuidado_promocion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`autocuidado_promocion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `acepta_cambios_envejecimiento` TINYINT(1) NULL DEFAULT NULL,
  `acepta_cambios_enfermedad` TINYINT(1) NULL DEFAULT NULL,
  `alteraciones_emocionales` JSON NULL DEFAULT NULL,
  `emocional_especificacion` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `autocuidado_promocion_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`desarrollo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`desarrollo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `edad_menarca` TINYINT UNSIGNED NULL DEFAULT NULL,
  `edad_telarca` TINYINT UNSIGNED NULL DEFAULT NULL,
  `edad_pubarca` TINYINT UNSIGNED NULL DEFAULT NULL,
  `edad_menopausia` TINYINT UNSIGNED NULL DEFAULT NULL,
  `menstruacion_tipo` ENUM('Regular', 'Irregular') NULL DEFAULT NULL,
  `menstruacion_duracion` TINYINT UNSIGNED NULL DEFAULT NULL,
  `metodos_planificacion` VARCHAR(255) NULL DEFAULT NULL,
  `fum` DATE NULL DEFAULT NULL,
  `gestas` TINYINT UNSIGNED NULL DEFAULT NULL,
  `partos` TINYINT UNSIGNED NULL DEFAULT NULL,
  `abortos` TINYINT UNSIGNED NULL DEFAULT NULL,
  `cesareas` TINYINT UNSIGNED NULL DEFAULT NULL,
  `nacidos_vivos` TINYINT UNSIGNED NULL DEFAULT NULL,
  `embarazos_alto_riesgo` TINYINT(1) NULL DEFAULT NULL,
  `cirugias_reproductor` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_papanicolau` DATE NULL DEFAULT NULL,
  `resultado_papanicolau` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_exploracion_mama` DATE NULL DEFAULT NULL,
  `resultado_exploracion_mama` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_examen_prostata` DATE NULL DEFAULT NULL,
  `resultado_prostata` VARCHAR(255) NULL DEFAULT NULL,
  `cambios_vida` JSON NULL DEFAULT NULL,
  `esquema_vacunacion` ENUM('Completo', 'Incompleto') NULL DEFAULT NULL,
  `vacunas_pendientes` VARCHAR(255) NULL DEFAULT NULL,
  `asiste_campanas` TINYINT(1) NULL DEFAULT NULL,
  `privacidad_educacional` VARCHAR(255) NULL DEFAULT NULL,
  `respuesta_duelo` VARCHAR(255) NULL DEFAULT NULL,
  `enfermedad_cronica` VARCHAR(255) NULL DEFAULT NULL,
  `enferma_frecuencia` TINYINT(1) NULL DEFAULT NULL,
  `es_autosuficiente` TINYINT(1) NULL DEFAULT NULL,
  `problemas_psicologicos` TINYINT(1) NULL DEFAULT NULL,
  `consciente_enfermedad` TINYINT(1) NULL DEFAULT NULL,
  `conoce_riesgos` TINYINT(1) NULL DEFAULT NULL,
  `contacto_riesgo_ambiental` JSON NULL DEFAULT NULL,
  `es_adicto` TINYINT(1) NULL DEFAULT NULL,
  `convive_adictos` TINYINT(1) NULL DEFAULT NULL,
  `adicciones_entorno` VARCHAR(255) NULL DEFAULT NULL,
  `medidas_seguridad` JSON NULL DEFAULT NULL,
  `requiere_cuidados_especificos` TINYINT(1) NULL DEFAULT NULL,
  `cuidados_especificos_detalle` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `desarrollo_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`entorno`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`entorno` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `tipo_piso` JSON NULL DEFAULT NULL,
  `tipo_pared` JSON NULL DEFAULT NULL,
  `tipo_techo` JSON NULL DEFAULT NULL,
  `tipo_luz` JSON NULL DEFAULT NULL,
  `abastecimiento_agua` JSON NULL DEFAULT NULL,
  `purificacion_agua` JSON NULL DEFAULT NULL,
  `drenaje` JSON NULL DEFAULT NULL,
  `tratamiento_basura` JSON NULL DEFAULT NULL,
  `fauna_nociva` JSON NULL DEFAULT NULL,
  `animales_domesticos` JSON NULL DEFAULT NULL,
  `num_animales` ENUM('1 a 2', '3 a 5', 'Más de 5', 'No aplica') NULL DEFAULT NULL,
  `animales_vacunados` ENUM('Perros', 'Gatos', 'Ambos', 'Ninguno') NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `entorno_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`gds15`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`gds15` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `gds01` TINYINT(1) NULL DEFAULT NULL,
  `gds02` TINYINT(1) NULL DEFAULT NULL,
  `gds03` TINYINT(1) NULL DEFAULT NULL,
  `gds04` TINYINT(1) NULL DEFAULT NULL,
  `gds05` TINYINT(1) NULL DEFAULT NULL,
  `gds06` TINYINT(1) NULL DEFAULT NULL,
  `gds07` TINYINT(1) NULL DEFAULT NULL,
  `gds08` TINYINT(1) NULL DEFAULT NULL,
  `gds09` TINYINT(1) NULL DEFAULT NULL,
  `gds10` TINYINT(1) NULL DEFAULT NULL,
  `gds11` TINYINT(1) NULL DEFAULT NULL,
  `gds12` TINYINT(1) NULL DEFAULT NULL,
  `gds13` TINYINT(1) NULL DEFAULT NULL,
  `gds14` TINYINT(1) NULL DEFAULT NULL,
  `gds15` TINYINT(1) NULL DEFAULT NULL,
  `puntaje_total` TINYINT UNSIGNED NULL DEFAULT NULL,
  `interpretacion` ENUM('Normal', 'Depresión leve', 'Depresión grave') NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `gds15_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`katz`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`katz` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `bano` TINYINT(1) NULL DEFAULT NULL,
  `vestido` TINYINT(1) NULL DEFAULT NULL,
  `uso_wc` TINYINT(1) NULL DEFAULT NULL,
  `movilidad` TINYINT(1) NULL DEFAULT NULL,
  `continencia` TINYINT(1) NULL DEFAULT NULL,
  `alimentacion` TINYINT(1) NULL DEFAULT NULL,
  `puntaje_total` TINYINT UNSIGNED NULL DEFAULT NULL,
  `interpretacion` ENUM('Independiente', 'Dependencia leve', 'Dependencia severa') NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `katz_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`mmse`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`mmse` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `ot_anio` TINYINT(1) NULL DEFAULT NULL,
  `ot_estacion` TINYINT(1) NULL DEFAULT NULL,
  `ot_dia_fecha` TINYINT(1) NULL DEFAULT NULL,
  `ot_mes` TINYINT(1) NULL DEFAULT NULL,
  `ot_dia_semana` TINYINT(1) NULL DEFAULT NULL,
  `oe_lugar` TINYINT(1) NULL DEFAULT NULL,
  `oe_piso` TINYINT(1) NULL DEFAULT NULL,
  `oe_ciudad` TINYINT(1) NULL DEFAULT NULL,
  `oe_provincia` TINYINT(1) NULL DEFAULT NULL,
  `oe_pais` TINYINT(1) NULL DEFAULT NULL,
  `fi_peseta` TINYINT(1) NULL DEFAULT NULL,
  `fi_caballo` TINYINT(1) NULL DEFAULT NULL,
  `fi_manzana` TINYINT(1) NULL DEFAULT NULL,
  `ac_30` TINYINT(1) NULL DEFAULT NULL,
  `ac_27` TINYINT(1) NULL DEFAULT NULL,
  `ac_24` TINYINT(1) NULL DEFAULT NULL,
  `ac_21` TINYINT(1) NULL DEFAULT NULL,
  `ac_18` TINYINT(1) NULL DEFAULT NULL,
  `rd_peseta` TINYINT(1) NULL DEFAULT NULL,
  `rd_caballo` TINYINT(1) NULL DEFAULT NULL,
  `rd_manzana` TINYINT(1) NULL DEFAULT NULL,
  `le_lapiz` TINYINT(1) NULL DEFAULT NULL,
  `le_reloj` TINYINT(1) NULL DEFAULT NULL,
  `le_repeticion` TINYINT(1) NULL DEFAULT NULL,
  `le_orden1` TINYINT(1) NULL DEFAULT NULL,
  `le_orden2` TINYINT(1) NULL DEFAULT NULL,
  `le_orden3` TINYINT(1) NULL DEFAULT NULL,
  `le_lectura` TINYINT(1) NULL DEFAULT NULL,
  `le_escritura` TINYINT(1) NULL DEFAULT NULL,
  `le_copia` TINYINT(1) NULL DEFAULT NULL,
  `puntaje_total` TINYINT UNSIGNED NULL DEFAULT NULL,
  `interpretacion` ENUM('Normal', 'Sospecha patológica', 'Deterioro cognitivo', 'Demencia') NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `mmse_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`paciente_datos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`paciente_datos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `nombre` VARCHAR(150) NULL DEFAULT NULL,
  `edad` TINYINT UNSIGNED NULL DEFAULT NULL,
  `genero` ENUM('M', 'F') NULL DEFAULT NULL,
  `lugar_nacimiento` VARCHAR(150) NULL DEFAULT NULL,
  `domicilio` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_ingreso` DATE NULL DEFAULT NULL,
  `religion` VARCHAR(80) NULL DEFAULT NULL,
  `escolaridad` ENUM('Sin escolaridad', 'Primaria', 'Secundaria', 'Preparatoria', 'Licenciatura o más') NULL DEFAULT NULL,
  `estado_civil` ENUM('Soltero/a', 'Casado/a', 'Viudo/a', 'Divorciado/a', 'Unión libre') NULL DEFAULT NULL,
  `ocupacion` VARCHAR(100) NULL DEFAULT NULL,
  `dependencia_institucion` VARCHAR(100) NULL DEFAULT NULL,
  `servicios_salud` JSON NULL DEFAULT NULL,
  `cuando_acude_medico` ENUM('Por control', 'Por malestar', 'Otro') NULL DEFAULT NULL,
  `capaz_decisiones` TINYINT(1) NULL DEFAULT NULL,
  `responsable` VARCHAR(150) NULL DEFAULT NULL,
  `lleva_tratamiento` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `paciente_datos_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`patron_vida`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`patron_vida` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `relacion_familiar` ENUM('Buena', 'Regular', 'Mala') NULL DEFAULT NULL,
  `ingreso_economico` ENUM('Menos de 1 s.m.', '1–2 s.m.', 'Más de 3 s.m.', 'Ninguno') NULL DEFAULT NULL,
  `dependencia_economica` ENUM('Personal', 'Esposo/a', 'Hijos', 'Otro') NULL DEFAULT NULL,
  `estado_nutricional` ENUM('Desnutrición', 'Normal', 'Sobrepeso', 'Obesidad') NULL DEFAULT NULL,
  `cabello` ENUM('Quebradizo', 'Opaco', 'Escaso', 'Saludable', 'Más de uno') NULL DEFAULT NULL,
  `mucosas` ENUM('Sangrado', 'Infección', 'Palidez', 'Saludables') NULL DEFAULT NULL,
  `piel` ENUM('Piel de gallina', 'Seca y quebrada', 'Puntos negros', 'Seca y maltratada', 'Escamosa', 'Palidez', 'Saludable', 'Más de uno') NULL DEFAULT NULL,
  `labios` ENUM('Con lesiones', 'Secos', 'Agrietados', 'Saludables', 'Más de uno') NULL DEFAULT NULL,
  `encias` ENUM('Hinchadas', 'Sangran fácilmente', 'Más de uno', 'Saludables') NULL DEFAULT NULL,
  `nariz_orejas` ENUM('Con lesión seborreica', 'Húmedas', 'Enrojecidas', 'Más de uno', 'Saludables') NULL DEFAULT NULL,
  `unas` ENUM('Pálidas', 'Quebradas', 'Estriadas', 'Más de uno', 'Saludables') NULL DEFAULT NULL,
  `sistema_oseo` ENUM('Encorvado', 'Reducción de talla', 'Descalcificación', 'Más de uno', 'Saludable') NULL DEFAULT NULL,
  `estado_general` ENUM('Debilidad', 'Fatiga', 'Cansancio', 'Cianosis', 'Más de uno', 'Saludable') NULL DEFAULT NULL,
  `kg_subidos` ENUM('1–3 kg', '4–6 kg', '+10 kg', 'No aplica') NULL DEFAULT NULL,
  `kg_perdidos` ENUM('1–3 kg', '4–6 kg', '7–9 kg', '+10 kg', 'No aplica') NULL DEFAULT NULL,
  `dentadura` ENUM('Dientes completos', 'Faltan dientes', 'Prótesis extraíble', 'Prótesis fija', 'Más de uno') NULL DEFAULT NULL,
  `guisa_alimentos` ENUM('Aceite vegetal', 'Aceite animal', 'Manteca') NULL DEFAULT NULL,
  `problema_cavidad_oral` VARCHAR(255) NULL DEFAULT NULL,
  `problema_dental_comer` VARCHAR(255) NULL DEFAULT NULL,
  `problema_digestion` VARCHAR(255) NULL DEFAULT NULL,
  `alimentos_puede_comer` ENUM('Derivados de la leche', 'Carnes', 'Azúcares', 'Grasas', 'Sales', 'Ricos en colesterol (huevo)', 'Más de uno', 'Otros', 'Come de todo') NULL DEFAULT NULL,
  `desayuno` JSON NULL DEFAULT NULL,
  `comida` JSON NULL DEFAULT NULL,
  `cena` JSON NULL DEFAULT NULL,
  `cepillado_dientes` ENUM('Diario', 'Cada 2 días', 'Cada 3 días', 'Más de cada 4 días', 'Esporádicamente', 'Nunca') NULL DEFAULT NULL,
  `bano` ENUM('Diario', 'Cada 2 días', 'Cada 3 días') NULL DEFAULT NULL,
  `cambio_ropa` ENUM('Diario', 'Cada 2 días', 'Cada 3 días') NULL DEFAULT NULL,
  `lavado_manos` JSON NULL DEFAULT NULL,
  `enfermedad_presente` VARCHAR(255) NULL DEFAULT NULL,
  `tiene_tratamiento` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `valoracion_id` (`valoracion_id` ASC) VISIBLE,
  CONSTRAINT `patron_vida_ibfk_1`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `valoracion_adulto_mayor`.`retroalimentacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoracion_adulto_mayor`.`retroalimentacion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `valoracion_id` INT NOT NULL,
  `maestro_id` INT NOT NULL,
  `comentario` TEXT NOT NULL,
  `fecha` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `leida` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_retro_valoracion_idx` (`valoracion_id` ASC) VISIBLE,
  INDEX `fk_retro_maestro_idx` (`maestro_id` ASC) VISIBLE,
  CONSTRAINT `retroalimentacion_ibfk_maestro`
    FOREIGN KEY (`maestro_id`)
    REFERENCES `valoracion_adulto_mayor`.`usuarios` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `retroalimentacion_ibfk_valoracion`
    FOREIGN KEY (`valoracion_id`)
    REFERENCES `valoracion_adulto_mayor`.`valoraciones` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


ALTER TABLE paciente_datos 
ADD UNIQUE INDEX uq_paciente_valoracion (valoracion_id);