"use client";

import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";

const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

// logout button
export default function Logout() {
  const router = useRouter();

  async function handleLogout() {
    await fetch(`${API}/api/db/disconnect`, { method: "POST" });
    router.refresh();
  }

  return (
    <Button variant="destructive" size="sm" onClick={handleLogout}>
      Logout
    </Button>
  );
}
