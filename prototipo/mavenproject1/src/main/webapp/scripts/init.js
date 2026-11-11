window.onload = function() {
  cambiarSeccion(0);
  generarPreguntasMmse();
  generarPreguntasGds();
  generarPreguntasKatz();
  cargandoInicial = true;
  cargarDatosGuardados().then(function(seccion) {
    if (seccion > 0) cambiarSeccion(seccion);
    cargandoInicial = false;
  });
  
 if (sessionStorage.getItem('modoLectura') === 'true') {
  document.querySelectorAll('input, select, textarea').forEach(function(el) {
    el.disabled = true;
  });
  document.querySelector('.btn-cerrar').onclick = function() {
    sessionStorage.removeItem('modoLectura');
    window.location.href = 'dashboard-maestro.html';
  };
}
};

function salir() {
  var rol = sessionStorage.getItem('usuarioRol');
  window.location.href = rol === 'maestro' ? 'dashboard-maestro.html' : 'dashboard-estudiante.html';
}
