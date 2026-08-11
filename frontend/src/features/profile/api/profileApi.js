import { apiClient } from '../../../shared/api/apiClient'

export const getProfile = () => apiClient('/api/profile')

export function updateProfile(payload) {
  return apiClient('/api/profile', {
    method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload),
  })
}
