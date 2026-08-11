import { useEffect, useState } from 'react'
import { updateProfile } from '../api/profileApi'
import './ProfileEditor.css'

export default function ProfileEditor({ profile, onSaved }) {
  const toForm = value => ({
    ...value,
    aboutContent: [value.aboutParagraph1, value.aboutParagraph2].filter(Boolean).join('\n\n'),
  })
  const [form, setForm] = useState(() => toForm(profile))
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  useEffect(() => setForm(toForm(profile)), [profile])
  const change = event => setForm(current => ({ ...current, [event.target.name]: event.target.value }))
  const submit = async event => {
    event.preventDefault(); setSaving(true); setMessage('')
    try {
      const { aboutContent, ...fields } = form
      const response = await updateProfile({ ...fields, aboutParagraph1: aboutContent, aboutParagraph2: '' })
      onSaved(response.data); setMessage('소개 내용을 저장했습니다.')
    }
    catch (error) { setMessage(error.message) }
    finally { setSaving(false) }
  }
  return <section className="content-section profile-editor">
    <span className="section-number">// PROFILE CMS</span>
    <div className="section-grid manage-heading"><h2>소개 내용을<br/><em>수정하세요.</em></h2><p>히어로 문구와 연락처, ABOUT 내용을 관리합니다.</p></div>
    <form className="create-form" onSubmit={submit}>
      <label className="wide">히어로 첫 문장 /<input name="heroLine1" value={form.heroLine1} onChange={change} required maxLength="500" /></label>
      <label className="wide">히어로 둘째 문장 /<input name="heroLine2" value={form.heroLine2} onChange={change} required maxLength="500" /></label>
      <label>이메일 /<input name="email" value={form.email ?? ''} onChange={change} maxLength="200" placeholder="name@example.com" /></label>
      <label>GitHub /<input name="githubUrl" value={form.githubUrl ?? ''} onChange={change} maxLength="500" placeholder="https://github.com/..." /></label>
      <label className="wide">블로그 /<input name="blogUrl" value={form.blogUrl ?? ''} onChange={change} maxLength="500" placeholder="https://..." /></label>
      <label>ABOUT 제목 /<input name="aboutTitle" value={form.aboutTitle} onChange={change} required maxLength="100" /></label>
      <label>ABOUT 강조 제목 /<input name="aboutEmphasis" value={form.aboutEmphasis} onChange={change} required maxLength="100" /></label>
      <label className="wide">ABOUT 내용 /<textarea name="aboutContent" value={form.aboutContent} onChange={change} required maxLength="2000" rows="12" placeholder="입력한 줄바꿈과 빈 줄이 화면에 그대로 표시됩니다." /></label>
      {message && <div className="notice wide" role="status">{message}</div>}
      <button className="submit wide" disabled={saving}>{saving ? '저장 중...' : '소개 저장 →'}</button>
    </form>
  </section>
}
