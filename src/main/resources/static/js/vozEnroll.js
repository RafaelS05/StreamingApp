async function recordToFileInput(inputId, ms = 3000) {
  const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
  const recorder = new MediaRecorder(stream);
  const chunks = [];

  recorder.ondataavailable = e => chunks.push(e.data);

  recorder.onstop = () => {
    const blob = new Blob(chunks, { type: recorder.mimeType });
    const file = new File([blob], "enroll.webm", { type: recorder.mimeType });
    const dt = new DataTransfer();
    dt.items.add(file);
    document.getElementById(inputId).files = dt.files;
    stream.getTracks().forEach(t => t.stop());
  };

  recorder.start();
  setTimeout(() => recorder.stop(), ms);
}

document.addEventListener("DOMContentLoaded", () => {
  const btn = document.getElementById("btnRecordEnroll");
  const status = document.getElementById("enrollStatus");
  if (!btn) return;

  btn.addEventListener("click", async () => {
    status.textContent = "Grabando...";
    await recordToFileInput("audioEnrollFile", 3000);
    status.textContent = "Audio listo ✅";
  });
});
