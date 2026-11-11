window.onload = function() {
  cambiarSeccion(0);
  generarPreguntasMmse();
  generarPreguntasGds();
  generarPreguntasKatz();
  cargandoInicial = true;

  cargarDatosGuardados().then(function(seccion) {
    if (seccion > 0) cambiarSeccion(seccion);
    cargandoInicial = false;

    var modoLectura = sessionStorage.getItem('modoLectura') === 'true';
    if (modoLectura) {
      document.querySelectorAll('input, select, textarea').forEach(function(el) {
        el.disabled = true;
      });
      var botonCerrar = document.querySelector('.btn-cerrar');
      if (botonCerrar) {
        botonCerrar.onclick = function() {
          sessionStorage.removeItem('modoLectura');
          sessionStorage.removeItem('accionPDF');
          window.location.href = sessionStorage.getItem('usuarioRol') === 'maestro' ? 'dashboard-maestro.html' : 'dashboard-estudiante.html';
        };
      }
    }

    // Imprimir únicamente después de que el servidor haya recuperado los datos.
    if (modoLectura && sessionStorage.getItem('accionPDF') === 'true') {
      sessionStorage.removeItem('accionPDF');
      if (window.valoracionDatosCompletos !== true) {
        var aviso = document.createElement('div');
        aviso.setAttribute('role', 'alert');
        aviso.style.cssText = 'margin:16px auto;padding:12px 16px;max-width:960px;border:1px solid #b7791f;background:#fff8e6;color:#744210;border-radius:8px;font-weight:600;';
        aviso.textContent = 'REGISTRO HISTÓRICO INCOMPLETO: no se encontraron todas las respuestas individuales guardadas. El documento muestra únicamente la información disponible; no se reconstruyeron respuestas faltantes.';
        var form = document.getElementById('formulario');
        if (form) form.insertBefore(aviso, form.firstChild);
      }
      window.setTimeout(function() { window.print(); }, 500);
    }
  }).catch(function(error) {
    cargandoInicial = false;
    console.error('No se pudo recuperar la valoración:', error);
    mostrarToast('No se pudieron recuperar los datos guardados.', true);
  });
};

function salir() {
  var rol = sessionStorage.getItem('usuarioRol');
  sessionStorage.removeItem('modoLectura');
  sessionStorage.removeItem('accionPDF');
  window.location.href = rol === 'maestro' ? 'dashboard-maestro.html' : 'dashboard-estudiante.html';
}
