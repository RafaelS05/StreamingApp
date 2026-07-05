function switchTab(tab) {
    const buttons = document.querySelectorAll('.tab-button');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    buttons.forEach(btn => btn.classList.remove('active'));

    if (tab === 'login') {
        buttons[0].classList.add('active');
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
    } else {
        buttons[1].classList.add('active');
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
    }
}

// Función para mostrar mensajes
function showMessage(type, text) {
    const messagesContainer = document.getElementById('form-messages');
    messagesContainer.innerHTML = `<div class="form-message ${type}">${text}</div>`;

    setTimeout(() => {
        messagesContainer.innerHTML = '';
    }, 5000);
}

// Validación de formularios
document.getElementById('login-form').addEventListener('submit', function (e) {
    e.preventDefault();

    const email = this.querySelector('input[type="email"]').value;
    const password = this.querySelector('input[type="password"]').value;

    if (!email || !password) {
        showMessage('error', '❌ Por favor completa todos los campos');
        return;
    }
});

document.getElementById('register-form').addEventListener('submit', function (e) {
    e.preventDefault();

    const inputs = this.querySelectorAll('input[required]');
    const passwords = this.querySelectorAll('input[type="password"]');

    let isValid = true;

    // Validar campos requeridos
    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.style.borderColor = '#F44336';
        } else {
            input.style.borderColor = 'rgba(30, 144, 255, 0.3)';
        }
    });

    // Validar contraseñas coincidan
    if (passwords[0].value !== passwords[1].value) {
        isValid = false;
        passwords.forEach(p => p.style.borderColor = '#F44336');
        showMessage('error', '❌ Las contraseñas no coinciden');
        return;
    }

    if (!isValid) {
        showMessage('error', '❌ Por favor completa todos los campos requeridos');
        return;
    }
});

// Efectos adicionales para inputs
document.querySelectorAll('.form-input').forEach(input => {
    input.addEventListener('focus', function () {
        this.parentElement.style.transform = 'scale(1.02)';
    });

    input.addEventListener('blur', function () {
        this.parentElement.style.transform = 'scale(1)';
    });
});

// Animación de carga inicial
window.addEventListener('load', function () {
    document.querySelector('.form-container').style.animation = 'slideInUp 0.8s ease-out';
});

