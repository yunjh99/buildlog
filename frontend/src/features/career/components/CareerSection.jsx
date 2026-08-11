import { useCallback, useEffect, useState } from 'react'
import { createCareer, deleteCareer, getCareers, updateCareer } from '../api/careerApi'
import { formatMonth } from '../../../shared/utils/date'
import EntryModal from '../../../shared/components/EntryModal'
import './CareerSection.css'

const emptyCareer = () => ({
  companyName: '', startDate: '', endDate: '',
  roles: [{ title: '', activities: [{ content: '' }] }],
})

export default function CareerSection({ isAdmin = false }) {
  const [careers, setCareers] = useState([])
  const [form, setForm] = useState(emptyCareer)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [modalOpen, setModalOpen] = useState(false)
  const [message, setMessage] = useState('')

  const loadCareers = async () => {
    setLoading(true)
    try { setCareers((await getCareers()).data ?? []) }
    catch (error) { setMessage(error.message) }
    finally { setLoading(false) }
  }
  useEffect(() => { loadCareers() }, [])

  const closeModal = useCallback(() => {
    setModalOpen(false); setEditingId(null); setForm(emptyCareer()); setMessage('')
  }, [])
  const addCareer = () => {
    setEditingId(null); setForm(emptyCareer()); setMessage(''); setModalOpen(true)
  }
  const editCareer = career => {
    setEditingId(career.id)
    setForm({
      companyName: career.companyName,
      startDate: career.startDate,
      endDate: career.endDate ?? '',
      roles: career.roles.map(role => ({
        title: role.title,
        activities: role.activities.map(activity => ({ content: activity.content })),
      })),
    })
    setMessage(''); setModalOpen(true)
  }

  const changeField = event => setForm(current => ({ ...current, [event.target.name]: event.target.value }))
  const changeRole = (roleIndex, title) => setForm(current => ({ ...current, roles: current.roles.map((role, index) => index === roleIndex ? { ...role, title } : role) }))
  const changeActivity = (roleIndex, activityIndex, content) => setForm(current => ({ ...current, roles: current.roles.map((role, index) => index === roleIndex ? { ...role, activities: role.activities.map((activity, i) => i === activityIndex ? { content } : activity) } : role) }))
  const addRole = () => setForm(current => ({ ...current, roles: [...current.roles, { title: '', activities: [{ content: '' }] }] }))
  const removeRole = roleIndex => setForm(current => ({ ...current, roles: current.roles.filter((_, index) => index !== roleIndex) }))
  const addActivity = roleIndex => setForm(current => ({ ...current, roles: current.roles.map((role, index) => index === roleIndex ? { ...role, activities: [...role.activities, { content: '' }] } : role) }))
  const removeActivity = (roleIndex, activityIndex) => setForm(current => ({ ...current, roles: current.roles.map((role, index) => index === roleIndex ? { ...role, activities: role.activities.filter((_, i) => i !== activityIndex) } : role) }))

  const submit = async event => {
    event.preventDefault(); setSubmitting(true); setMessage('')
    try {
      const payload = { ...form, endDate: form.endDate || null }
      if (editingId) await updateCareer(editingId, payload)
      else await createCareer(payload)
      await loadCareers(); closeModal()
    } catch (error) { setMessage(error.message) }
    finally { setSubmitting(false) }
  }

  const removeCareer = async career => {
    if (!window.confirm(`'${career.companyName}' 이력을 삭제할까요?`)) return
    try { await deleteCareer(career.id); await loadCareers() }
    catch (error) { setMessage(error.message) }
  }

  return <section className="content-section career-display" id="career">
    <span className="section-number">// EXPERIENCE</span>
    <div className="section-title"><h2>이력</h2><div className="section-title-actions"><small>{String(careers.length).padStart(2, '0')} COMPANIES</small>{isAdmin && <button className="add-entry" onClick={addCareer}>+ 이력 추가</button>}</div></div>
    {message && !modalOpen && <div className="notice" role="status">{message}</div>}
    <div className="career-list">
      {careers.map(career => <article className="career-card" key={career.id}>
        <div className="career-company"><h3>{career.companyName}</h3><time>{formatMonth(career.startDate)} ~ {career.endDate ? formatMonth(career.endDate) : '재직 중'}</time>{isAdmin && <div className="career-actions"><button type="button" onClick={() => editCareer(career)}>수정</button><button type="button" onClick={() => removeCareer(career)}>삭제</button></div>}</div>
        <div className="career-roles">{career.roles.map(role => <div className="career-role" key={role.id}><h4>{role.title}</h4><ul>{role.activities.map(activity => <li key={activity.id}>{activity.content}</li>)}</ul></div>)}</div>
      </article>)}
      {loading && <div className="empty">이력을 불러오는 중...</div>}
      {!loading && careers.length === 0 && <div className="empty">등록된 이력이 없습니다.</div>}
    </div>

    {modalOpen && <EntryModal eyebrow={editingId ? 'CAREER EDIT' : 'CAREER ADD'} title={editingId ? '이력 수정' : '이력 추가'} titleId="career-modal-title" onClose={closeModal}>
      <p className="modal-description">회사별 재직 기간과 역할, 역할마다 수행한 활동을 기록합니다.</p>
      {message && <div className="notice" role="status">{message}</div>}
      <form className="create-form career-form" onSubmit={submit}>
        <label className="wide">회사명 /<input name="companyName" value={form.companyName} onChange={changeField} required maxLength="100" placeholder="Company name" /></label>
        <label>입사일 /<input type="date" name="startDate" value={form.startDate} onChange={changeField} required /></label>
        <label>퇴사일 /<input type="date" name="endDate" value={form.endDate} min={form.startDate} onChange={changeField} /></label>
        <div className="career-role-editor wide"><div className="career-editor-heading"><span>역할 및 활동 /</span><button type="button" onClick={addRole}>+ 역할 추가</button></div>
          {form.roles.map((role, roleIndex) => <fieldset key={roleIndex}><div className="career-role-heading"><input value={role.title} onChange={event => changeRole(roleIndex, event.target.value)} required maxLength="100" placeholder={`역할 ${roleIndex + 1}`} aria-label={`역할 ${roleIndex + 1}`} />{form.roles.length > 1 && <button type="button" className="career-remove" onClick={() => removeRole(roleIndex)}>역할 삭제</button>}</div><div className="career-activity-editor">{role.activities.map((activity, activityIndex) => <div key={activityIndex}><textarea value={activity.content} onChange={event => changeActivity(roleIndex, activityIndex, event.target.value)} required maxLength="1000" rows="2" placeholder="담당 업무와 성과를 입력하세요." aria-label={`역할 ${roleIndex + 1} 활동 ${activityIndex + 1}`} />{role.activities.length > 1 && <button type="button" className="career-remove" onClick={() => removeActivity(roleIndex, activityIndex)}>×</button>}</div>)}<button type="button" className="career-add-activity" onClick={() => addActivity(roleIndex)}>+ 활동 추가</button></div></fieldset>)}
        </div>
        <button type="button" className="career-cancel wide" onClick={closeModal}>취소</button>
        <button className="submit wide" disabled={submitting}>{submitting ? '저장 중...' : editingId ? '이력 수정 →' : '이력 저장 →'}</button>
      </form>
    </EntryModal>}
  </section>
}
