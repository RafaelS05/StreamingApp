function RecordVoz(palabraClaveUser){
    const SpeechRecognition =
            window.SpeechRecognition || window.webkitSpeechRecognition;
    
            if (!SpeechRecognition) {
                alert("El navegador no soporta el reconocimiento de voz.");
                return; 
    }
    
    const recognition = new SpeechRecognition();
    recognition.lang = 'es-ES';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;
    
    recognition.onresult = (event) => {
        document.getElementById(palabraClaveUser).value = 
                event.results[0][0].transcript;
    };
    recognition.onerror = (e) =>{
        alert("Error al reconocer: " + e.error);
    };
    recognition.start();
}