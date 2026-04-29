let recognitionInstance = null;
let isListening = false;

function RecordVoz(palabraClaveUser) {
    const SpeechRecognition =
            window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
        alert("El navegador no soporta el reconocimiento de voz.");
        return;
    }

    if (isListening && recognitionInstance) {
        recognitionInstance.stop();
        return;
    }

    const recognition = new SpeechRecognition();
    recognitionInstance = recognition;
    isListening = true;

    recognition.lang = 'es-ES';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onresult = (event) => {
        const texto = event.results[0][0].transcript;
        const palabra = document.getElementById(palabraClaveUser);
        if (palabra)
            palabra.value = texto;
    };

    recognition.onerror = (e) => {
        let msg = "Error al reconocer: " + e.error;

        if (e.error === "not-allowed" || e.error === "service-not-allowed") {
            msg = "Permiso de micrófono denegado. Actívalo en la configuración del sitio.";
        } else if (e.error === "no-speech") {
            msg = "No se detectó voz. Intente hablar de nuevo.";
        } else if (e.error === "audio-capture") {
            msg = "No se pudo capturar audio. Verifique su micrófono.";
        }

        alert(msg);
    };

    recognition.onend = () => {
        isListening = false;
        recognitionInstance = null;
    };

    try {
        recognition.start();
    } catch (err) {
        isListening = false;
        recognitionInstance = null;
        alert("No se pudo iniciar el reconocimiento. Intente nuevamente.");
    }
}

function initLoginToggle() {
    const form = document.getElementById("login-form");
    const toggleBtn = document.getElementById("ButtonToggleModoLogin");
    const passwordMode = document.getElementById("passwordMode");
    const vozMode = document.getElementById("vozMode");
    const passwordInput = passwordMode?.querySelector('input[name="password"]');
    const vozInput = document.getElementById("palabraClaveUser");
    const btnGrabar = document.getElementById("GrabarVoz");

    if (!form || !toggleBtn || !passwordMode || !vozMode || !passwordInput || !vozInput || !btnGrabar)
        return;

    const Action_Password = "/usuario/login/password";
    const Action_Voz = "/usuario/login/voz";

    let modo = "password";

    function setModo(nuevoModo) {
        modo = nuevoModo;

        if (modo === "password") {
            passwordMode.classList.remove("d-none");
            vozMode.classList.add("d-none");

            passwordInput.required = true;
            vozInput.required = false;
            vozInput.value = "";

            form.action = Action_Password;
            toggleBtn.textContent = "Ingresar con voz (Palabra clave)";
        } else {
            passwordMode.classList.add("d-none");
            vozMode.classList.remove("d-none");

            passwordInput.required = false;
            passwordInput.value = "";

            vozInput.required = true;

            form.action = Action_Voz;
            toggleBtn.textContent = "Ingresar con contraseña";
        }
    }
    
    toggleBtn.addEventListener("click", () => {
        setModo(modo === "password" ? "voz" : "password");
    });

    btnGrabar.addEventListener("click", () => {
        RecordVoz("palabraClaveUser");
    });

    setModo("password");
}

document.addEventListener("DOMContentLoaded", initLoginToggle);
