"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AuthCard from "@/components/AuthCard";
import FormField from "@/components/FormField";
import VoiceRecorder from "@/components/VoiceRecorder";
import {
  ApiError,
  getAuthMethods,
  verifySms,
  verifyVoice,
  type UserLoginResponse,
} from "@/lib/api";
import { getPending2FA, saveSession, type Pending2FA } from "@/lib/auth";

export default function TwoFactorPage() {
  const router = useRouter();
  const [pending, setPending] = useState<Pending2FA | null>(null);
  const [methodName, setMethodName] = useState<string | null>(null);
  const [code, setCode] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const p = getPending2FA();
    if (!p) {
      router.replace("/login");
      return;
    }
    getAuthMethods()
      .then((list) => {
        const m = list.find((x) => x.idMethod === p.authMethod);
        setPending(p);
        setMethodName(m?.name ?? "SMS");
      })
      .catch(() => {
        setPending(p);
        setMethodName("SMS");
      });
  }, [router]);

  function complete(res: UserLoginResponse) {
    saveSession(res);
    router.push("/");
  }

  async function handleSms(e: React.FormEvent) {
    e.preventDefault();
    if (!pending) return;
    setError(null);
    setLoading(true);
    try {
      complete(await verifySms(pending.tempToken, code));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not reach the server");
      setLoading(false);
    }
  }

  async function handleVoice(audio: Blob) {
    if (!pending) return;
    setError(null);
    setLoading(true);
    try {
      complete(await verifyVoice(pending.tempToken, audio));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not reach the server");
      setLoading(false);
    }
  }

  if (!pending || !methodName) {
    return (
      <AuthCard title="Verification" subtitle="Loading…">
        <p className="text-sm text-zinc-400">Preparing verification…</p>
      </AuthCard>
    );
  }

  if (methodName === "VOZ") {
    return (
      <AuthCard
        title="Voice verification"
        subtitle="Record the same phrase you enrolled with to finish signing in."
      >
        <VoiceRecorder onSubmit={handleVoice} submitLabel="Verify voice" disabled={loading} />
        {loading && <p className="mt-4 text-center text-sm text-zinc-400">Verifying…</p>}
        {error && <p className="mt-4 text-center text-sm text-red-400">{error}</p>}
      </AuthCard>
    );
  }

  return (
    <AuthCard
      title="SMS verification"
      subtitle="We sent a 6-digit code to your phone. It expires in 5 minutes."
    >
      <form onSubmit={handleSms} className="flex flex-col gap-4">
        <FormField
          label="Verification code"
          inputMode="numeric"
          pattern="\d{6}"
          maxLength={6}
          required
          autoComplete="one-time-code"
          placeholder="000000"
          value={code}
          onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))}
        />

        {error && <p className="text-sm text-red-400">{error}</p>}

        <button
          type="submit"
          disabled={loading || code.length !== 6}
          className="mt-2 rounded-lg bg-violet-600 py-2.5 font-medium text-white transition hover:bg-violet-500 disabled:opacity-50"
        >
          {loading ? "Verifying…" : "Verify"}
        </button>
      </form>

      <button
        type="button"
        onClick={() => router.push("/login")}
        className="mt-6 w-full text-center text-sm text-zinc-400 hover:text-zinc-200"
      >
        Back to sign in
      </button>
    </AuthCard>
  );
}
