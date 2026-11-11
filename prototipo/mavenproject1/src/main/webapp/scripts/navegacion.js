// =============================================
// NAVEGACIÓN ENTRE PANTALLAS
// =============================================
function mostrar(id) {
  document.querySelectorAll('.pantalla').forEach(function(p) {
    p.classList.remove('activa');
  });
  document.getElementById(id).classList.add('activa');
}

function navegar(pagina) {
  window.location.href = pagina;
}

// =============================================
// NAVEGACIÓN ENTRE SECCIONES DEL FORMULARIO
// Con guardado automático al avanzar
// =============================================
var seccionActual = 0;
var totalSecciones = 7;
var guardandoSeccion = false; 
var cargandoInicial = false; // Evita doble clic

async function cambiarSeccion(n) {
  // Solo se permiten las siete secciones existentes.
  if (!Number.isInteger(n) || n < 0 || n >= totalSecciones) return;

  // Evitar que un doble clic permita saltar el guardado en curso.
  var avanzando = n > seccionActual;
  if (avanzando && guardandoSeccion) return;

  if (avanzando && !guardandoSeccion && !cargandoInicial && sessionStorage.getItem('modoLectura') !== 'true') {
    guardandoSeccion = true;

    // Deshabilitar botón Siguiente visualmente
    var btnSig = document.querySelector('#sec-' + seccionActual + ' .btn-nav:not(.btn-nav-prev)');
    if (btnSig) {
      btnSig.disabled    = true;
      btnSig.textContent = 'Guardando...';
    }

    var guardado = await guardarSeccion(seccionActual);

    // Restaurar botón incluso si el guardado falla.
    if (btnSig) {
      btnSig.disabled = false;
      btnSig.textContent = 'Siguiente →';
    }
    guardandoSeccion = false;

    // Nunca avanzar si el servidor no confirmó la persistencia.
    if (!guardado) return;
  }

  // Ocultar sección actual
  var secAnterior = document.getElementById('sec-' + seccionActual);
  var pasoAnterior = document.getElementById('paso-' + seccionActual);
  if (secAnterior)  secAnterior.classList.remove('activa');
  if (pasoAnterior) {
    pasoAnterior.classList.remove('activo');
    if (avanzando) pasoAnterior.classList.add('completado');
  }

  seccionActual = n;

  // Mostrar nueva sección
  var secNueva  = document.getElementById('sec-' + seccionActual);
  var pasoNuevo = document.getElementById('paso-' + seccionActual);
  if (secNueva)  secNueva.classList.add('activa');
  if (pasoNuevo) {
    pasoNuevo.classList.remove('completado');
    pasoNuevo.classList.add('activo');
  }

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// =============================================
// CASILLAS: marcar/desmarcar estilo
// =============================================
function toggleMarcada(input) {
  var label = input.closest('.casilla-item');
  if (input.type === 'radio') {
    document.querySelectorAll('input[name="' + input.name + '"]').forEach(function(r) {
      r.closest('.casilla-item').classList.remove('marcada');
    });
  }
  if (input.checked) {
    label.classList.add('marcada');
  } else {
    label.classList.remove('marcada');
  }
}