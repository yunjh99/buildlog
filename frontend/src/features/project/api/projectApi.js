import { apiClient } from '../../../shared/api/apiClient'

export function getProjects(page = 0, size = 4) {
  return apiClient(`/api/projects?page=${page}&size=${size}`)
}

export function createProject(payload) {
  return apiClient('/api/projects', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateProject(id, payload) {
  return apiClient(`/api/projects/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function deleteProject(id) {
  return apiClient(`/api/projects/${id}`, { method: 'DELETE' })
}
