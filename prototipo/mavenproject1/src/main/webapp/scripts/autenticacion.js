// autenticacion.js
// Maneja el formulario de login y la redirección por rol

document.addEventListener('DOMContentLoaded', () => {
    const form    = document.getElementById('formLogin');
    const btnLogin = document.getElementById('btnLogin');
    const errorMsg = document.getElementById('errorLogin');

    if (!form) return; // Solo corre en la página de login

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMsg.textContent = '';
        btnLogin.disabled = true;
        btnLogin.textContent = 'Ingresando...';

        const datos = new FormData(form);

        try {
            const res = await fetch('api/login', {
                method: 'POST',
                body: new URLSearchParams(datos) // Servlet espera form-urlencoded
            });

            const json = await res.json();

            if (json.ok) {
                // Guardar nombre en sessionStorage para mostrarlo en el dashboard
                sessionStorage.setItem('usuarioNombre', json.nombre);
                sessionStorage.setItem('usuarioRol',    json.rol);
                window.location.href = json.redirect;
            } else {
                errorMsg.textContent = json.mensaje;
                btnLogin.disabled = false;
                btnLogin.textContent = 'Ingresar';
            }

        } catch (err) {
            errorMsg.textContent = 'No se pudo conectar con el servidor.';
            btnLogin.disabled = false;
            btnLogin.textContent = 'Ingresar';
        }
    });
});

/** Llama esto en cualquier página protegida para verificar sesión activa */
async function verificarSesion(rolRequerido = null) {
    try {
        const res  = await fetch('api/sesion');
        const json = await res.json();

        if (!json.activa) {
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