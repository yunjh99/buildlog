import { useCallback, useEffect, useState } from 'react'
import { createEducation, deleteEducation, getEducations, updateEducation } from '../api/educationApi'
import { formatMonth } from '../../../shared/utils/date'
import EntryModal from '../../../shared/components/EntryModal'
import './EducationSection.css'

const empty = () => ({ type: 'UNIVERSITY', institution: '', program: '', startDate: '', endDate: '', status: '', description: '' })
const labels = { UNIVERSITY: 'University', BOOTCAMP: 'Bootcamp', COURSE: 'Course' }

export default function EducationSection({ isAdmin = false }) {
  const [items, setItems] = useState([]), [form, setForm] = useState(empty), [editId, setEditId] = useState(null)
  const [modalOpen, setModalOpen] = useState(false), [message, setMessage] = useState('')
  const load = async () => { try { setItems((await getEducations()).data ?? []) } catch (error) { setMessage(error.message) } }
  useEffect(() => { load() }, [])
  const close = useCallback(() => { setModalOpen(false); setEditId(null); setForm(empty()); setMessage('') }, [])
  const add = () => { setEditId(null); setForm(empty()); setMessage(''); setModalOpen(true) }
  const edit = item => { setEditId(item.id); setForm({ ...item, endDate: item.endDate ?? '', status: item.status ?? '', description: item.description ?? '' }); setMessage(''); setModalOpen(true) }
  const change = event => setForm(current => ({ ...current, [event.target.name]: event.target.value }))
  const submit = async event => { event.preventDefault(); try { const payload = { ...form, endDate: form.endDate || null, status: form.status || null, description: form.description || null }; if (editId) await updateEducation(editId, payload); else await createEducation(payload); await load(); close() } catch (error) { setMessage(error.message) } }
  const remove = async item => { if (!window.confirm(`'${item.institution}' 교육을 삭제할까요?`)) return; try { await deleteEducation(item.id); await load() } catch (error) { setMessage(error.message) } }

  return <section className="content-section education-display" id="education">
    <span className="section-number">// EDUCATION</span>
    <div className="section-title"><h2>교육</h2><div className="section-title-actions"><small>{String(items.length).padStart(2, '0')} ENTRIES</small>{isAdmin && <button className="add-entry" onClick={add}>+ 교육 추가</button>}</div></div>
    {message && !modalOpen && <div className="notice" role="status">{message}</div>}
    <div className="education-list">{items.map(item => <article key={item.id}><div><small>{labels[item.type]}</small><h3>{item.institution}</h3><time>{formatMonth(item.startDate)} ~ {item.endDate ? formatMonth(item.endDate) : '진행 중'}</time></div><div><h4>{item.program}</h4>{item.status && <strong>{item.status}</strong>}<p>{item.description}</p>{isAdmin && <span className="entry-actions"><button onClick={() => edit(item)}>수정</button><button onClick={() => remove(item)}>삭제</button></span>}</div></article>)}{!items.length && <div className="empty">등록된 교육이 없습니다.</div>}</div>
    {modalOpen && <EntryModal eyebrow={editId ? 'EDUCATION EDIT' : 'EDUCATION ADD'} title={editId ? '교육 수정' : '교육 추가'} titleId="education-modal-title" onClose={close}>
      {message && <div className="notice" role="status">{message}</div>}
      <form className="create-form" onSubmit={submit}><label>유형 /<select name="type" value={form.type} onChange={change}><option value="UNIVERSITY">대학교</option><option value="BOOTCAMP">부트캠프</option><option value="COURSE">기타 교육</option></select></label><label>기관명 /<input name="institution" value={form.institution} onChange={change} required /></label><label className="wide">전공·과정명 /<input name="program" value={form.program} onChange={change} required /></label><label>시작일 /<input type="date" name="startDate" value={form.startDate} onChange={change} required /></label><label>종료일 /<input type="date" name="endDate" min={form.startDate} value={form.endDate} onChange={change} /></label><label className="wide">학위·수료 상태 /<input name="status" value={form.status} onChange={change} /></label><label className="wide">주요 내용 /<textarea name="description" value={form.description} onChange={change} rows="5" /></label><button type="button" className="manager-cancel wide" onClick={close}>취소</button><button className="submit wide">{editId ? '교육 수정 →' : '교육 저장 →'}</button></form>
    </EntryModal>}
  </section>
}
