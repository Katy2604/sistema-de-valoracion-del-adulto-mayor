'use strict';

const assert = require('assert');
const fs = require('fs');
const path = require('path');
const vm = require('vm');

const root = path.resolve(__dirname, '../src/main/webapp');
const pages = [
  'index.html',
  'pages/avisoConfidencialidad.html',
  'pages/dashboard-estudiante.html',
  'pages/dashboard-maestro.html',
  'pages/formularioValoracion.html',
  'pages/admin/usuarios.html'
];

function read(relativePath) {
  return fs.readFileSync(path.join(root, relativePath), 'utf8');
}

function checkLocalAssets(pagePath, html) {
  const refs = [];
  for (const match of html.matchAll(/<script\b[^>]*\bsrc=["']([^"']+)["']/gi)) refs.push(match[1]);
  for (const match of html.matchAll(/<link\b[^>]*\bhref=["']([^"']+\.css(?:\?[^"']*)?)["']/gi)) refs.push(match[1]);
  for (const ref of refs) {
    if (/^(https?:)?\/\//i.test(ref) || ref.startsWith('data:')) continue;
    const clean = ref.split('?')[0].split('#')[0];
    const target = path.resolve(root, path.dirname(pagePath), clean);
    assert.ok(target.startsWith(root + path.sep), pagePath + ': referencia fuera de webapp: ' + ref);
    assert.ok(fs.existsSync(target), pagePath + ': recurso local inexistente: ' + ref);
  }
}

function checkInlineScripts(pagePath, html) {
  let index = 0;
  for (const match of html.matchAll(/<script\b(?![^>]*\bsrc=)[^>]*>([\s\S]*?)<\/script>/gi)) {
    index++;
    const code = match[1].trim();
    if (!code) continue;
    new vm.Script(code, { filename: pagePath + '#inline-' + index });
  }
}

for (const page of pages) {
  const html = read(page);
  checkLocalAssets(page, html);
  checkInlineScripts(page, html);
}

const form = read('pages/formularioValoracion.html');
for (let i = 0; i <= 6; i++) {
  assert.ok(form.includes('id="sec-' + i + '"'), 'Falta sección sec-' + i);
}
for (let i = 0; i < 30; i++) {
  assert.ok(form.includes('name="mmse' + i + '"'), 'Falta respuesta MMSE ' + i);
}

const formJs = fs.readFileSync(path.resolve(__dirname, '../src/main/webapp/scripts/formulario.js'), 'utf8');
assert.ok((formJs.match(/puntaSi:/g) || []).length === 15, 'GDS-15 debe contener 15 reactivos');
assert.ok((formJs.match(/^  '/gm) || []).length >= 6, 'Katz debe conservar sus actividades');

const helpers = fs.readFileSync(path.resolve(__dirname, '../src/main/webapp/scripts/helpers.js'), 'utf8');
for (let i = 1; i <= 6; i++) {
  assert.ok(helpers.includes('recolectarSeccion' + i), 'Falta recolector de sección ' + i);
}
assert.ok(helpers.includes('../api/valoraciones/seccion'), 'No se guarda el contenido de secciones');
assert.ok(helpers.includes('throw e;'), 'La recuperación debe propagar errores para bloquear impresión');

const navigation = fs.readFileSync(path.resolve(__dirname, '../src/main/webapp/scripts/navegacion.js'), 'utf8');
assert.ok(navigation.includes('if (!guardado) return;'), 'La navegación debe bloquear avance si falla el guardado');

const sectionServlet = fs.readFileSync(path.resolve(__dirname, '../src/main/java/com/mycompany/mavenproject1/ValoracionesSeccionServlet.java'), 'utf8');
assert.ok(sectionServlet.includes('AND completada=0'), 'La escritura de secciones debe bloquear valoraciones finalizadas');
assert.ok(sectionServlet.includes('usuario.getId()'), 'La escritura debe validar propietario');

const valuationServlet = fs.readFileSync(path.resolve(__dirname, '../src/main/java/com/mycompany/mavenproject1/ValoracionesServlet.java'), 'utf8');
assert.ok(valuationServlet.includes('puedeConsultar(id, usuario)'), 'La lectura debe validar propietario o rol autorizado');
assert.ok(valuationServlet.includes('valoracion_secciones'), 'La finalización debe requerir secciones persistidas');

const login = fs.readFileSync(path.resolve(__dirname, '../src/main/java/com/mycompany/mavenproject1/Loginservlet.java'), 'utf8');
assert.ok(login.includes('aceptaConfidencialidad'), 'El servidor debe exigir aceptación del aviso');
assert.ok(login.includes('changeSessionId()'), 'El inicio de sesión debe renovar el identificador de sesión');

console.log('SVAM static checks: OK (' + pages.length + ' páginas, referencias locales, scripts inline y contratos críticos).');
