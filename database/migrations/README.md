# Migraciones de base de datos SVAM

## Antes de desplegar

1. Realiza un respaldo completo de la base de datos `valoracion_adulto_mayor`.
2. Confirma que la aplicación apunta a la misma base de datos que vas a migrar.
3. Ejecuta los archivos SQL de esta carpeta en orden, usando una cuenta con permisos para crear tablas.
4. Verifica que la tabla nueva exista y que la aplicación pueda insertar y consultar registros de prueba.
5. Conserva el respaldo hasta terminar las pruebas funcionales.

## 2026-11-11 — respuestas por sección

Ejecuta `2026-11-11_create_valoracion_secciones.sql` sobre la base `valoracion_adulto_mayor`.

La migración crea `valoracion_secciones`, que conserva el JSON completo enviado por cada sección (1 a 6), con una fila por valoración y sección. El índice único permite actualizar una sección sin crear duplicados. La clave foránea elimina las respuestas asociadas si una valoración se elimina.

La migración es aditiva: no borra ni modifica las tablas existentes ni intenta completar registros históricos. Las valoraciones antiguas sin respuestas individuales permanecen incompletas y el PDF debe indicarlo.

**Importante:** el código de la aplicación no ejecuta esta migración automáticamente. No despliegues esta versión en producción hasta aplicar la migración y verificarla en un entorno de pruebas.
