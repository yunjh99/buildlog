import { apiClient } from '../../../shared/api/apiClient'

export function getCareers() {
  return apiClient('/api/careers')
}

export function createCareer(payload) {
  return apiClient('/api/careers', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateCareer(id, payload) {
  return apiClient(`/api/careers/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function deleteCareer(id) {
  return apiClient(`/api/careers/${id}`, { method: 'DELETE' })
}
