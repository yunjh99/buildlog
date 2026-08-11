import { useEffect, useState } from 'react'
import PortfolioPage from '../pages/portfolio/PortfolioPage'
import { apiClient } from '../shared/api/apiClient'

export default function App() {
  const pathname = window.location.pathname
  const adminPage = pathname === '/admin' || pathname.startsWith('/admin/')
  const [authenticated, setAuthenticated] = useState(() => Boolean(sessionStorage.getItem('buildlog.accessToken')))
  const [loginId, setLoginId] = useState('admin')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    const expireAuthentication = () => setAuthenticated(false)
    window.addEventListener('buildlog:authentication-expired', expireAuthentication)
    return () => window.removeEventListener('buildlog:authentication-expired', expireAuthentication)
  }, [])

  const login = async event => {
    event.preventDefault(); setSubmitting(true); setMessage('')
    try {
      const response = await apiClient('/api/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ loginId, password }) })
      sessionStorage.setItem('buildlog.accessToken', response.data.accessToken)
      setAuthenticated(true)
    } catch (error) { setMessage(error.message) } finally { setSubmitting(false) }
  }

  if (adminPage && !authenticated) return <main className="admin-login-shell"><form className="admin-login" onSubmit={login}>
    <span>// ADMIN ACCESS</span><h1>관리자 로그인</h1><p>기록을 추가하거나 수정하려면 로그인하세요.</p>
    <label>아이디<input value={loginId} onChange={event => setLoginId(event.target.value)} autoComplete="username" required /></label>
    <label>비밀번호<input type="password" value={password} onChange={event => setPassword(event.target.value)} autoComplete="current-password" required autoFocus /></label>
    {message && <div className="login-notice" role="alert">{message}</div>}
    <button disabled={submitting}>{submitting ? '로그인 중...' : '로그인 →'}</button>
  </form></main>

  return <PortfolioPage isAdmin={adminPage && authenticated} />
}
