/*
  page.tsx
  Main dashboard page for the Next.js frontend application.
  This page fetches Projects from the REST API and displays them in an interactive dashboard / table. 
*/
import { Project } from "@/lib/types"; // import Project type definition
import ProjectsTable from "@/components/custom/ProjectsTable"; // import custom ProjectsTable component
import { DbStatus } from "@/lib/types"; // import DbStatus type definition
import DbSetupPage from "@/app/DbSetupPage"; // import DatabaseSetupPage component

// API_URL is used server-side (container-to-container via Docker service name).
// Falls back to NEXT_PUBLIC_API_URL for local development (i.e., when using 'npm run dev' directly)
const API = process.env.API_URL ?? process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

// Function to fetch projects from the REST API
async function getProjects(): Promise<Project[]> {
  const res = await fetch(
    `${API}/api/projects`, 
    { cache: "no-store" } // disable cache to ensure fresh data on each request.
  );
  // if response not OK, try to parse error message from response body, otherwise throw generic error with status code
  if (!res.ok) return res.json().then(data => { throw new Error(data?.message ?? `Server error (${res.status}).`) }); 

  // if response is OK, return the JSON data as an array of Projects
  return res.json();
}

async function checkDbConnection(): Promise<boolean> {
  try {
    const res = await fetch(`${API}/api/db/status`, { cache: "no-store" });
    if (!res.ok) return false; // if response not OK, assume not connected
    const data: DbStatus = await res.json();
    return data.connected; // return the 'connected' status from the response
  } catch {
    return false; // if fetch fails (e.g., server not reachable), assume not connected
  }
}

export default async function DashboardPage() {
  const dbConnected = await checkDbConnection(); // check database connection before rendering the page

  // if not connected, render the DbSetupPage
  if (!dbConnected) {
    return <DbSetupPage />;
  }

  const projects = await getProjects(); // fetch projects only when connected

  return (
    <div className="mx-auto w-full max-w-5xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-xl font-semibold">Projects</h1>
      </div>
      <ProjectsTable initialProjects={projects} />
    </div>
  );
}
