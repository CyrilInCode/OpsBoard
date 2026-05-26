import type { FormEvent, ReactNode } from 'react'
import { useEffect, useMemo, useState } from 'react'
import {
  Activity,
  AlertTriangle,
  CheckCircle2,
  CircleDot,
  Plus,
  RefreshCw,
  Send,
  ShieldCheck,
  Users,
} from 'lucide-react'
import type { CreateIncidentInput, Dashboard, Incident, IncidentSeverity, IncidentStatus, Team } from '../lib/api'
import { api, teamNameFor } from '../lib/api'

type LoadState = 'idle' | 'loading' | 'ready' | 'error'

const emptyDashboard: Dashboard = {
  total: 0,
  open: 0,
  inProgress: 0,
  resolved: 0,
  critical: 0,
}

export function OpsBoardApp() {
  const [incidents, setIncidents] = useState<Incident[]>([])
  const [teams, setTeams] = useState<Team[]>([])
  const [dashboard, setDashboard] = useState<Dashboard>(emptyDashboard)
  const [loadState, setLoadState] = useState<LoadState>('idle')
  const [error, setError] = useState<string | null>(null)

  async function loadData() {
    setLoadState('loading')
    setError(null)

    try {
      const [nextDashboard, nextIncidents, nextTeams] = await Promise.all([
        api.getDashboard(),
        api.getIncidents(),
        api.getTeams(),
      ])
      setDashboard(nextDashboard)
      setIncidents(nextIncidents)
      setTeams(nextTeams)
      setLoadState('ready')
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Unable to load OpsBoard data')
      setLoadState('error')
    }
  }

  useEffect(() => {
    void loadData()
  }, [])

  async function createIncident(input: CreateIncidentInput) {
    const created = await api.createIncident(input)
    setIncidents((current) => [created, ...current])
    await loadData()
  }

  async function updateStatus(id: string, status: IncidentStatus) {
    const updated = await api.updateIncidentStatus(id, status)
    setIncidents((current) => current.map((incident) => (incident.id === id ? updated : incident)))
    await loadData()
  }

  const activeIncidents = useMemo(
    () => incidents.filter((incident) => incident.status !== 'RESOLVED'),
    [incidents],
  )

  return (
    <main className="app-shell">
      <section className="hero-band">
        <div>
          <p className="eyebrow">OpsBoard</p>
          <h1>Incident command center</h1>
          <p className="hero-copy">Gateway, microservices, teams and PostgreSQL in one DevOps-ready workspace.</p>
        </div>
        <button className="icon-button primary-action" type="button" onClick={loadData} disabled={loadState === 'loading'}>
          <RefreshCw size={18} aria-hidden="true" />
          <span>{loadState === 'loading' ? 'Refreshing' : 'Refresh'}</span>
        </button>
      </section>

      {error ? <div className="alert-banner">{error}</div> : null}

      <section className="stats-grid" aria-label="Incident statistics">
        <StatCard icon={<Activity size={20} />} label="Total incidents" value={dashboard.total} tone="blue" />
        <StatCard icon={<CircleDot size={20} />} label="Open" value={dashboard.open} tone="orange" />
        <StatCard icon={<ShieldCheck size={20} />} label="In progress" value={dashboard.inProgress} tone="teal" />
        <StatCard icon={<AlertTriangle size={20} />} label="Critical" value={dashboard.critical} tone="red" />
      </section>

      <section className="work-grid">
        <IncidentComposer teams={teams} onCreate={createIncident} />
        <TeamPanel teams={teams} activeIncidentCount={activeIncidents.length} />
      </section>

      <section className="table-section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Live queue</p>
            <h2>Incidents</h2>
          </div>
          <span className="count-pill">{incidents.length}</span>
        </div>

        <div className="incident-list">
          {incidents.map((incident) => (
            <IncidentRow
              key={incident.id}
              incident={incident}
              teamName={teamNameFor(teams, incident.teamId)}
              onResolve={() => updateStatus(incident.id, 'RESOLVED')}
              onStart={() => updateStatus(incident.id, 'IN_PROGRESS')}
            />
          ))}
          {incidents.length === 0 && loadState !== 'loading' ? <p className="empty-state">No incidents</p> : null}
        </div>
      </section>
    </main>
  )
}

