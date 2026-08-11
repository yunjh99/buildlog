import { useCallback, useEffect, useState } from 'react'
import { createCertification, deleteCertification, getCertifications, updateCertification } from '../api/certificationApi'
import EntryModal from '../../../shared/components/EntryModal'
import { formatMonth } from '../../../shared/utils/date'
import './CertificationSection.css'

const empty = () => ({ name: '', issuer: '', acquiredDate: '', credentialId: '', credentialUrl: '' })

export default function CertificationSection({ isAdmin = false }) {
  const [items, setItems] = useState([]), [form, setForm] = useState(empty), [editId, setEditId] = useState(null)
  const [modalOpen, setModalOpen] = useState(false), [message, setMessage] = useState('')
  const load = async () => { try { setItems((await getCertifications()).data ?? []) } catch (error) { setMessage(error.message) } }
  useEffect(() => { load() }, [])
  const close = useCallback(() => { setModalOpen(false); setEditId(null); setForm(empty()); setMessage('') }, [])
  const add = () => { setEditId(null); setForm(empty()); setMessage(''); setModalOpen(true) }
  const edit = item => { setEditId(item.id); setForm({ ...item, credentialId: item.credentialId ?? '', credentialUrl: item.credentialUrl ?? '' }); setMessage(''); setModalOpen(true) }
  const change = event => setForm(current => ({ ...current, [event.target.name]: event.target.value }))
  const submit = async event => { event.preventDefault(); try { const payload = { ...form, credentialId: form.credentialId || null, credentialUrl: form.credentialUrl || null }; if (editId) await updateCertification(editId, payload); else await createCertification(payload); await load(); close() } catch (error) { setMessage(error.message) } }
  const remove = async item => { if (!window.confirm(`'${item.name}' 자격증을 삭제할까요?`)) return; try { await deleteCertification(item.id); await load() } catch (error) { setMessage(error.message) } }

  return <section className="content-section certification-display" id="certifications">
    <span className="section-number">// CERTIFICATIONS</span>
    <div className="section-title"><h2>자격증</h2><div className="section-title-actions"><small>{String(items.length).padStart(2, '0')} ENTRIES</small>{isAdmin && <button className="add-entry" onClick={add}>+ 자격증 추가</button>}</div></div>
    {message && !modalOpen && <div className="notice" role="status">{message}</div>}
    <div className="certification-list">{items.map(item => <article key={item.id}><time>{formatMonth(item.acquiredDate)}</time><div><h3>{item.name}</h3><p>{item.issuer}{item.credentialId && ` · ${item.credentialId}`}</p>{item.credentialUrl && <a href={item.credentialUrl} target="_blank" rel="noreferrer">인증 보기 ↗</a>}{isAdmin && <span className="entry-actions"><button onClick={() => edit(item)}>수정</button><button onClick={() => remove(item)}>삭제</button></span>}</div></article>)}{!items.length && <div className="empty">등록된 자격증이 없습니다.</div>}</div>
    {modalOpen && <EntryModal eyebrow={editId ? 'CERTIFICATION EDIT' : 'CERTIFICATION ADD'} title={editId ? '자격증 수정' : '자격증 추가'} titleId="certification-modal-title" onClose={close}>
      {message && <div className="notice" role="status">{message}</div>}
      <form className="create-form" onSubmit={submit}><label>자격증명 /<input name="name" value={form.name} onChange={change} required /></label><label>발급기관 /<input name="issuer" value={form.issuer} onChange={change} required /></label><label>취득일 /<input type="date" name="acquiredDate" value={form.acquiredDate} onChange={change} required /></label><label>자격번호 /<input name="credentialId" value={form.credentialId} onChange={change} /></label><label className="wide">인증 URL /<input type="url" name="credentialUrl" value={form.credentialUrl} onChange={change} /></label><button type="button" className="manager-cancel wide" onClick={close}>취소</button><button className="submit wide">{editId ? '자격증 수정 →' : '자격증 저장 →'}</button></form>
    </EntryModal>}
  </section>
}
