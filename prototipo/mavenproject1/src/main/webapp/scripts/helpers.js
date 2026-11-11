// =============================================
// ESTADO GLOBAL DE LA VALORACIÓN
// =============================================
var valoracionId = null; // Se llena al guardar sección 1

// =============================================
// RECOLECTORES DE DATOS POR SECCIÓN
// =============================================

function recolectarSeccion0() {
  return {
    nombrePaciente:   val('nombrePaciente'),
    edadPaciente:     val('edadPaciente'),
    generoPaciente:   val('generoPaciente'),
    lugarNacimiento:  val('lugarNacimiento'),
    domicilio:        val('domicilio'),
    fechaIngreso:     val('fechaIngreso'),
    religion:         val('religion'),
    escolaridad:      val('escolaridad'),
    estadoCivil:      val('estadoCivil'),
    ocupacion:        val('ocupacion'),
    dependencia:      val('dependencia'),
    cuandoAcude:      val('cuandoAcude'),
    capazDecisiones:  val('capazDecisiones'),
    responsable:      val('responsable'),
    llevaTratamiento: val('llevaTratamiento'),
    serviciosSalud:   chks('serviciosSalud')
  };
}

function recolectarSeccion1() {
  return Object.assign(recolectarSeccionCompleta(1), {
    tipoPiso:           chks('tipoPiso'),
    tipoPared:          chks('tipoPared'),
    tipoTecho:          chks('tipoTecho'),
    tipoLuz:            chks('tipoLuz'),
    abastecimientoAgua: chks('abastecimientoAgua'),
    purificacionAgua:   chks('purificacionAgua'),
    drenaje:            chks('drenaje'),
    tratamientoBasura:  chks('tratamientoBasura'),
    faunaNociva:        chks('faunaNociva'),
    animalesDomesticos: chks('animalesDomesticos'),

    numAnimales:        val('numAnimales'),
    animalesVacunados:  val('animalesVacunados'), 
    relacionFamiliar: val('relacionFamiliar'),
        ingresoEconomico: val('ingresoEconomico'),
        dependenciaEconomica: val('dependenciaEconomica'),

        estadoNutricional: radio('nutricional'),
        
        peso: val('peso'),
        talla: val('talla'),
        imc: val('imc'),

        cabello: val('cabello'),
        mucosas: val('mucosas'),
        piel: val('piel'),
        labios: val('labios'),
        encias: val('encias'),
        narizOrejas: val('narizOrejas'),
        unas: val('unas'),
        sistemaOseo: val('sistemaOseo'),
        estadoGeneral: val('estadoGeneral'),

        kgSubidos: val('kgSubidos'),
        kgPerdidos: val('kgPerdidos'),

        dentadura: val('dentadura'),
        guisaAlimentos: val('guisaAlimentos'),

        problemaCavidadOral: val('problemaCavidad'),
        problemaDentalComer: val('problemaDental'),
        problemaDigestion: val('problemaDigestion'),

        alimentosPuedeComer: val('alimentosPuedeComer'),

        desayuno: chks('desayuno'),
        comida: chks('comida'),
        cena: chks('cena'),

        cepilladoDientes: val('cepillado'),
        bano: val('bano'),
        cambioRopa: val('cambioRopa'),

        lavadoManos: chks('lavadoManos'),

        enfermedadPresente: val('enfermedadPresente'),
        tieneTratamiento: val('tieneTratamiento')
  });
}

// Recolecta los controles reales de una sección. Los radios sin respuesta
// no se inventan y los checkbox se conservan como arreglos.
function recolectarSeccionCompleta(indice) {
  var contenedor = document.getElementById('sec-' + indice);
  if (!contenedor) throw new Error('No se encontró la sección ' + indice);
  var datos = {};
  var grupos = {};
  contenedor.querySelectorAll('input, select, textarea').forEach(function(el) {
    if (el.disabled || el.type === 'button' || el.type === 'submit') return;
    var clave = el.name || el.id;
    if (!clave) return;
    if (el.type === 'radio') {
      if (el.checked) datos[clave] = el.value;
    } else if (el.type === 'checkbox') {
      if (!grupos[clave]) grupos[clave] = [];
      if (el.checked) grupos[clave].push(el.value);
    } else {
      datos[clave] = el.value;
    }
  });
  Object.keys(grupos).forEach(function(clave) { datos[clave] = grupos[clave]; });
  return datos;
}

function recolectarSeccion2() { return recolectarSeccionCompleta(2); }
function recolectarSeccion3() { return recolectarSeccionCompleta(3); }
function recolectarSeccion4() { return recolectarSeccionCompleta(4); }
function recolectarSeccion5() { return recolectarSeccionCompleta(5); }
function recolectarSeccion6() { return recolectarSeccionCompleta(6); }

// =============================================
// GUARDADO POR SECCIÓN
// =============================================

