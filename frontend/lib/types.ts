export interface Project {
  id: number;
  title: string;
  description: string;
  tags: string[];
  archived: boolean;
  deleted: boolean;
  removalDate: string | null;
}

export interface SkippedEntry {
  entry: string;
  reason: string;
}

export interface ImportResult {
  importedCount: number;
  skippedEntries: SkippedEntry[];
}

export interface DbConnectRequest {
  username: string;
  password: string;
  host?: string;
  port?: number;
}

export interface DbStatus {
  connected: boolean;
}

export interface AddProjectRequest { title: string; }
export interface UpdateTitleRequest { title: string; }
export interface UpdateDescriptionRequest { description: string; }
export interface UpdateTagsRequest { tags: string[]; }