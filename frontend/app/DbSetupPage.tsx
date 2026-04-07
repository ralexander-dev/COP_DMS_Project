"use client";

import { useRouter } from "next/navigation";
import Login from "@/components/custom/Login";

export default function DbSetupPage() {
  const router = useRouter();

  return (
    <div className="mx auto w-full max-w-5xl px-4 py-8">
      <Login onConnected={() => router.refresh()} />
    </div>
  );
}

