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
  project: Project | null;
  onClose: () => void;
  onProjectUpdate: (updated: Project) => void;
  onDeleteRequest: (project: Project) => void;
}

// EditProjectDialog component for editing project details.
export default function EditProjectDialog({
  project,
  onClose,
  onProjectUpdate,
  onDeleteRequest,
}: Props) {
  const [editTitle, setEditTitle] = useState("");
  const [editTags, setEditTags] = useState("");
  const [editDescription, setEditDescription] = useState("");
  const [saving, setSaving] = useState(false);

  // when project details change, initialize the edit fields with the project's current data
  useEffect(() => {
    if (project) {
      setEditTitle(project.title);
      setEditTags((project.tags ?? []).join(", "));
      setEditDescription(project.description ?? "");
    }
  }, [project]);

  // handle saving changes to the project
  async function handleSave() {
    if (!project) return;

    // validate for blank title
    const trimmedTitle = editTitle.trim();
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
    if (editDescription.length > 5000) {
      toast.error("Description must be 5000 characters or fewer.");
      return;
    }

    // validate tags - each tag must match the pattern and be <= 200 characters
    const tagTokens = editTags
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);
    const invalidTag = tagTokens.find((t) => !TAG_PATTERN.test(t) || t.length > 200);

    // if tag is invalid, show error message and return without updating
    if (invalidTag) {
      toast.error(
        `Invalid tag "${invalidTag}". Tags must be ≤ 200 characters and contain only letters, numbers, underscores, or hyphens.`
      );
      return;
    }

    // if validation passes, send update requests to the server sequentially
    setSaving(true);
    try {
      let finalProject: Project = project;
      let anyFailed = false;

      // update title first if changed
      if (trimmedTitle !== project.title) {
        const res = await fetch(`${API}/api/projects/${project.id}/title`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ title: trimmedTitle }),
        });
        if (res.ok) {
          finalProject = await res.json();
        } else {
          anyFailed = true;
        }
      }

      // update tags next (skip if empty — backend requires at least one valid tag)
      const newTagsStr = tagTokens.join(",");
      const oldTagsStr = (project.tags ?? []).join(",");
      if (tagTokens.length > 0 && newTagsStr !== oldTagsStr) {
        const res = await fetch(`${API}/api/projects/${project.id}/tags`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ tags: newTagsStr }),
        });
        if (res.ok) {
          finalProject = await res.json();
        } else {
          anyFailed = true;
        }
      }

      // update description last so all prior changes are already persisted
      const oldDesc = project.description ?? "";
      if (editDescription !== oldDesc) {
        const res = await fetch(`${API}/api/projects/${project.id}/description`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ description: editDescription }),
        });
        if (res.ok) {
          finalProject = await res.json();
        } else {
          anyFailed = true;
        }
      }

      // if nothing changed, just close
      if (finalProject === project && !anyFailed) {
        onClose();
        return;
      }

      onProjectUpdate(finalProject);
      onClose();
      if (anyFailed) {
        toast.warning("Some changes could not be saved.");
      } else {
        toast.success("Project updated.");
      }
    } catch {
      toast.error("Failed to save changes.");
    } finally {
      setSaving(false);
    }
  }

  // handle toggling archive status of the project
  async function handleToggleArchive() {
    if (!project) return;
    try {
      const res = await fetch(`${API}/api/projects/${project.id}/archive`, { method: "PATCH" });
      if (!res.ok) throw new Error();
      const updated: Project = await res.json();
      onProjectUpdate(updated);
      onClose();
      toast.success(updated.archived ? "Project archived." : "Project unarchived.");
    } catch {
      toast.error("Failed to update archive status.");
    }
  }

  // handle restoring a deleted project
  async function handleRestore() {
    if (!project) return;
    try {
      const res = await fetch(`${API}/api/projects/${project.id}/restore`, { method: "POST" });
      if (!res.ok) throw new Error();
      const updated: Project = await res.json();
      onProjectUpdate(updated);
      onClose();
      toast.success("Project restored.");
    } catch {
      toast.error("Failed to restore project.");
    }
  }

  return (
    <Dialog open={project !== null} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Edit Project</DialogTitle>
          <DialogDescription className="sr-only">
            Edit the title, tags, and description of this project.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={(e) => { e.preventDefault(); handleSave(); }}>
        <div className="space-y-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="edit-title">Title</Label>
            <Input
              id="edit-title"
              value={editTitle}
              onChange={(e) => setEditTitle(e.target.value)}
              maxLength={200}
              disabled={saving}
              autoFocus
            />
            <p className="text-xs text-muted-foreground text-right">
              {editTitle.length} / 200
            </p>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="edit-tags">Tags</Label>
            <Input
              id="edit-tags"
              value={editTags}
              onChange={(e) => setEditTags(e.target.value)}
              placeholder="e.g. frontend, react-app"
              disabled={saving}
            />
            <p className="text-xs text-muted-foreground">
              Comma-separated. Letters, numbers, underscores, and hyphens only.
            </p>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="edit-description">Description</Label>
            <Textarea
              id="edit-description"
              value={editDescription}
              onChange={(e) => setEditDescription(e.target.value)}
              placeholder="Description (optional)"
              maxLength={5000}
              disabled={saving}
              rows={4}
              className="resize-none"
            />
            <p className="text-xs text-muted-foreground text-right">
              {editDescription.length} / 5000
            </p>
          </div>
        </div>

        <DialogFooter>
          <div className="flex w-full items-center justify-between">
            <div className="flex gap-2">
              {project && !project.deleted && (
                <>
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    disabled={saving}
                    onClick={handleToggleArchive}
                  >
                    {project.archived ? "Unarchive" : "Archive"}
                  </Button>
                  <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    disabled={saving}
                    onClick={() => {
                      onDeleteRequest(project);
                      onClose();
                    }}
                  >
                    Delete
                  </Button>
                </>
              )}
              {project?.deleted && (
                <Button type="button" variant="outline" size="sm" disabled={saving} onClick={handleRestore}>
                  Restore
                </Button>
              )}
            </div>
            <div className="flex gap-2">
              <Button type="button" variant="outline" size="sm" disabled={saving} onClick={onClose}>
                Cancel
              </Button>
              <Button type="submit" size="sm" disabled={saving}>
                Save Changes
              </Button>
            </div>
          </div>
        </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
