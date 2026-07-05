"use client";

import { useMemo, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import {
  clearSession,
  getServerSessionRaw,
  getSessionRaw,
  subscribeSession,
  type Session,
} from "@/lib/auth";

export default function Home() {
  const router = useRouter();
  const rawSession = useSyncExternalStore(subscribeSession, getSessionRaw, getServerSessionRaw);
  const session = useMemo<Session | null>(
    () => (rawSession ? (JSON.parse(rawSession) as Session) : null),
    [rawSession],
  );
  const checked = typeof window !== "undefined";

  function handleLogout() {
    clearSession();
    router.push("/login");
  }

  return (
    <main className="flex min-h-screen flex-1 flex-col bg-zinc-950 text-white">
      <header className="flex items-center justify-between border-b border-zinc-800 px-6 py-4">
        <span className="text-xl font-bold tracking-tight text-violet-500">PStreaming</span>
        {checked &&
          (session ? (
            <div className="flex items-center gap-4">
              <span className="text-sm text-zinc-300">
                {session.name ?? "User"}
                {session.rol && <span className="ml-2 text-zinc-500">({session.rol})</span>}
              </span>
              <button
                onClick={handleLogout}
                className="rounded-lg border border-zinc-700 px-4 py-2 text-sm text-zinc-300 transition hover:bg-zinc-800"
              >
                Sign out
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-3">
              <Link
                href="/login"
                className="rounded-lg border border-zinc-700 px-4 py-2 text-sm text-zinc-300 transition hover:bg-zinc-800"
              >
                Sign in
              </Link>
              <Link
                href="/register"
                className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-violet-500"
              >
                Create account
              </Link>
            </div>
          ))}
      </header>

      <section className="flex flex-1 flex-col items-center justify-center px-6 text-center">
        <h1 className="max-w-2xl text-4xl font-bold tracking-tight sm:text-5xl">
          Movies and series, secured by your voice.
        </h1>
        <p className="mt-4 max-w-xl text-zinc-400">
          PStreaming protects your account with two-factor authentication — a code by SMS or
          your own voice.
        </p>
        {checked && !session && (
          <Link
            href="/register"
            className="mt-8 rounded-lg bg-violet-600 px-6 py-3 font-medium text-white transition hover:bg-violet-500"
          >
            Get started
          </Link>
        )}
        {checked && session && (
          <p className="mt-8 text-zinc-300">
            Welcome back{session.name ? `, ${session.name}` : ""} — the catalog UI is coming
            next.
          </p>
        )}
      </section>
    </main>
  );
}
