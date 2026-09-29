import { useCallback, useEffect, useState } from 'react'
import { createCareer, deleteCareer, getCareers, updateCareer } from '../api/careerApi'
import { formatMonth } from '../../../shared/utils/date'
import EntryModal from '../../../shared/components/EntryModal'
import './CareerSection.css'

const emptySection = () => ({ title: '', activities: [{ content: '' }] })
const emptyPosition = (startDate = '', endDate = '') => ({ title: '', startDate, endDate, sections: [emptySection()] })
const emptyCareer = () => ({ companyName: '', startDate: '', endDate: '', positions: [emptyPosition()] })

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
      positions: career.positions.map(position => ({
        title: position.title ?? '',
        startDate: position.startDate,
        endDate: position.endDate ?? '',
        sections: position.sections.map(section => ({
          title: section.title,
          activities: section.activities.map(activity => ({ content: activity.content })),
        })),
      })),
    })
    setMessage(''); setModalOpen(true)
  }

  const changeField = event => setForm(current => ({ ...current, [event.target.name]: event.target.value }))
  const changePosition = (positionIndex, field, value) => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? { ...position, [field]: value } : position),
  }))
  const changeSection = (positionIndex, sectionIndex, title) => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? {
      ...position, sections: position.sections.map((section, i) => i === sectionIndex ? { ...section, title } : section),
    } : position),
  }))
  const changeActivity = (positionIndex, sectionIndex, activityIndex, content) => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? {
      ...position, sections: position.sections.map((section, i) => i === sectionIndex ? {
        ...section, activities: section.activities.map((activity, j) => j === activityIndex ? { content } : activity),
      } : section),
    } : position),
  }))
  const addPosition = () => setForm(current => ({ ...current, positions: [...current.positions, emptyPosition(current.startDate, current.endDate)] }))
  const removePosition = positionIndex => setForm(current => ({ ...current, positions: current.positions.filter((_, index) => index !== positionIndex) }))
  const addSection = positionIndex => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? { ...position, sections: [...position.sections, emptySection()] } : position),
  }))
  const removeSection = (positionIndex, sectionIndex) => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? { ...position, sections: position.sections.filter((_, i) => i !== sectionIndex) } : position),
  }))
  const addActivity = (positionIndex, sectionIndex) => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? {
      ...position, sections: position.sections.map((section, i) => i === sectionIndex ? { ...section, activities: [...section.activities, { content: '' }] } : section),
    } : position),
  }))
  const removeActivity = (positionIndex, sectionIndex, activityIndex) => setForm(current => ({
    ...current, positions: current.positions.map((position, index) => index === positionIndex ? {
      ...position, sections: position.sections.map((section, i) => i === sectionIndex ? { ...section, activities: section.activities.filter((_, j) => j !== activityIndex) } : section),
    } : position),
  }))

  const submit = async event => {
    event.preventDefault(); setSubmitting(true); setMessage('')
    try {
      const payload = {
        ...form, endDate: form.endDate || null,
        positions: form.positions.map(position => ({ ...position, title: position.title.trim() || null, endDate: position.endDate || null })),
      }
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
        <div className="career-positions">{career.positions.map(position => <div className="career-position" key={position.id}>
          {position.title && <h4>{position.title}</h4>}
          {career.positions.length > 1 && <time>{formatMonth(position.startDate)} ~ {position.endDate ? formatMonth(position.endDate) : '현재'}</time>}
          <div className="career-sections">{position.sections.map(section => <div className="career-section" key={section.id}>
            <h5>{section.title}</h5><ul>{section.activities.map(activity => <li key={activity.id}>{activity.content}</li>)}</ul>
          </div>)}</div>
        </div>)}</div>
      </article>)}
      {loading && <div className="empty">이력을 불러오는 중...</div>}
      {!loading && careers.length === 0 && <div className="empty">등록된 이력이 없습니다.</div>}
    </div>

    {modalOpen && <EntryModal eyebrow={editingId ? 'CAREER EDIT' : 'CAREER ADD'} title={editingId ? '이력 수정' : '이력 추가'} titleId="career-modal-title" onClose={closeModal}>
      <p className="modal-description">회사와 직무 기간, 직무별 섹션 및 활동을 기록합니다. 기존 이력의 빈 직무명은 필요할 때 입력할 수 있습니다.</p>
      {message && <div className="notice" role="status">{message}</div>}
      <form className="create-form career-form" onSubmit={submit}>
        <label className="wide">회사명 /<input name="companyName" value={form.companyName} onChange={changeField} required maxLength="100" placeholder="Company name" /></label>
        <label>입사일 /<input type="date" name="startDate" value={form.startDate} onChange={changeField} required /></label>
        <label>퇴사일 /<input type="date" name="endDate" value={form.endDate} min={form.startDate} onChange={changeField} /></label>
        <div className="career-position-editor wide"><div className="career-editor-heading"><span>직무 이력 /</span><button type="button" onClick={addPosition}>+ 직무 추가</button></div>
          {form.positions.map((position, positionIndex) => <fieldset key={positionIndex} className="career-position-fields">
            <div className="career-position-heading"><strong>직무 {positionIndex + 1}</strong>{form.positions.length > 1 && <button type="button" className="career-remove" onClick={() => removePosition(positionIndex)}>직무 삭제</button>}</div>
            <label>직무명 (선택) /<input value={position.title} onChange={event => changePosition(positionIndex, 'title', event.target.value)} maxLength="100" placeholder="예: 백엔드 개발" /></label>
            <div className="career-position-dates"><label>직무 시작일 /<input type="date" value={position.startDate} onChange={event => changePosition(positionIndex, 'startDate', event.target.value)} min={form.startDate} max={form.endDate || undefined} required /></label><label>직무 종료일 /<input type="date" value={position.endDate} onChange={event => changePosition(positionIndex, 'endDate', event.target.value)} min={position.startDate} max={form.endDate || undefined} required={Boolean(form.endDate)} /></label></div>
            <div className="career-section-editor"><div className="career-editor-heading"><span>섹션 /</span><button type="button" onClick={() => addSection(positionIndex)}>+ 섹션 추가</button></div>
              {position.sections.map((section, sectionIndex) => <div className="career-section-fields" key={sectionIndex}><div className="career-section-heading"><input value={section.title} onChange={event => changeSection(positionIndex, sectionIndex, event.target.value)} required maxLength="100" placeholder={`섹션 ${sectionIndex + 1} 제목`} aria-label={`직무 ${positionIndex + 1} 섹션 ${sectionIndex + 1} 제목`} />{position.sections.length > 1 && <button type="button" className="career-remove" onClick={() => removeSection(positionIndex, sectionIndex)}>섹션 삭제</button>}</div>
                <div className="career-activity-editor">{section.activities.map((activity, activityIndex) => <div key={activityIndex}><textarea value={activity.content} onChange={event => changeActivity(positionIndex, sectionIndex, activityIndex, event.target.value)} required maxLength="1000" rows="2" placeholder="담당 업무와 성과를 입력하세요." aria-label={`직무 ${positionIndex + 1} 섹션 ${sectionIndex + 1} 활동 ${activityIndex + 1}`} />{section.activities.length > 1 && <button type="button" className="career-remove" onClick={() => removeActivity(positionIndex, sectionIndex, activityIndex)}>×</button>}</div>)}<button type="button" className="career-add-activity" onClick={() => addActivity(positionIndex, sectionIndex)}>+ 활동 추가</button></div>
              </div>)}
            </div>
          </fieldset>)}
        </div>
        <button type="button" className="career-cancel wide" onClick={closeModal}>취소</button>
        <button className="submit wide" disabled={submitting}>{submitting ? '저장 중...' : editingId ? '이력 수정' : '이력 저장'}</button>
      </form>
    </EntryModal>}
  </section>
}
