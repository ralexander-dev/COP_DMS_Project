"use client";
import { Project } from "@/lib/types";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import ProjectStatusBadge from "@/components/custom/ProjectStatusBadge";
import ProjectTagsBadge from "@/components/custom/ProjectTagsBadge";

interface Props {
  project: Project | null;
  onClose: () => void;
}

export default function ProjectDetailsDialog({ project, onClose }: Props) {
  return (
    <Dialog open={project !== null} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Project Details</DialogTitle>
          <DialogDescription className="sr-only">
            View details for this project.
          </DialogDescription>
        </DialogHeader>

        {project && (
          <div className="space-y-4 py-2">
            <div className="grid grid-cols-[6rem_1fr] items-start gap-y-3 gap-x-4 text-sm">
              <span className="text-muted-foreground font-medium">ID</span>
              <span>{project.id}</span>

              <span className="text-muted-foreground font-medium">Title</span>
              <span className="font-medium">{project.title}</span>

              <span className="text-muted-foreground font-medium">Status</span>
              <span><ProjectStatusBadge project={project} /></span>

              <span className="text-muted-foreground font-medium">Tags</span>
              <span>
                {project.tags.length > 0 ? (
                  <ProjectTagsBadge project={project} />
                ) : (
                  <span className="text-muted-foreground italic">No tags</span>
                )}
              </span>
            </div>

            <Separator />

            <div className="space-y-1 text-sm">
              <p className="text-muted-foreground font-medium">Description</p>
              <p className="whitespace-pre-wrap">
                {project.description?.trim()
                  ? project.description
                  : <span className="text-muted-foreground italic">No description provided.</span>}
              </p>
            </div>

            {project.removalDate && (
              <>
                <Separator />
                <div className="grid grid-cols-[6rem_1fr] items-start gap-x-4 text-sm">
                  <span className="text-muted-foreground font-medium">Removal Date</span>
                  <span>{project.removalDate}</span>
                </div>
              </>
            )}
          </div>
        )}

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Close</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