async function guardarSeccion(seccion) {
  try {
    if (seccion === 0) {
        
      if (!valoracionId) {
        valoracionId = sessionStorage.getItem('valoracionId');
      }
      // Sección 1: crea la valoración y obtiene el ID
      var datos = recolectarSeccion0();
      var body  = new URLSearchParams(datos);

      // Servicios de salud es array — URLSearchParams no lo maneja solo
      // Quitamos el string y agregamos cada valor por separado
      body.delete('serviciosSalud');
      datos.serviciosSalud.forEach(function(s) {
        body.append('serviciosSalud', s);
      });
      
      if (valoracionId) body.append('valoracionId', valoracionId);


      var res  = await fetch('../api/valoraciones', { method: 'POST', body: body });
      var json = await res.json();

      if (res.ok && json.ok) {
        valoracionId = json.valoracionId;
        sessionStorage.setItem('valoracionId', valoracionId);
        console.log('Valoración creada con ID:', valoracionId);
      } else {
        mostrarToast('Error al guardar datos del paciente: ' + (json.mensaje || ''), true);
        return false;
      }

    } else {
      // Secciones 2-7: guardan sobre la valoración existente
      if (!valoracionId) {
        valoracionId = sessionStorage.getItem('valoracionId');
      }
      if (!valoracionId) {
        mostrarToast('No se encontró la valoración activa.', true);
        return;
      }

      var datos = window['recolectarSeccion' + seccion]();
      datos.valoracionId = valoracionId;
      datos.seccion      = seccion;

      var res  = await fetch('../api/valoraciones/seccion', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
      });
      var json = await res.json();

      if (!res.ok || !json.ok) {
        mostrarToast('Error al guardar sección ' + (seccion + 1) + ': ' + (json.mensaje || ''), true);
        return false;
      }
    }

    mostrarToast('Guardado ✓');
    return true;

  } catch (e) {
    console.error('Error guardando sección', seccion, e);
    mostrarToast('Error de conexión al guardar.', true);
    return false;
  }
}

// =============================================
// FINALIZAR VALORACIÓN (sección 7)
// =============================================

async function finalizarYGuardar() {
  var guardado = await guardarSeccion(6);
  if (!guardado || !valoracionId) return;

  try {
    var res  = await fetch('../api/valoraciones', {
      method: 'PUT',
      body: new URLSearchParams({ valoracionId: valoracionId })
    });
    var json = await res.json();

    if (res.ok && json.ok) {
      mostrarToast('Valoración completada ✓');
      // Generar PDF después de guardar
      setTimeout(function() { finalizarValoracion(); }, 800);
    } else {
      mostrarToast('Error al finalizar la valoración.', true);
    }
  } catch (e) {
    mostrarToast('Error de conexión al finalizar.', true);
  }
}

// =============================================
// HELPERS DE LECTURA DE CAMPOS
// =============================================

function val(id) {
  var el = document.getElementById(id);
  return el ? el.value : '';
}

function chks(name) {
  return Array.from(document.querySelectorAll('input[name="' + name + '"]:checked'))
              .map(function(el) { return el.value; });
}

function radio(name) {
  var el = document.querySelector('input[name="' + name + '"]:checked');
  return el ? el.value : '';
}

// =============================================
// TOAST DE NOTIFICACIÓN
// =============================================

function mostrarToast(mensaje, esError) {
  var toast = document.getElementById('toast-guardado');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast-guardado';
    toast.style.cssText = [
      'position:fixed', 'bottom:24px', 'right:24px',
      'padding:10px 20px', 'border-radius:8px',
      'font-family:Montserrat,sans-serif', 'font-size:0.82rem',
      'font-weight:600', 'z-index:9999',
      'transition:opacity 0.3s', 'box-shadow:0 4px 12px rgba(0,0,0,0.15)'
    ].join(';');
    document.body.appendChild(toast);
  }
  toast.style.background = esError ? '#c0392b' : '#27ae60';
  toast.style.color      = '#fff';
  toast.textContent      = mensaje;
  toast.style.opacity    = '1';
  clearTimeout(toast._timer);
  toast._timer = setTimeout(function() { toast.style.opacity = '0'; }, 2500);
}

// =============================================
// CARGA DE DATOS GUARDADOS AL RETOMAR
// =============================================

