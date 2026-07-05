async function recordToFileInput(inputId, ms = 15000) {
    const stream = await navigator.mediaDevices.getUserMedia({audio: true});

    const mimeType =
            MediaRecorder.isTypeSupported("audio/webm;codecs=opus")
            ? "audio/webm;codecs=opus"
            : (MediaRecorder.isTypeSupported("audio/webm") ? "audio/webm" : "");

    const recorder = new MediaRecorder(stream, mimeType ? {mimeType} : undefined);
    const chunks = [];

    recorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0)
            chunks.push(e.data);
    };

    const stopped = new Promise((resolve, reject) => {
        recorder.onerror = (e) => reject(e.error || e);
        recorder.onstop = () => {
            try {
                const finalType = recorder.mimeType || mimeType || "audio/webm";
                const blob = new Blob(chunks, {type: finalType});
                const file = new File([blob], "enroll.webm", {type: finalType});

                const dt = new DataTransfer();
                dt.items.add(file);

                const input = document.getElementById(inputId);
                if (!input)
                    throw new Error(`No existe input con id="${inputId}"`);

                input.files = dt.files;
                resolve(file);
            } finally {
                stream.getTracks().forEach((t) => t.stop());
            }
        };
    });

    recorder.start();
    setTimeout(() => recorder.stop(), ms);

    return stopped;
}

window.RecordFileInput = async function (inputId) {
    const status = document.getElementById("enrollStatus");
    const btn = document.getElementById("btnSubmitEnroll");
    if (status)
        status.textContent = "Grabando...";
    if (btn)
        btn.disabled = true;

    try {
        const file = await recordToFileInput(inputId, 3000);
        if (status)
            status.textContent = `Audio listo ✅ (${Math.round(file.size / 1024)} KB)`;
        if (btn)
            btn.disabled = false;
    } catch (e) {
        console.error(e);
        if (status)
            status.textContent = "Error grabando audio ❌";
    }
};

