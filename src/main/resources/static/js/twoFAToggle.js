async function recordToFileInput(inputId, ms = 3000) {
  const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
  const recorder = new MediaRecorder(stream);
  const chunks = [];

  recorder.ondataavailable = e => chunks.push(e.data);

  recorder.onstop = () => {
    const blob = new Blob(chunks, { type: recorder.mimeType });
    const file = new File([blob], "voice2fa.webm", { type: recorder.mimeType });
    const dt = new DataTransfer();
    dt.items.add(file);
    document.getElementById(inputId).files = dt.files;
    stream.getTracks().forEach(t => t.stop());
  };

  recorder.start();
  setTimeout(() => recorder.stop(), ms);
}

document.addEventListener("DOMContentLoaded", () => {
  const smsForm = document.getElementById("sms-Form");
  const vozForm = document.getElementById("voz-Form");
  const btnSms = document.getElementById("btn-Sms");
  const btnVoz = document.getElementById("btn-Voz");

  const btnRecord = document.getElementById("btnRecord2fa");
  const btnVerify = document.getElementById("btnVerify2fa");

  if (!smsForm || !vozForm || !btnSms || !btnVoz) return;

  btnSms.addEventListener("click", () => {
    smsForm.classList.remove("d-none");
    vozForm.classList.add("d-none");
  });

  btnVoz.addEventListener("click", () => {
    vozForm.classList.remove("d-none");
    smsForm.classList.add("d-none");
  });

  if (btnRecord && btnVerify) {
    btnRecord.addEventListener("click", async () => {
      btnVerify.disabled = true;
      await recordToFileInput("audio2faFile", 3000);
      btnVerify.disabled = false;
    });
  }
});
