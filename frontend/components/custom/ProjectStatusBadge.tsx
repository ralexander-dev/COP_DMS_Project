import { Project } from "@/lib/types";
import { Badge } from "@/components/ui/badge";

// component used for displaying a badge indicating whether a project is active, archived, or deleted.
export default function ProjectStatusBadge({ project }: { project: Project }) {
  if (project.deleted) {
    return (
      <Badge variant="destructive">
        Deleted
      </Badge>
    )
  } else if (project.archived) {
    return (
      <Badge variant="outline">
        Archived
      </Badge>
    )
  } else {
    return (
      <Badge>
        Active
      </Badge>
    )
  }
}