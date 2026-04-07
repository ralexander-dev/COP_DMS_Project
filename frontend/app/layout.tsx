/*
  layout.tsx
  Root layout component for the Next.js frontend application.
  This component sets up the global HTML structure for all children pages. 
*/
import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { ThemeProvider } from "next-themes";
import { Toaster } from "@/components/ui/sonner";
import Navbar from "@/components/custom/Navbar";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Project DMS",
  description: "Database Management System",
};

const API = process.env.API_URL ?? process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

async function getIsConnected(): Promise<boolean> {
  try {
    const res = await fetch(`${API}/api/db/status`, { cache: "no-store" });
    if (!res.ok) return false;
    const data = await res.json();
    return data?.connected === true;
  } catch {
    return false;
  }
}

// RootLayout
export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const isConnected = await getIsConnected();

  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
      suppressHydrationWarning
    >
      <body className="min-h-full flex flex-col">
        {/* 
          ThemeProvider manages light/dark mode theme accross the application.
        */}
        <ThemeProvider attribute="class" defaultTheme="system" enableSystem>
          <Navbar isConnected={isConnected} />
          <main className="flex-1">{children}</main>
          {/*
            Toaster is used for showing notifications across the app (for client-side success/error messages).
          */}
          <Toaster />
        </ThemeProvider>
      </body>
    </html>
  );
}
