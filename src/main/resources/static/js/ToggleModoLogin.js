function ToggleModoLogin() {
    const form = document.getElementById('login-form');
    const toggleBtn = document.getElementById('ButtonToggleModoLogin');
    const passwordMode = document.getElementById('passwordMode');
    const vozMode = document.getElementById('vozMode');
    const passwordInput = document.getElementById('passwordInput');
    const palabraClaveInput = document.getElementById('PalabraClaveUsuario');

    if (!form || !toggleBtn || !passwordMode || !vozMode || !passwordInput || !palabraClaveInput) {
        return;
    }

    const passwordAction = form.dataset.passwordAction || form.getAttribute('action') || '/usuario/login';
    const voiceAction = form.dataset.voiceAction || '/usuario/login/voz';

    const setPasswordMode = () => {
        passwordMode.classList.remove('d-none');
        vozMode.classList.add('d-none');

        passwordInput.disabled = false;
        passwordInput.required = true;

        palabraClaveInput.disabled = true;
        palabraClaveInput.required = false;
        palabraClaveInput.value = '';

        form.setAttribute('action', passwordAction);
        toggleBtn.textContent = 'Ingresar con voz (Palabra clave)';
    };

    const setVoiceMode = () => {
        passwordMode.classList.add('d-none');
        vozMode.classList.remove('d-none');

        passwordInput.disabled = true;
        passwordInput.required = false;
        passwordInput.value = '';

        palabraClaveInput.disabled = false;
        palabraClaveInput.required = true;

        form.setAttribute('action', voiceAction);
        toggleBtn.textContent = 'Ingresar con contraseña';
    };

    let isVoiceMode = false;
    setPasswordMode();

    toggleBtn.addEventListener('click', () => {
        isVoiceMode = !isVoiceMode;
        if (isVoiceMode) {
            setVoiceMode();
            return;
        }
        setPasswordMode();
    });
}

document.addEventListener('DOMContentLoaded', ToggleModoLogin);
