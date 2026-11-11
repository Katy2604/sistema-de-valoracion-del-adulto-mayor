// autenticacion.js
// Maneja el formulario de login y la redirección por rol.

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formLogin');
    if (!form) return; // Solo corre en la página de login.

    const btnLogin = document.getElementById('btnLogin');
    const errorMsg = document.getElementById('errorLogin');
    const checkConf = document.getElementById('checkConf');

    function mostrarError(mensaje) {
        if (errorMsg) errorMsg.textContent = mensaje;
    }

    // Si falta la casilla por un cambio accidental en el HTML, se bloquea el acceso.
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        mostrarError('');

        if (!checkConf || !checkConf.checked) {
            mostrarError('Para ingresar, es necesario seleccionar la casilla de aceptación del aviso de confidencialidad y manejo de datos personales.');
            if (checkConf) checkConf.focus();
            return;
        }

        if (!btnLogin) {
            mostrarError('No fue posible iniciar sesión. Actualiza la página e inténtalo de nuevo.');
            return;
        }

        btnLogin.disabled = true;
        btnLogin.textContent = 'Ingresando...';

        const datos = new FormData(form);

        try {
            const res = await fetch('api/login', {
                method: 'POST',
                body: new URLSearchParams(datos),
                credentials: 'same-origin',
                cache: 'no-store'
            });

            const json = await res.json();

            if (res.ok && json.ok) {
                sessionStorage.setItem('usuarioNombre', json.nombre);
                sessionStorage.setItem('usuarioRol', json.rol);
                window.location.href = json.redirect;
            } else {
                mostrarError(json.mensaje || 'No fue posible iniciar sesión.');
                btnLogin.disabled = false;
                btnLogin.textContent = 'INGRESAR';
            }

        } catch (err) {
            mostrarError('No se pudo conectar con el servidor. Inténtalo nuevamente.');
            btnLogin.disabled = false;
            btnLogin.textContent = 'INGRESAR';
        }
    });

    if (checkConf) {
        checkConf.addEventListener('change', () => {
            if (checkConf.checked && errorMsg) errorMsg.textContent = '';
        });
    }
});

/** Llama esto en cualquier página protegida para verificar sesión activa. */
async function verificarSesion(rolRequerido = null) {
    try {
        const res = await fetch('api/sesion', { credentials: 'same-origin', cache: 'no-store' });
        const json = await res.json();

        if (!res.ok || !json.activa) {
            window.location.href = '../index.html';
            return null;
        }
        if (rolRequerido && json.rol !== rolRequerido) {
            window.location.href = '../index.html';
            return null;
        }
        return json;
    } catch {
        window.location.href = '../index.html';
        return null;
    }
}
