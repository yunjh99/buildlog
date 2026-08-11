import { apiClient } from '../../../shared/api/apiClient'

export function getTechStacks() {
  return apiClient('/api/tech-stacks')
}

export function createTechStack(payload) {
  return apiClient('/api/tech-stacks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateTechStack(id, payload) {
  return apiClient(`/api/tech-stacks/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function deleteTechStack(id) {
  return apiClient(`/api/tech-stacks/${id}`, { method: 'DELETE' })
}
