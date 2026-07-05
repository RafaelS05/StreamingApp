"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import AuthCard from "@/components/AuthCard";
import FormField from "@/components/FormField";
import VoiceRecorder from "@/components/VoiceRecorder";
import {
  ApiError,
  enrollVoice,
  getAuthMethods,
  register,
  type AuthMethod,
} from "@/lib/api";

export default function RegisterPage() {
  const router = useRouter();
  const [methods, setMethods] = useState<AuthMethod[]>([]);
  const [form, setForm] = useState({
    name: "",
    surname: "",
    email: "",
    password: "",
    phone: "",
    authMethod: "",
  });
  // Set after a successful register when the user chose VOZ — switches the
  // card to the voice enrollment step
  const [enrollUserId, setEnrollUserId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getAuthMethods()
      .then((list) => {
        setMethods(list);
        if (list.length > 0) {
          setForm((f) => ({ ...f, authMethod: String(list[0].idMethod) }));
        }
      })
      .catch(() => setError("Could not load 2FA methods. Is the API running?"));
  }, []);

  function set(field: keyof typeof form) {
    return (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm((f) => ({ ...f, [field]: e.target.value }));
  }

  const chosenMethod = methods.find((m) => String(m.idMethod) === form.authMethod);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const user = await register({
        name: form.name,
        surname: form.surname,
        email: form.email,
        password: form.password,
        phone: form.phone,
        authMethod: Number(form.authMethod),
      });
      if (chosenMethod?.name === "VOZ") {
        setEnrollUserId(user.idUsuario);
        setLoading(false);
      } else {
        router.push("/login");
      }
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not reach the server");
      setLoading(false);
    }
  }

  async function handleEnroll(audio: Blob) {
    if (!enrollUserId) return;
    setError(null);
    setLoading(true);
    try {
      await enrollVoice(enrollUserId, audio);
      router.push("/login");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not reach the server");
      setLoading(false);
    }
  }

  if (enrollUserId) {
    return (
      <AuthCard
        title="Enroll your voice"
        subtitle="Record a short phrase (3–5 seconds). You'll repeat it to sign in."
      >
        <VoiceRecorder onSubmit={handleEnroll} submitLabel="Enroll voice" disabled={loading} />
        {loading && <p className="mt-4 text-center text-sm text-zinc-400">Enrolling…</p>}
        {error && <p className="mt-4 text-center text-sm text-red-400">{error}</p>}
      </AuthCard>
    );
  }

  return (
    <AuthCard title="Create account" subtitle="Join PStreaming">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <div className="grid grid-cols-2 gap-4">
          <FormField label="First name" required value={form.name} onChange={set("name")} />
          <FormField label="Last name" required value={form.surname} onChange={set("surname")} />
        </div>
        <FormField
          label="Email"
          type="email"
          required
          autoComplete="email"
          value={form.email}
          onChange={set("email")}
        />
        <FormField
          label="Password"
          type="password"
          required
          minLength={8}
          autoComplete="new-password"
          value={form.password}
          onChange={set("password")}
        />
        <FormField
          label="Phone"
          type="tel"
          required
          placeholder="+50688888888"
          value={form.phone}
          onChange={set("phone")}
        />

        <label className="block">
          <span className="mb-1.5 block text-sm font-medium text-zinc-300">
            Two-factor method
          </span>
          <select
            required
            value={form.authMethod}
            onChange={set("authMethod")}
            className="w-full rounded-lg border border-zinc-700 bg-zinc-800 px-3.5 py-2.5 text-white outline-none transition focus:border-violet-500 focus:ring-1 focus:ring-violet-500"
          >
            {methods.map((m) => (
              <option key={m.idMethod} value={m.idMethod}>
                {m.name === "VOZ" ? "Voice recognition" : "SMS code"}
              </option>
            ))}
          </select>
        </label>

        {error && <p className="text-sm text-red-400">{error}</p>}

        <button
          type="submit"
          disabled={loading || methods.length === 0}
          className="mt-2 rounded-lg bg-violet-600 py-2.5 font-medium text-white transition hover:bg-violet-500 disabled:opacity-50"
        >
          {loading ? "Creating account…" : "Create account"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-zinc-400">
        Already have an account?{" "}
        <Link href="/login" className="font-medium text-violet-400 hover:text-violet-300">
          Sign in
        </Link>
      </p>
    </AuthCard>
  );
}
