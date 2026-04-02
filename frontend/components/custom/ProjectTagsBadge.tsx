import { Project } from "@/lib/types";
import { Badge } from "@/components/ui/badge";

// component used for displaying a badge with the project's tags 
export default function ProjectTagsBadge({ project }: { project: Project }) {
  return (
    <Badge variant="outline">
      {project.tags && project.tags.length > 0 ? project.tags.join(", ") : "No Tags"}
    </Badge>
  )
}