function StatCard({
  icon,
  label,
  value,
  tone,
}: Readonly<{ icon: ReactNode; label: string; value: number; tone: 'blue' | 'orange' | 'teal' | 'red' }>) {
  return (
    <article className={`stat-card ${tone}`}>
      <div className="stat-icon">{icon}</div>
      <div>
        <p>{label}</p>
        <strong>{value}</strong>
      </div>
    </article>
  )
}

function IncidentComposer({
  teams,
  onCreate,
}: Readonly<{ teams: Team[]; onCreate: (input: CreateIncidentInput) => Promise<void> }>) {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [severity, setSeverity] = useState<IncidentSeverity>('HIGH')
  const [teamId, setTeamId] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!teamId && teams[0]) {
      setTeamId(teams[0].id)
    }
  }, [teamId, teams])

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!teamId) {
      return
    }

    setSaving(true)
    try {
      await onCreate({ title, description, severity, teamId })
      setTitle('')
      setDescription('')
      setSeverity('HIGH')
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="composer" onSubmit={handleSubmit}>
      <div className="section-heading compact">
        <div>
          <p className="eyebrow">Intake</p>
          <h2>New incident</h2>
        </div>
        <Plus size={20} aria-hidden="true" />
      </div>

      <label>
        Title
        <input value={title} onChange={(event) => setTitle(event.target.value)} maxLength={120} required />
      </label>

      <label>
        Description
        <textarea value={description} onChange={(event) => setDescription(event.target.value)} maxLength={2000} required />
      </label>

      <div className="form-row">
        <label>
          Severity
          <select value={severity} onChange={(event) => setSeverity(event.target.value as IncidentSeverity)}>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="CRITICAL">Critical</option>
          </select>
        </label>

        <label>
          Team
          <select value={teamId} onChange={(event) => setTeamId(event.target.value)} required>
            {teams.map((team) => (
              <option key={team.id} value={team.id}>
                {team.name}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button className="icon-button primary-action" type="submit" disabled={saving || !teamId}>
        <Send size={18} aria-hidden="true" />
        <span>{saving ? 'Creating' : 'Create incident'}</span>
      </button>
    </form>
  )
}

function TeamPanel({ teams, activeIncidentCount }: Readonly<{ teams: Team[]; activeIncidentCount: number }>) {
  return (
    <aside className="team-panel">
      <div className="section-heading compact">
        <div>
          <p className="eyebrow">Ownership</p>
          <h2>Teams</h2>
        </div>
        <Users size={20} aria-hidden="true" />
      </div>

      <div className="operations-score">
        <strong>{activeIncidentCount}</strong>
        <span>active incidents</span>
      </div>

      <div className="team-list">
        {teams.map((team) => (
          <article className="team-card" key={team.id}>
            <div>
              <h3>{team.name}</h3>
              <p>{team.serviceName}</p>
            </div>
            <span>{team.onCallEngineer}</span>
          </article>
        ))}
      </div>
    </aside>
  )
}

function IncidentRow({
  incident,
  teamName,
  onResolve,
  onStart,
}: Readonly<{ incident: Incident; teamName: string; onResolve: () => void; onStart: () => void }>) {
  return (
    <article className="incident-row">
      <div className="incident-main">
        <span className={`severity ${incident.severity.toLowerCase()}`}>{incident.severity}</span>
        <div>
          <h3>{incident.title}</h3>
          <p>{incident.description}</p>
        </div>
      </div>
      <div className="incident-meta">
        <span>{teamName}</span>
        <StatusBadge status={incident.status} />
      </div>
      <div className="row-actions">
        {incident.status === 'OPEN' ? (
          <button className="icon-only" type="button" onClick={onStart} aria-label="Start incident">
            <Activity size={18} aria-hidden="true" />
          </button>
        ) : null}
        {incident.status !== 'RESOLVED' ? (
          <button className="icon-only success" type="button" onClick={onResolve} aria-label="Resolve incident">
            <CheckCircle2 size={18} aria-hidden="true" />
          </button>
        ) : null}
      </div>
    </article>
  )
}

function StatusBadge({ status }: Readonly<{ status: IncidentStatus }>) {
  return <span className={`status-badge ${status.toLowerCase().replace('_', '-')}`}>{status.replace('_', ' ')}</span>
}
