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
          window.location.href = 'dashboard-estudiante.html';
        };
      }
    }

    // Imprimir únicamente después de que el servidor haya recuperado los datos.
    if (modoLectura && sessionStorage.getItem('accionPDF') === 'true') {
      sessionStorage.removeItem('accionPDF');
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
