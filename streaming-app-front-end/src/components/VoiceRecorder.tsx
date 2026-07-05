"use client";

import { useEffect, useRef, useState } from "react";

interface VoiceRecorderProps {
  // Called with the recorded audio when the user confirms the take
  onSubmit: (audio: Blob) => void;
  submitLabel: string;
  disabled?: boolean;
}

type RecorderState = "idle" | "recording" | "recorded";

export default function VoiceRecorder({ onSubmit, submitLabel, disabled }: VoiceRecorderProps) {
  const [state, setState] = useState<RecorderState>("idle");
  const [seconds, setSeconds] = useState(0);
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [micError, setMicError] = useState<string | null>(null);

  const recorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);
  const blobRef = useRef<Blob | null>(null);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      recorderRef.current?.stream.getTracks().forEach((t) => t.stop());
      if (audioUrl) URL.revokeObjectURL(audioUrl);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function startRecording() {
    setMicError(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const recorder = new MediaRecorder(stream);
      chunksRef.current = [];
      recorder.ondataavailable = (e) => chunksRef.current.push(e.data);
      recorder.onstop = () => {
        const blob = new Blob(chunksRef.current, { type: recorder.mimeType });
        blobRef.current = blob;
        setAudioUrl((prev) => {
          if (prev) URL.revokeObjectURL(prev);
          return URL.createObjectURL(blob);
        });
        stream.getTracks().forEach((t) => t.stop());
        setState("recorded");
      };
      recorderRef.current = recorder;
      recorder.start();
      setSeconds(0);
      timerRef.current = setInterval(() => setSeconds((s) => s + 1), 1000);
      setState("recording");
    } catch {
      setMicError("Microphone access denied. Allow microphone permissions and try again.");
    }
  }

  function stopRecording() {
    if (timerRef.current) clearInterval(timerRef.current);
    recorderRef.current?.stop();
  }

  return (
    <div className="flex flex-col items-center gap-4">
      {state === "idle" && (
        <button
          type="button"
          onClick={startRecording}
          disabled={disabled}
          className="rounded-lg bg-violet-600 px-6 py-3 font-medium text-white transition hover:bg-violet-500 disabled:opacity-50"
        >
          Start recording
        </button>
      )}

      {state === "recording" && (
        <>
          <div className="flex items-center gap-2 text-red-400">
            <span className="h-3 w-3 animate-pulse rounded-full bg-red-500" />
            Recording… {seconds}s
          </div>
          <button
            type="button"
            onClick={stopRecording}
            className="rounded-lg bg-red-600 px-6 py-3 font-medium text-white transition hover:bg-red-500"
          >
            Stop
          </button>
        </>
      )}

      {state === "recorded" && audioUrl && (
        <>
          <audio controls src={audioUrl} className="w-full" />
          <div className="flex gap-3">
            <button
              type="button"
              onClick={startRecording}
              disabled={disabled}
              className="rounded-lg border border-zinc-600 px-5 py-2.5 text-zinc-300 transition hover:bg-zinc-800 disabled:opacity-50"
            >
              Re-record
            </button>
            <button
              type="button"
              onClick={() => blobRef.current && onSubmit(blobRef.current)}
              disabled={disabled}
              className="rounded-lg bg-violet-600 px-5 py-2.5 font-medium text-white transition hover:bg-violet-500 disabled:opacity-50"
            >
              {submitLabel}
            </button>
          </div>
        </>
      )}

      {micError && <p className="text-sm text-red-400">{micError}</p>}
    </div>
  );
}
