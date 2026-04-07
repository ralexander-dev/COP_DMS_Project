import Link from "next/link";
import { Separator } from "@/components/ui/separator";
import ThemeToggle from "@/components/custom/ThemeToggle";
import Logout from "@/components/custom/Logout";

const links = [
  { label: "Home", href: "/" },
];

// Navigation bar used across the entire application for consistent navigation and theme toggling. 
export default function Navbar({ isConnected }: { isConnected: boolean }) {
  return (
    <header className="w-full border-b bg-background">
      <div className="mx-auto flex h-12 max-w-5xl items-center gap-6 px-4">
        <Link href="/" className="text-base font-semibold tracking-tight">
          Project DMS
        </Link>
        <Separator orientation="vertical" className="h-5" />
        <nav className="flex items-center gap-4">
          {links.map(({ label, href }) => (
            <Link
              key={href}
              href={href}
              className="text-sm text-muted-foreground transition-colors hover:text-foreground"
            >
              {label}
            </Link>
          ))}
        </nav>
        <div className="ml-auto flex items-center gap-3">
          {isConnected && <Logout />}
          <ThemeToggle />
        </div>
      </div>
    </header>
  );
}
