"use client";
import { useEffect, useRef, useState } from "react";
import { toast } from "sonner";
import { Project, ImportResult, SkippedEntry } from "@/lib/types";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

interface Props {
  open: boolean;
  onClose: () => void;
  onImportComplete: (projects: Project[]) => void;
}

// ImportProjectsDialog component for uploading a .txt file to import multiple projects at once.
export default function ImportProjectsDialog({ open, onClose, onImportComplete }: Props) {
  const inputRef = useRef<HTMLInputElement>(null); // reference to the hidden file input element
  const [dragging, setDragging] = useState(false); // state to track whether a file is being dragged over the drop zone
  const [file, setFile] = useState<File | null>(null); // state to store the selected file
  const [pending, setPending] = useState(false); // state to indicate whether the import operation is in progress
  const [result, setResult] = useState<ImportResult | null>(null); // state to store the result of the import operation, including counts and any skipped entries
  const [errorMsg, setErrorMsg] = useState<string | null>(null); // state to store any error message from the import operation
  const [skippedOpen, setSkippedOpen] = useState(false); // state to track whether the skipped entries dialog is open

  useEffect(() => {
    if (open) {
      setFile(null);
      setResult(null);
      setErrorMsg(null);
      setSkippedOpen(false);
    }
  }, [open]);

  // handle file selection either through the file input or drag-and-drop
  function handleFiles(files: FileList | null) {
    if (!files || files.length === 0) return;
    setFile(files[0]);
    setResult(null);
    setErrorMsg(null);
  }

  // handle dropping a file onto the drop zone
  function handleDrop(e: React.DragEvent) {
    e.preventDefault();
    setDragging(false);
    handleFiles(e.dataTransfer.files);
  }

  // handle the import operation by sending the selected file to the server and processing the response
  async function handleImport() {
    if (!file) return; // if no file is recieved, return early
    setErrorMsg(null);
    setResult(null);
    setPending(true);
    // attempt to upload the file to the server and process the response
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await fetch(`${API}/api/projects/import`, {
        method: "POST",
        body: formData,
      }); 
      if (!res.ok) {
        const data = await res.json().catch(() => null);
        setErrorMsg(data?.message ?? `Server error (${res.status}).`);
        return;
      }
      const data: ImportResult = await res.json();
      setResult(data);
      setSkippedOpen(false);
      toast.success(`Imported ${data.importedCount} project${data.importedCount !== 1 ? "s" : ""}.`);

      // refresh the full project list so the table reflects all newly imported projects
      const listRes = await fetch(`${API}/api/projects`);
      if (listRes.ok) {
        const projects: Project[] = await listRes.json();
        onImportComplete(projects);
      }
    } catch {
      setErrorMsg("Network error — could not reach the server.");
      toast.error("Import failed.");
    } finally {
      setPending(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Import Projects</DialogTitle>
          <DialogDescription className="sr-only">
            Upload a .txt file to import multiple projects at once.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-2">
          {/* Format hint */}
          <div className="rounded-md border bg-muted/50 px-4 py-3 text-xs text-muted-foreground">
            <p className="mb-1.5 font-medium text-foreground">File format — one project per line:</p>
            <p className="font-mono">title</p>
            <p className="font-mono">title|description</p>
            <p className="font-mono">title|description|tag1,tag2,tag3</p>
          </div>

          <Separator />

          {/* Drop zone */}
          <button
            type="button"
            className={`flex w-full cursor-pointer flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed px-6 py-8 text-sm text-muted-foreground transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
              dragging ? "border-primary bg-primary/5" : "border-border hover:border-primary/50"
            }`}
            onClick={() => inputRef.current?.click()}
            onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
            onDragLeave={() => setDragging(false)}
            onDrop={handleDrop}
          >
            <span className="text-2xl">📄</span>
            {file ? (
              <span className="font-medium text-foreground">{file.name}</span>
            ) : (
              <span>
                Drag &amp; drop a <code className="font-mono">.txt</code> file here, or click to browse
              </span>
            )}
          </button>
          <input
            ref={inputRef}
            type="file"
            accept=".txt"
            className="hidden"
            onChange={(e) => handleFiles(e.target.files)}
          />

          {/* Error */}
          {errorMsg && (
            <p className="text-sm text-destructive">{errorMsg}</p>
          )}

          {/* Result */}
          {result && (
            <div className="rounded-md border bg-muted/30 px-4 py-3 text-sm">
              <p className="font-medium">
                {result.importedCount} project{result.importedCount !== 1 ? "s" : ""} imported successfully.
              </p>
              {(result.skippedEntries?.length ?? 0) > 0 && (
                <div className="mt-2 flex flex-col gap-2">
                  <button
                    type="button"
                    className="flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground"
                    onClick={() => setSkippedOpen((o) => !o)}
                  >
                    <span>{skippedOpen ? "▾" : "▸"}</span>
                    {result.skippedEntries.length} skipped line{result.skippedEntries.length !== 1 ? "s" : ""}
                  </button>
                  {skippedOpen && (
                    <ul className="flex flex-col gap-1">
                      {result.skippedEntries.map((entry: SkippedEntry, i: number) => (
                        <li key={i} className="rounded-md bg-muted px-3 py-2 text-xs">
                          <span className="font-mono text-foreground">{entry.entry}</span>
                          <span className="ml-2 text-muted-foreground">— {entry.reason}</span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              )}
            </div>
          )}
        </div>

        <DialogFooter>
          <Button variant="outline" size="sm" disabled={pending} onClick={onClose}>
            Close
          </Button>
          <Button size="sm" disabled={!file || pending} onClick={handleImport}>
            {pending ? "Importing…" : "Import"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
