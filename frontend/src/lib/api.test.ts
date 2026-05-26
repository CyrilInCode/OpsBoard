import { afterEach, describe, expect, it, vi } from 'vitest'
import { api, teamNameFor } from './api'

describe('api client', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('loads dashboard data from the gateway API', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ total: 3, open: 2, inProgress: 1, resolved: 0, critical: 1 }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const dashboard = await api.getDashboard()

    expect(fetchMock).toHaveBeenCalledWith(
      'http://localhost:8080/api/incidents/dashboard',
      expect.objectContaining({ headers: expect.objectContaining({ 'Content-Type': 'application/json' }) }),
    )
    expect(dashboard.critical).toBe(1)
  })

  it('sends status updates as PATCH requests', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ id: 'incident-1', status: 'RESOLVED' }),
    })
    vi.stubGlobal('fetch', fetchMock)

    await api.updateIncidentStatus('incident-1', 'RESOLVED')

    expect(fetchMock).toHaveBeenCalledWith(
      'http://localhost:8080/api/incidents/incident-1/status',
      expect.objectContaining({
        method: 'PATCH',
        body: JSON.stringify({ status: 'RESOLVED' }),
      }),
    )
  })

  it('returns a readable fallback for unknown teams', () => {
    expect(teamNameFor([], 'missing')).toBe('Unknown team')
  })
})