async function cargarDatosGuardados() {
  var id = sessionStorage.getItem('valoracionId');
  if (!id) return;
  valoracionId = id;

  try {
    var res  = await fetch('../api/valoraciones?id=' + id);
    var json = await res.json();
    if (!json.ok) return;

    // Sección 1 - paciente
    setVal('nombrePaciente',  json.nombrePaciente);
    setVal('edadPaciente',    json.edadPaciente);
    setVal('generoPaciente',  json.generoPaciente);
    setVal('lugarNacimiento', json.lugarNacimiento);
    setVal('domicilio',       json.domicilio);
    setVal('fechaIngreso',    json.fechaIngreso);
    setVal('religion',        json.religion);
    setVal('escolaridad',     json.escolaridad);
    setVal('estadoCivil',     json.estadoCivil);
    setVal('ocupacion',       json.ocupacion);
    setVal('dependencia',     json.dependencia);
    setVal('cuandoAcude',     json.cuandoAcude);
    setVal('responsable',     json.responsable);
    setChks('serviciosSalud', json.serviciosSalud);
    if (json.capazDecisiones != null) setVal('capazDecisiones', json.capazDecisiones ? 'Sí' : 'No');
    if (json.llevaTratamiento != null) setVal('llevaTratamiento', json.llevaTratamiento ? 'Sí' : 'No');

    // Sección 2 - entorno
    setChks('tipoPiso',           json.tipoPiso);
    setChks('tipoPared',          json.tipoPared);
    setChks('tipoTecho',          json.tipoTecho);
    setChks('tipoLuz',            json.tipoLuz);
    setChks('abastecimientoAgua', json.abastecimientoAgua);
    setChks('purificacionAgua',   json.purificacionAgua);
    setChks('drenaje',            json.drenaje);
    setChks('tratamientoBasura',  json.tratamientoBasura);
    setChks('faunaNociva',        json.faunaNociva);
    setChks('animalesDomesticos', json.animalesDomesticos);
    setVal('numAnimales',         json.numAnimales);
    setVal('animalesVacunados',   json.animalesVacunados);

    // Sección 2 - patrón de vida
    setVal('relacionFamiliar',     json.relacionFamiliar);
    setVal('ingresoEconomico',     json.ingresoEconomico);
    setVal('dependenciaEconomica', json.dependenciaEconomica);
    setRadio('nutricional',        json.estadoNutricional);
    setVal('peso', json.peso);
    setVal('talla',json.talla);
    setVal('imc', json.imc);
    setVal('cabello',      json.cabello);
    setVal('mucosas',      json.mucosas);
    setVal('piel',         json.piel);
    setVal('labios',       json.labios);
    setVal('encias',       json.encias);
    setVal('narizOrejas',  json.narizOrejas);
    setVal('unas',         json.unas);
    setVal('sistemaOseo',  json.sistemaOseo);
    setVal('estadoGeneral',json.estadoGeneral);
    setVal('kgSubidos',    json.kgSubidos);
    setVal('kgPerdidos',   json.kgPerdidos);
    setVal('dentadura',    json.dentadura);
    setVal('guisaAlimentos',      json.guisaAlimentos);
    setVal('problemaCavidad',     json.problemaCavidad);
    setVal('problemaDental',      json.problemaDental);
    setVal('problemaDigestion',   json.problemaDigestion);
    setVal('alimentosPuedeComer', json.alimentosPuedeComer);
    setChks('desayuno',    json.desayuno);
    setChks('comida',      json.comida);
    setChks('cena',        json.cena);
    setVal('cepillado',    json.cepillado);
    setVal('bano',         json.bano);
    setVal('cambioRopa',   json.cambioRopa);
    setChks('lavadoManos', json.lavadoManos);
    setVal('enfermedadPresente', json.enfermedadPresente);
    setVal('tieneTratamiento',   json.tieneTratamiento);

    // Restaurar cualquier control persistido, incluidas las respuestas de escalas.
    // Los controles que nunca se respondieron permanecen sin selección.
    Object.keys(json).forEach(function(clave) {
      if (clave === 'ok' || clave === 'seccionActual' || /^seccionGuardada/.test(clave)) return;
      var valor = json[clave];
      var controles = document.querySelectorAll('[name="' + CSS.escape(clave) + '"]');
      if (!controles.length) {
        var porId = document.getElementById(clave);
        if (porId && valor != null && typeof valor !== 'object') porId.value = valor;
        return;
      }
      if (controles[0].type === 'radio') {
        controles.forEach(function(el) { el.checked = String(el.value) === String(valor); });
      } else if (controles[0].type === 'checkbox') {
        var seleccionados = Array.isArray(valor) ? valor.map(String) : [];
        controles.forEach(function(el) { el.checked = seleccionados.indexOf(String(el.value)) !== -1; });
      } else if (valor != null && typeof valor !== 'object') {
        controles[0].value = valor;
      }
    });
    if (typeof calcularMmse === 'function') calcularMmse();
    if (typeof calcularGds === 'function') calcularGds();
    if (typeof calcularKatz === 'function') calcularKatz();

    // No asumir que una valoración histórica contiene respuestas que nunca se guardaron.
    window.valoracionDatosCompletos = [1, 2, 3, 4, 5, 6].every(function(n) {
      return json['seccionGuardada' + n] === true;
    });

    return json.seccionActual || 0;
  } catch (e) {
      
    console.error('Error cargando:', e); return 0;
  }
}

function setVal(id, v) {
  var el = document.getElementById(id);
  if (el && v != null && v !== 'null') el.value = v;
}

function setChks(name, arr) {
  if (!arr) return;
  var vals = Array.isArray(arr) ? arr : [];
  document.querySelectorAll('input[name="' + name + '"]').forEach(function(el) {
    el.checked = vals.indexOf(el.value) > -1;
    if (el.checked) el.closest('.casilla-item').classList.add('marcada');
  });
}

function setRadio(name, v) {
  if (!v) return;
  var el = document.querySelector('input[name="' + name + '"][value="' + CSS.escape(v) + '"]');
  if (el) { el.checked = true; el.closest('.casilla-item').classList.add('marcada'); }
}
