// =============================================
// MINI MENTAL (MMSE) — preguntas y puntaje
// =============================================
var preguntasMmse = [
  { texto: '¿En qué año estamos?',              pts: 1 },
  { texto: '¿En qué estación del año estamos?', pts: 1 },
  { texto: '¿En qué día (fecha) estamos?',      pts: 1 },
  { texto: '¿En qué mes estamos?',              pts: 1 },
  { texto: '¿En qué día de la semana?',         pts: 1 },
  { texto: '¿En qué hospital o lugar estamos?', pts: 1 },
  { texto: '¿En qué piso o planta estamos?',    pts: 1 },
  { texto: '¿En qué ciudad estamos?',           pts: 1 },
  { texto: '¿En qué provincia o estado?',       pts: 1 },
  { texto: '¿En qué país estamos?',             pts: 1 }
];

function generarPreguntasMmse() {
  var contenedor = document.getElementById('preguntas-mmse');
  if (!contenedor || contenedor.innerHTML !== '') return;
  preguntasMmse.forEach(function(p, i) {
    contenedor.innerHTML += `
      <div class="escala-pregunta">
        <span>${i+1}. ${p.texto}</span>
        <div class="resp-sinno">
          <label><input type="radio" name="mmse${i}" value="1" onchange="calcularMmse()"/> Correcto</label>
          <label><input type="radio" name="mmse${i}" value="0" onchange="calcularMmse()"/> Incorrecto</label>
        </div>
      </div>`;
  });
}

function calcularMmse() {
  var total = 0;
  for (var i = 0; i < 30; i++) {
    var sel = document.querySelector('input[name="mmse' + i + '"]:checked');
    if (sel) total += parseInt(sel.value);
  }
  document.getElementById('puntaje-mmse').textContent = total;
  var inter, col;
  if      (total >= 27) { inter = 'Normal';               col = 'var(--verde-ok)';   }
  else if (total >= 24) { inter = 'Sospecha patológica';   col = 'var(--dorado-uaa)'; }
  else if (total >= 12) { inter = 'Deterioro cognitivo';   col = 'var(--rojo-error)'; }
  else                  { inter = 'Demencia';              col = 'var(--rojo-error)'; }
  var el = document.getElementById('inter-mmse');
  el.textContent = inter;
  el.style.color  = col;
}

// =============================================
// GDS-15 — preguntas y puntaje automático
// =============================================
// puntua=true → un punto si responde "Sí"
// puntua=false → un punto si responde "No"
var preguntasGds = [
  { texto: '¿Está satisfecho/a con su vida en general?',                               puntaSi: false },
  { texto: '¿Ha abandonado muchas de sus actividades e intereses?',                    puntaSi: true  },
  { texto: '¿Siente que su vida está vacía?',                                          puntaSi: true  },
  { texto: '¿Se siente aburrido/a con frecuencia?',                                    puntaSi: true  },
  { texto: '¿Se siente de buen humor la mayor parte del tiempo?',                      puntaSi: false },
  { texto: '¿Tiene miedo de que le pase algo malo?',                                   puntaSi: true  },
  { texto: '¿Se siente feliz la mayor parte del tiempo?',                              puntaSi: false },
  { texto: '¿Se siente a menudo desesperanzado/a?',                                    puntaSi: true  },
  { texto: '¿Prefiere quedarse en casa en lugar de salir e intentar cosas nuevas?',    puntaSi: true  },
  { texto: '¿Siente que tiene más problemas de memoria que la mayoría?',               puntaSi: true  },
  { texto: '¿Cree que estar vivo/a en este momento es maravilloso?',                   puntaSi: false },
  { texto: '¿Siente que su vida en este momento es bastante inútil?',                  puntaSi: true  },
  { texto: '¿Se siente lleno/a de energía?',                                           puntaSi: false },
  { texto: '¿Considera que su situación no tiene remedio?',                            puntaSi: true  },
  { texto: '¿Cree que la mayoría de las personas son mejores que usted?',              puntaSi: true  }
];

