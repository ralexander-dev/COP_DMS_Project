"use client";

import { useState } from "react";
import { DbConnectRequest } from "@/lib/types";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";

const API = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

// login component for connecting to a MySQL db
export default function Login({ onConnected } : { onConnected: () => void }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isExternal, setIsExternal] = useState(false);
  const [host, setHost] = useState("");
  const [port, setPort] = useState("3306");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleLogin() {
    setLoading(true);
    setError(null);

    const body: DbConnectRequest = { username, password };
    if (isExternal) {
      body.host = host;
      body.port = parseInt(port, 10);
    }

    try {
      const res = await fetch(`${API}/api/db/connect`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        onConnected();
      } else {
        const data = await res.json();
        setError(data.message ?? "Failed to connect to the database");
      }
    } catch {
      setError("Could not connect to the database");
    }
    setLoading(false);
  }

  return (
    <Dialog open={true}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Connect to Database</DialogTitle>
          <DialogDescription>
            Please enter your database credentials to connect.
          </DialogDescription>
        </DialogHeader>

        <form className="flex flex-col gap-4" onSubmit={e => { e.preventDefault(); handleLogin(); }}>
          <div className="flex items-center gap-3">
            <Switch id="external-toggle" checked={isExternal} onCheckedChange={setIsExternal} />
            <Label htmlFor="external-toggle">External Database</Label>
          </div>

          {isExternal && (
            <div className="flex gap-2">
              <div className="flex flex-col gap-1 flex-1">
                <Label htmlFor="db-host">Host / IP</Label>
                <Input id="db-host" value={host} onChange={e => setHost(e.target.value)} placeholder="192.168.1.100" />
              </div>
              <div className="flex flex-col gap-1 w-24">
                <Label htmlFor="db-port">Port</Label>
                <Input id="db-port" value={port} onChange={e => setPort(e.target.value)} placeholder="3306" />
              </div>
            </div>
          )}

          <div className="flex flex-col gap-1">
            <Label htmlFor="db-username">Username</Label>
            <Input id="db-username" value={username} onChange={e => setUsername(e.target.value)} placeholder="username" />
          </div>
          <div className="flex flex-col gap-1">
            <Label htmlFor="db-password">Password</Label>
            <Input id="db-password" type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="password" />
          </div>
          {error && <div className="text-red-500">{error}</div>}
          <Button type="submit" disabled={loading}>{loading ? "Connecting..." : "Connect"}</Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}