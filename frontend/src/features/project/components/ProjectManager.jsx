import { useEffect, useState } from 'react'
import { createPortal } from 'react-dom'
import TechStackSelector from '../../tech-stack/components/TechStackSelector'
import { createProject, updateProject } from '../api/projectApi'
import './ProjectManager.css'

const toForm = project => ({
  name: project?.name ?? '',
  startDate: project?.startDate ?? '',
  endDate: project?.endDate ?? '',
  type: project?.type ?? 'PERSONAL',
  teamSize: project?.teamSize ?? '',
  description: project?.description ?? '',
  githubUrl: project?.githubUrl ?? '',
  siteUrl: project?.siteUrl ?? '',
  techStackIds: project?.techStacks?.map(tech => tech.id) ?? [],
  contributions: project?.contributions?.map(item => ({ title: item.title, detail: item.detail ?? '' })) ?? [{ title: '', detail: '' }],
})

export default function ProjectManager({ project, onChanged, onClose }) {
  const [form, setForm] = useState(() => toForm(project))
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    setForm(toForm(project))
    setMessage('')
  }, [project])

  useEffect(() => {
    const closeOnEscape = event => { if (event.key === 'Escape') onClose() }
    document.body.classList.add('modal-open')
    window.addEventListener('keydown', closeOnEscape)
    return () => {
      document.body.classList.remove('modal-open')
      window.removeEventListener('keydown', closeOnEscape)
    }
  }, [onClose])

  const change = event => {
    const { name, value } = event.target
    setForm(current => ({ ...current, [name]: value }))
  }

  const changeWork = (index, field, value) => setForm(current => ({
    ...current,
    contributions: current.contributions.map((item, i) => i === index ? { ...item, [field]: value } : item),
  }))

  const submit = async event => {
    event.preventDefault()
    setSubmitting(true)
    setMessage('')
    const payload = {
      ...form,
      endDate: form.endDate || null,
      teamSize: form.type === 'TEAM' ? Number(form.teamSize) : null,
      description: form.description || null,
      githubUrl: form.githubUrl || null,
      siteUrl: form.siteUrl || null,
      techStacks: form.techStackIds.map((techStackId, index) => ({ techStackId, displayOrder: index + 1 })),
      contributions: form.contributions.filter(item => item.title.trim()).map((item, index) => ({
        title: item.title,
        detail: item.detail || null,
        displayOrder: index + 1,
      })),
    }
    delete payload.techStackIds

    try {
      if (project) await updateProject(project.id, payload)
      else await createProject(payload)
      setMessage(project ? '프로젝트를 수정했습니다.' : '프로젝트를 등록했습니다.')
      await onChanged()
      onClose()
    } catch (error) {
      setMessage(error.message)
    } finally {
      setSubmitting(false)
    }
  }

  return createPortal(<div className="edit-modal" role="dialog" aria-modal="true" aria-labelledby="project-edit-title" onMouseDown={event => { if (event.target === event.currentTarget) onClose() }}>
    <section className="edit-modal-panel project-manager">
    <div className="edit-modal-header"><div><span className="section-number">// PROJECT {project ? 'EDIT' : 'ADD'}</span><h2 id="project-edit-title">프로젝트 {project ? '수정' : '추가'}</h2></div><button type="button" className="edit-modal-close" onClick={onClose} aria-label="창 닫기">×</button></div>
    {message && <div className="notice" role="status">{message}</div>}

    <form className="create-form manager-form" onSubmit={submit}>
      <label className="wide">프로젝트명 /<input name="name" value={form.name} onChange={change} required maxLength="100" /></label>
      <label>시작일 /<input type="date" name="startDate" value={form.startDate} onChange={change} required /></label>
      <label>종료일 /<input type="date" name="endDate" value={form.endDate} min={form.startDate} onChange={change} /></label>
      <label>유형 /<select name="type" value={form.type} onChange={change}><option value="PERSONAL">개인</option><option value="TEAM">팀</option></select></label>
      {form.type === 'TEAM' && <label>팀 인원 /<input type="number" name="teamSize" value={form.teamSize} min="2" onChange={change} required /></label>}
      <label className="wide">설명 /<textarea name="description" value={form.description} onChange={change} rows="4" maxLength="1000" /></label>
      <label className="wide">GitHub 주소 /<input type="url" name="githubUrl" value={form.githubUrl} onChange={change} maxLength="500" placeholder="https://github.com/username/repository" /></label>
      <label className="wide">사이트 주소 /<input type="url" name="siteUrl" value={form.siteUrl} onChange={change} maxLength="500" placeholder="https://example.com" /></label>
      <TechStackSelector value={form.techStackIds} onChange={techStackIds => setForm(current => ({ ...current, techStackIds }))} />
      <div className="wide work-editor">
        <div className="editor-title"><span>주요 작업 /</span><button type="button" onClick={() => setForm(current => ({ ...current, contributions: [...current.contributions, { title: '', detail: '' }] }))}>+ 작업 추가</button></div>
        {form.contributions.map((item, index) => <div className="work-row" key={index}><input value={item.title} onChange={event => changeWork(index, 'title', event.target.value)} placeholder="작업 제목 (선택)" /><textarea value={item.detail} onChange={event => changeWork(index, 'detail', event.target.value)} rows="2" placeholder="상세 내용" />{form.contributions.length > 1 && <button type="button" className="remove" onClick={() => setForm(current => ({ ...current, contributions: current.contributions.filter((_, i) => i !== index) }))}>삭제</button>}</div>)}
      </div>
      <button type="button" className="manager-cancel wide" onClick={onClose}>취소</button>
      <button className="submit wide" disabled={submitting}>{submitting ? '저장 중...' : `프로젝트 ${project ? '수정' : '저장'} →`}</button>
    </form>
    </section>
  </div>, document.body)
}