function generarPreguntasGds() {
  var contenedor = document.getElementById('preguntas-gds');
  if (!contenedor || contenedor.innerHTML !== '') return;
  preguntasGds.forEach(function(p, i) {
    contenedor.innerHTML += `
      <div class="escala-pregunta">
        <span>${i+1}. ${p.texto}</span>
        <div class="resp-sinno">
          <label><input type="radio" name="gds${i}" value="si" onchange="calcularGds()"/> Sí</label>
          <label><input type="radio" name="gds${i}" value="no" onchange="calcularGds()"/> No</label>
        </div>
      </div>`;
  });
}

function calcularGds() {
  var total = 0;
  preguntasGds.forEach(function(p, i) {
    var sel = document.querySelector('input[name="gds' + i + '"]:checked');
    if (!sel) return;
    if (p.puntaSi  && sel.value === 'si') total++;
    if (!p.puntaSi && sel.value === 'no') total++;
  });
  document.getElementById('puntaje-gds').textContent = total;
  var inter, col;
  if (total <= 5)      { inter = 'Normal';          col = 'var(--verde-ok)'; }
  else if (total <= 10){ inter = 'Depresión leve';  col = 'var(--dorado-uaa)'; }
  else                 { inter = 'Depresión grave'; col = 'var(--rojo-error)'; }
  var el = document.getElementById('inter-gds');
  el.textContent = inter;
  el.style.color = col;
}

// =============================================
// ÍNDICE DE KATZ — actividades y puntaje
// =============================================
var actividadesKatz = [
  'Baño',
  'Vestido',
  'Uso del WC',
  'Movilidad',
  'Continencia',
  'Alimentación'
];

function generarPreguntasKatz() {
  var contenedor = document.getElementById('preguntas-katz');
  if (!contenedor || contenedor.innerHTML !== '') return;
  actividadesKatz.forEach(function(a, i) {
    contenedor.innerHTML += `
      <div class="escala-pregunta">
        <span>${i+1}. ${a}</span>
        <div class="resp-sinno">
          <label><input type="radio" name="katz${i}" value="0" onchange="calcularKatz()"/> Independiente</label>
          <label><input type="radio" name="katz${i}" value="1" onchange="calcularKatz()"/> Dependiente</label>
        </div>
      </div>`;
  });
}

function calcularKatz() {
  var total = 0;
  for (var i = 0; i < actividadesKatz.length; i++) {
    var sel = document.querySelector('input[name="katz' + i + '"]:checked');
    if (sel) total += parseInt(sel.value);
  }
  document.getElementById('puntaje-katz').textContent = total;
  var inter, col;
  if (total === 0)     { inter = 'Independiente';       col = 'var(--verde-ok)'; }
  else if (total <= 2) { inter = 'Dependencia leve';    col = 'var(--dorado-uaa)'; }
  else                 { inter = 'Dependencia severa';  col = 'var(--rojo-error)'; }
  var el = document.getElementById('inter-katz');
  el.textContent = inter;
  el.style.color = col;
}

// =============================================
// FINALIZAR VALORACIÓN
// =============================================
function finalizarValoracion() {
  // La valoración ya fue guardada por finalizarYGuardar().
  // Imprimimos el formulario recuperado desde MySQL: no se crea otro registro
  // ni se envían respuestas duplicadas a un servlet legado.
  window.print();
}

