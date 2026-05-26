import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { OpsBoardApp } from './OpsBoardApp'
import type { Dashboard, Incident, Team } from '../lib/api'

const fixtures = vi.hoisted(() => {
  const teams: Team[] = [
    {
      id: 'team-platform',
      name: 'Platform Team',
      serviceName: 'gateway',
      contactEmail: 'platform@example.com',
      onCallEngineer: 'Nadia Martin',
      capacity: 8,
      createdAt: '2026-05-26T10:00:00Z',
      updatedAt: '2026-05-26T10:00:00Z',
    },
  ]

  const incidents: Incident[] = [
    {
      id: 'incident-api',
      title: 'API latency above SLO',
      description: 'Gateway p95 latency is above the expected SLO.',
      severity: 'HIGH',
      status: 'OPEN',
      teamId: 'team-platform',
      createdAt: '2026-05-26T10:00:00Z',
      updatedAt: '2026-05-26T10:00:00Z',
    },
  ]

  const dashboard: Dashboard = {
    total: 1,
    open: 1,
    inProgress: 0,
    resolved: 0,
    critical: 0,
  }

  return {
    teams,
    incidents,
    dashboard,
    api: {
      getDashboard: vi.fn(),
      getIncidents: vi.fn(),
      getTeams: vi.fn(),
      createIncident: vi.fn(),
      updateIncidentStatus: vi.fn(),
    },
  }
})

vi.mock('../lib/api', () => ({
  api: fixtures.api,
  teamNameFor: (teams: Team[], teamId: string) => teams.find((team) => team.id === teamId)?.name ?? 'Unknown team',
}))

describe('OpsBoardApp', () => {
  beforeEach(() => {
    fixtures.api.getDashboard.mockResolvedValue(fixtures.dashboard)
    fixtures.api.getIncidents.mockResolvedValue(fixtures.incidents)
    fixtures.api.getTeams.mockResolvedValue(fixtures.teams)
    fixtures.api.createIncident.mockResolvedValue({
      ...fixtures.incidents[0],
      id: 'incident-created',
      title: 'New outage',
      description: 'Fresh incident',
    })
    fixtures.api.updateIncidentStatus.mockResolvedValue({
      ...fixtures.incidents[0],
      status: 'IN_PROGRESS',
    })
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('renders incident, team and dashboard data from the API', async () => {
    render(<OpsBoardApp />)

    expect(await screen.findByText('API latency above SLO')).toBeInTheDocument()
    expect(screen.getAllByText('Platform Team').length).toBeGreaterThan(1)
    expect(screen.getByText('Nadia Martin')).toBeInTheDocument()
    expect(screen.getByText('Total incidents')).toBeInTheDocument()
    expect(fixtures.api.getDashboard).toHaveBeenCalled()
    expect(fixtures.api.getIncidents).toHaveBeenCalled()
    expect(fixtures.api.getTeams).toHaveBeenCalled()
  })

  it('creates an incident from the composer form', async () => {
    render(<OpsBoardApp />)

    await screen.findByText('API latency above SLO')
    fireEvent.change(screen.getByLabelText('Title'), { target: { value: 'New outage' } })
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'Fresh incident' } })
    fireEvent.click(screen.getByRole('button', { name: 'Create incident' }))

    await waitFor(() => {
      expect(fixtures.api.createIncident).toHaveBeenCalledWith({
        title: 'New outage',
        description: 'Fresh incident',
        severity: 'HIGH',
        teamId: 'team-platform',
      })
    })
  })

  it('starts an open incident from the row action', async () => {
    render(<OpsBoardApp />)

    await screen.findByText('API latency above SLO')
    fireEvent.click(screen.getByRole('button', { name: 'Start incident' }))

    await waitFor(() => {
      expect(fixtures.api.updateIncidentStatus).toHaveBeenCalledWith('incident-api', 'IN_PROGRESS')
    })
  })
})
