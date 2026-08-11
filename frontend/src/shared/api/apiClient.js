const ACCESS_TOKEN_KEY = 'buildlog.accessToken'

async function parseBody(response) {
  return response.json().catch(() => null)
}

async function refreshAccessToken() {
  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    credentials: 'include',
  })
  const body = await parseBody(response)
  if (!response.ok || !body?.data?.accessToken) {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY)
    window.dispatchEvent(new Event('buildlog:authentication-expired'))
    return null
  }
  sessionStorage.setItem(ACCESS_TOKEN_KEY, body.data.accessToken)
  return body.data.accessToken
}

export async function apiClient(url, options = {}) {
  const headers = new Headers(options.headers)
  const token = sessionStorage.getItem(ACCESS_TOKEN_KEY)
  if (token) headers.set('Authorization', `Bearer ${token}`)

  let response = await fetch(url, { ...options, headers, credentials: 'include' })

  const canRefresh = !url.startsWith('/api/auth/')
      && (response.status === 401 || response.status === 403)
  if (canRefresh) {
    const newAccessToken = await refreshAccessToken()
    if (newAccessToken) {
      headers.set('Authorization', `Bearer ${newAccessToken}`)
      response = await fetch(url, { ...options, headers, credentials: 'include' })
    }
  }

  const body = await parseBody(response)
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      throw new Error('관리자 로그인이 필요하거나 로그인 정보가 만료되었습니다.')
    }
    throw new Error(body?.message ?? body?.detail ?? `요청에 실패했습니다. (${response.status})`)
  }
  return body
}

export async function logout() {
  await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' })
  sessionStorage.removeItem(ACCESS_TOKEN_KEY)
  window.dispatchEvent(new Event('buildlog:authentication-expired'))
}
