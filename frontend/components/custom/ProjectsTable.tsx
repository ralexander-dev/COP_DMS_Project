"use client";
import { useState } from "react";
import { Project } from "@/lib/types";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  Table,
  TableHeader,
  TableBody,
  TableHead,
  TableRow,
  TableCell,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { PencilIcon, PlusIcon, UploadIcon } from "lucide-react";
import ProjectStatusBadge from "@/components/custom/ProjectStatusBadge";
import ProjectTagsBadge from "@/components/custom/ProjectTagsBadge";
import DeleteProjectDialog from "@/components/custom/DeleteProjectDialog";
import EditProjectDialog from "@/components/custom/EditProjectDialog";
import NewProjectDialog from "@/components/custom/NewProjectDialog";
import ImportProjectsDialog from "@/components/custom/ImportProjectsDialog";
import ProjectDetailsDialog from "@/components/custom/ProjectDetailsDialog";

const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

/*
  ProjectsTable component is the primary component used to display the 
  list of projects. Within it are the filtering features and dialogs
  for CRUD operations. 
*/
export default function ProjectsTable({ initialProjects }: { initialProjects: Project[] }) {
  const [projects, setProjects] = useState<Project[]>(initialProjects);
  const [deleteTarget, setDeleteTarget] = useState<Project | null>(null);
  const [editTarget, setEditTarget] = useState<Project | null>(null);
  const [viewTarget, setViewTarget] = useState<Project | null>(null);
  const [newOpen, setNewOpen] = useState(false);
  const [importOpen, setImportOpen] = useState(false);

  // handle updating a project 
  function updateProject(updated: Project) {
    setProjects((prev) => prev.map((p) => (p.id === updated.id ? updated : p)));
  }

  // handle deleting a project
  async function handleDelete(project: Project) {
    try {
      const res = await fetch(`${API}/api/projects/${project.id}`, { method: "DELETE" });
      if (!res.ok) throw new Error();
      const updated: Project = await res.json();
      updateProject(updated);
      toast.success("Project deleted.");
    } catch {
      toast.error("Failed to delete project.");
    }
  }

  const tabs = [
    { value: "all",      label: "All",      rows: projects },
    { value: "active",   label: "Active",   rows: projects.filter((p) => !p.archived && !p.deleted) },
    { value: "archived", label: "Archived", rows: projects.filter((p) => p.archived && !p.deleted) },
    { value: "deleted",  label: "Deleted",  rows: projects.filter((p) => p.deleted) },
  ];

  return (
    <>
      <Tabs defaultValue="all" className="w-full">
        <div className="flex items-center justify-between">
          <TabsList>
            {tabs.map((t) => (
              <TabsTrigger key={t.value} value={t.value}>{t.label}</TabsTrigger>
            ))}
          </TabsList>
          <div className="flex gap-2">
            <Button variant="outline" size="sm" onClick={() => setImportOpen(true)}>
              <UploadIcon />
              Import Projects
            </Button>
            <Button size="sm" onClick={() => setNewOpen(true)}>
              <PlusIcon />
              New Project
            </Button>
          </div>
        </div>

        {tabs.map((t) => (
          <TabsContent key={t.value} value={t.value} className="mt-4">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Title</TableHead>
                  <TableHead>Tags</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="w-10" />
                </TableRow>
              </TableHeader>
              <TableBody>
                {t.rows.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} className="py-8 text-center text-muted-foreground">
                      No projects found.
                    </TableCell>
                  </TableRow>
                ) : (
                  t.rows.map((project) => (
                    <TableRow key={project.id} className="cursor-pointer" onClick={() => setViewTarget(project)}>
                      <TableCell>{project.id}</TableCell>
                      <TableCell className="font-medium">{project.title}</TableCell>
                      <TableCell><ProjectTagsBadge project={project} /></TableCell>
                      <TableCell><ProjectStatusBadge project={project} /></TableCell>
                      <TableCell>
                        <Button
                          variant="ghost"
                          size="icon-sm"
                          aria-label="Edit project"
                          onClick={(e) => { e.stopPropagation(); setEditTarget(project); }}
                        >
                          <PencilIcon />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TabsContent>
        ))}
      </Tabs>

      <ProjectDetailsDialog
        project={viewTarget}
        onClose={() => setViewTarget(null)}
      />

      <ImportProjectsDialog
        open={importOpen}
        onClose={() => setImportOpen(false)}
        onImportComplete={(projects) => setProjects(projects)}
      />

      <NewProjectDialog
        open={newOpen}
        onClose={() => setNewOpen(false)}
        onProjectCreated={(project) => setProjects((prev) => [...prev, project])}
      />

      <EditProjectDialog
        project={editTarget}
        onClose={() => setEditTarget(null)}
        onProjectUpdate={updateProject}
        onDeleteRequest={(project) => {
          setEditTarget(null);
          setDeleteTarget(project);
        }}
      />

      <DeleteProjectDialog
        project={deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={async () => {
          if (deleteTarget) {
            await handleDelete(deleteTarget);
            setDeleteTarget(null);
          }
        }}
      />
    </>
  );
}