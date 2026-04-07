"use client";
import { useEffect, useState } from "react";
import { Project } from "@/lib/types";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";

const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";
const TAG_PATTERN = /^[\w-]+$/;

interface Props {
  open: boolean;
  onClose: () => void;
  onProjectCreated: (project: Project) => void;
}

// NewProjectDialog component for creating a new project.
export default function NewProjectDialog({ open, onClose, onProjectCreated }: Props) {
  const [title, setTitle] = useState("");
  const [tags, setTags] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (open) {
      setTitle("");
      setTags("");
      setDescription("");
    }
  }, [open]);

  // handle creating a new project with optional tags and description
  async function handleCreate() {
    // validate for blank title
    const trimmedTitle = title.trim();
    if (!trimmedTitle) {
      toast.error("Title cannot be blank.");
      return;
    }
    // validate title length
    if (trimmedTitle.length > 200) {
      toast.error("Title must be 200 characters or fewer.");
      return;
    }
    // validate description length
    if (description.length > 5000) {
      toast.error("Description must be 5000 characters or fewer.");
      return;
    }
    // validate tags format and length
    const tagTokens = tags
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);
    const invalidTag = tagTokens.find((t) => !TAG_PATTERN.test(t) || t.length > 200);
    if (invalidTag) {
      toast.error(
        `Invalid tag "${invalidTag}". Tags must be ≤ 200 characters and contain only letters, numbers, underscores, or hyphens.`
      );
      return;
    }

    setSaving(true);
    // attempt to create the project and then update it with tags and description if provided
    try {
      // create the project with just the title first
      const postRes = await fetch(`${API}/api/projects`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title: trimmedTitle }),
      });

      if (!postRes.ok) {
        const data = await postRes.json().catch(() => null);
        toast.error(data?.message ?? "Failed to create project.");
        return;
      }

      const newProject: Project = await postRes.json(); // new project returned from the server with generated ID

      let finalProject: Project = newProject;
      let anyFailed = false;

      // update tags sequentially first so the description PUT reads the already-saved tags
      if (tagTokens.length > 0) {
        const res = await fetch(`${API}/api/projects/${newProject.id}/tags`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ tags: tagTokens.join(",") }),
        });
        if (res.ok) {
          finalProject = await res.json();
        } else {
          anyFailed = true;
        }
      }

      // update description after tags so both fields are preserved
      if (description.trim()) {
        const res = await fetch(`${API}/api/projects/${newProject.id}/description`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ description }),
        });
        if (res.ok) {
          finalProject = await res.json();
        } else {
          anyFailed = true;
        }
      }

      onProjectCreated(finalProject);
      onClose();

      if (anyFailed) {
        toast.warning("Project created, but tags/description could not be saved.");
      } else {
        toast.success("Project created.");
      }
    } catch {
      toast.error("Failed to create project.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>New Project</DialogTitle>
          <DialogDescription className="sr-only">
            Create a new project with a title, optional tags, and an optional description.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={(e) => { e.preventDefault(); handleCreate(); }}>
        <div className="space-y-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="new-title">Title</Label>
            <Input
              id="new-title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              maxLength={200}
              disabled={saving}
              autoFocus
            />
            <p className="text-xs text-muted-foreground text-right">
              {title.length} / 200
            </p>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="new-tags">Tags</Label>
            <Input
              id="new-tags"
              value={tags}
              onChange={(e) => setTags(e.target.value)}
              placeholder="e.g. frontend, react-app"
              disabled={saving}
            />
            <p className="text-xs text-muted-foreground">
              Comma-separated. Letters, numbers, underscores, and hyphens only.
            </p>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="new-description">Description</Label>
            <Textarea
              id="new-description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Description (optional)"
              maxLength={5000}
              disabled={saving}
              rows={4}
              className="resize-none"
            />
            <p className="text-xs text-muted-foreground text-right">
              {description.length} / 5000
            </p>
          </div>
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" size="sm" disabled={saving} onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" size="sm" disabled={saving}>
            Create Project
          </Button>
        </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