function generarResumenHTML() {
  // Helpers
  const val  = id => document.getElementById(id)?.value || '—';
  const sel  = id => document.getElementById(id)?.value || '—';
  const chks = name => [...document.querySelectorAll(`input[name="${name}"]:checked`)]
                         .map(el => el.closest('.casilla-item')?.textContent.trim())
                         .filter(Boolean).join(', ') || '—';
  const radio = name => document.querySelector(`input[name="${name}"]:checked`)?.value || '—';

  // MMSE — puntaje ya calculado en pantalla
  const puntajeMmse = document.getElementById('puntaje-mmse')?.textContent || '0';
  const interMmse   = document.getElementById('inter-mmse')?.textContent   || '—';
  const puntajeGds  = document.getElementById('puntaje-gds')?.textContent  || '0';
  const interGds    = document.getElementById('inter-gds')?.textContent    || '—';
  const puntajeKatz = document.getElementById('puntaje-katz')?.textContent || '0';
  const interKatz   = document.getElementById('inter-katz')?.textContent   || '—';

  return `<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <title>Resumen de Valoración</title>
  <style>
    body { font-family: Arial, sans-serif; font-size: 12px; color: #222; margin: 30px; }
    h1   { font-size: 16px; text-align: center; color: #003087; margin-bottom: 4px; }
    h2   { font-size: 13px; color: #003087; border-bottom: 1px solid #003087; margin: 18px 0 8px; }
    p    { margin: 3px 0; }
    .fila { display: flex; gap: 12px; flex-wrap: wrap; }
    .campo { flex: 1 1 200px; margin-bottom: 6px; }
    .etiqueta { font-weight: bold; font-size: 11px; color: #555; }
    .valor    { font-size: 12px; }
    .puntaje  { font-size: 20px; font-weight: bold; color: #003087; }
    .subtitulo { font-size: 11px; color: #888; margin-bottom: 12px; text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 6px; }
    th, td { border: 1px solid #DDD; padding: 5px 8px; font-size: 11px; }
    th { background: #F0F4FF; font-weight: bold; }
    @media print {
      body { margin: 15px; }
      button { display: none; }
    }
  </style>
</head>
<body>

  <h1>Guía de Valoración del Adulto Mayor</h1>
  <p class="subtitulo">Referente teórico: Dorotea Elizabeth Orem &nbsp;|&nbsp; Fecha: ${new Date().toLocaleDateString('es-MX')}</p>

  <h2>I. Datos del Paciente</h2>
  <div class="fila">
    <div class="campo"><div class="etiqueta">Nombre</div><div class="valor">${val('nombrePaciente')}</div></div>
    <div class="campo"><div class="etiqueta">Edad</div><div class="valor">${val('edadPaciente')}</div></div>
    <div class="campo"><div class="etiqueta">Género</div><div class="valor">${sel('generoPaciente')}</div></div>
  </div>

  <h2>V. Mini Mental (MMSE)</h2>
  <div class="fila">
    <div class="campo"><div class="etiqueta">Puntaje</div><div class="puntaje">${puntajeMmse} / 30</div></div>
    <div class="campo"><div class="etiqueta">Interpretación</div><div class="valor">${interMmse}</div></div>
  </div>

  <h2>VI. Escala de Depresión GDS-15</h2>
  <div class="fila">
    <div class="campo"><div class="etiqueta">Puntaje</div><div class="puntaje">${puntajeGds} / 15</div></div>
    <div class="campo"><div class="etiqueta">Interpretación</div><div class="valor">${interGds}</div></div>
  </div>

  <h2>VII. Índice de Katz</h2>
  <div class="fila">
    <div class="campo"><div class="etiqueta">Dependencias</div><div class="puntaje">${puntajeKatz} / 6</div></div>
    <div class="campo"><div class="etiqueta">Interpretación</div><div class="valor">${interKatz}</div></div>
  </div>

  <br/>
  <button onclick="window.print()">🖨️ Imprimir / Guardar PDF</button>

</body>
</html>`;
}

//=============================================
//Cálculo del IMC
//=============================================
function calcularIMC() {
    var pesoInput = document.getElementById('peso');
    var tallaInput = document.getElementById('talla');
    var imcInput = document.getElementById('imc');

    if (!pesoInput || !tallaInput || !imcInput) {
        return;
    }

    var peso = parseFloat(pesoInput.value);
    var talla = parseFloat(tallaInput.value);

    if (peso > 0 && talla > 0) {
        imcInput.value = (peso / (talla * talla)).toFixed(2);
    } else {
        imcInput.value = '';
    }
}
//Calcular automáticamente al cambiar peso o talla
document.addEventListener('DOMContentLoaded', function () {
    var peso = document.getElementById('peso');
    var talla = document.getElementById('talla');

    if (peso) {
        peso.addEventListener('input', calcularIMC);
    }

    if (talla) {
        talla.addEventListener('input', calcularIMC);
    }

    calcularIMC();
});