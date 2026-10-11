"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();

    try {
      const response = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/auth/login`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            email,
            password,
          }),
        }
      );

      if (!response.ok) {
        setMessage("Invalid email or password");
        return;
      }

      const data = await response.json();

      localStorage.setItem("token", data.token);

      router.push("/dashboard");
    } catch (error) {
      console.error(error);
      setMessage("Login failed");
    }
  }

  return (
  <main className="min-h-screen flex items-center justify-center bg-gray-100">
    <div className="w-full max-w-md">
      <div className="mb-6 text-center">
        <h1 className="text-3xl font-bold text-black">
          Inventory & Procurement System
        </h1>

        <p className="mt-2 text-sm text-gray-600">
          Sign in to continue
        </p>
      </div>

      <form
        onSubmit={handleLogin}
        className="rounded-lg border border-gray-300 bg-white p-8 shadow-sm"
      >
        <h2 className="mb-6 text-2xl font-bold text-black">
          Login
        </h2>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="mb-4 w-full rounded border border-gray-400 bg-white px-3 py-3 text-black placeholder:text-gray-500 focus:border-blue-500 focus:outline-none"
          required
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="mb-4 w-full rounded border border-gray-400 bg-white px-3 py-3 text-black placeholder:text-gray-500 focus:border-blue-500 focus:outline-none"
          required
        />

        <button
          type="submit"
          className="w-full rounded bg-blue-600 py-3 font-medium text-white hover:bg-blue-700"
        >
          Login
        </button>

        {message && (
          <p className="mt-4 text-sm text-red-600">
            {message}
          </p>
        )}
      </form>
    </div>
  </main>
);
}