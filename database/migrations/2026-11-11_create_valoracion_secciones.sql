-- SVAM: almacenamiento íntegro y recuperable de cada sección del formulario.
-- Ejecutar una sola vez sobre valoracion_adulto_mayor antes de desplegar esta versión.
-- No modifica ni elimina datos históricos.

CREATE TABLE IF NOT EXISTS valoracion_secciones (
    id INT NOT NULL AUTO_INCREMENT,
    valoracion_id INT NOT NULL,
    seccion TINYINT UNSIGNED NOT NULL,
    payload JSON NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_valoracion_seccion (valoracion_id, seccion),
    KEY idx_valoracion_secciones_valoracion (valoracion_id),
    CONSTRAINT fk_valoracion_secciones_valoracion
        FOREIGN KEY (valoracion_id)
        REFERENCES valoraciones (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_valoracion_seccion
        CHECK (seccion BETWEEN 1 AND 6)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
