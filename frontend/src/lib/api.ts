export type IncidentSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
export type IncidentStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED'

export type Incident = {
  id: string
  title: string
  description: string
  severity: IncidentSeverity
  status: IncidentStatus
  teamId: string
  createdAt: string
  updatedAt: string
}

export type Team = {
  id: string
  name: string
  serviceName: string
  contactEmail: string
  onCallEngineer: string
  capacity: number
  createdAt: string
  updatedAt: string
}

export type Dashboard = {
  total: number
  open: number
  inProgress: number
  resolved: number
  critical: number
}

export type CreateIncidentInput = {
  title: string
  description: string
  severity: IncidentSeverity
  teamId: string
}

const apiBaseUrl = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api').replace(/\/$/, '')

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
    ...init,
  })

  if (!response.ok) {
    const problem = await response.json().catch(() => null)
    throw new Error(problem?.detail ?? problem?.title ?? `Request failed with ${response.status}`)
  }

  return response.json() as Promise<T>
}

export const api = {
  getDashboard: () => request<Dashboard>('/incidents/dashboard'),
  getIncidents: () => request<Incident[]>('/incidents'),
  getTeams: () => request<Team[]>('/teams'),
  createIncident: (input: CreateIncidentInput) =>
    request<Incident>('/incidents', {
      method: 'POST',
      body: JSON.stringify(input),
    }),
  updateIncidentStatus: (id: string, status: IncidentStatus) =>
    request<Incident>(`/incidents/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),
}

export function teamNameFor(teams: Team[], teamId: string) {
  return teams.find((team) => team.id === teamId)?.name ?? 'Unknown team'
}

