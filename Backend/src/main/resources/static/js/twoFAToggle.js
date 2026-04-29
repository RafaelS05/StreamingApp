document.addEventListener("DOMContentLoaded", () => {
    const smsForm    = document.getElementById("sms-Form");
    const vozForm    = document.getElementById("voz-Form");
    const btnSms     = document.getElementById("btn-Sms");
    const btnVoz     = document.getElementById("btn-Voz");
    const btnRecord  = document.getElementById("btnRecord2fa");
    const btnVerify  = document.getElementById("btnVerify2fa");
    const btnSendSms = document.getElementById("btnSendSms");
    const smsStatus  = document.getElementById("smsStatus");
    const recordStatus = document.getElementById("recordStatus");

    if (!smsForm || !vozForm || !btnSms || !btnVoz) return;

    btnSms.addEventListener("click", () => {
        smsForm.classList.remove("d-none");
        vozForm.classList.add("d-none");
    });

    btnVoz.addEventListener("click", () => {
        vozForm.classList.remove("d-none");
        smsForm.classList.add("d-none");
    });

    if (btnSendSms) {
        btnSendSms.addEventListener("click", async () => {
            btnSendSms.disabled = true;
            if (smsStatus) smsStatus.textContent = "Enviando SMS...";
            try {
                const res  = await fetch("/usuario/2fa/send-sms", { method: "POST" });
                const data = await res.json();
                if (res.ok) {
                    if (smsStatus) smsStatus.textContent = "✅ SMS enviado. Revisa tu teléfono.";
                } else {
                    if (smsStatus) smsStatus.textContent = "❌ " + (data.error || "Error al enviar");
                    btnSendSms.disabled = false;
                }
            } catch (e) {
                if (smsStatus) smsStatus.textContent = "❌ Error de red.";
                btnSendSms.disabled = false;
            }
        });
    }

    if (btnRecord && btnVerify) {
        btnRecord.addEventListener("click", async () => {
            btnRecord.disabled = true;
            btnVerify.disabled = true;
            if (recordStatus) recordStatus.textContent = "🎙 Grabando...";
            try {
                // Reutiliza recordToFileInput de vozEnroll.js
                const file = await recordToFileInput("audio2faFile", 3000);
                if (recordStatus) recordStatus.textContent = `✅ Audio listo (${Math.round(file.size / 1024)} KB)`;
                btnVerify.disabled = false;
            } catch (e) {
                console.error(e);
                if (recordStatus) recordStatus.textContent = "❌ Error al grabar.";
            } finally {
                btnRecord.disabled = false;
            }
        });
    }

    // Modo inicial según cómo llegó el usuario
    const container = document.getElementById("2fa-container");
    if (container && container.dataset.modo === "sms") {
        vozForm.classList.remove("d-none");
        smsForm.classList.add("d-none");
    }
});
