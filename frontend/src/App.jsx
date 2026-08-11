import { useCallback, useEffect, useState } from 'react'
import './App.css'

const createEmptyForm = () => ({
  name: '',
  startDate: '',
  endDate: '',
  type: 'PERSONAL',
  teamSize: '',
  description: '',
  contributions: [{ title: '', detail: '' }],
})

async function request(url, options) {
  const response = await fetch(url, options)
  const body = await response.json().catch(() => null)

  if (!response.ok) {
    throw new Error(body?.message ?? `요청에 실패했습니다. (${response.status})`)
  }

  return body
}

function App() {
  const [form, setForm] = useState(createEmptyForm)
  const [projects, setProjects] = useState([])
  const [page, setPage] = useState(0)
  const [hasNext, setHasNext] = useState(false)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [notice, setNotice] = useState('')

  const loadProjects = useCallback(async (nextPage = 0, append = false) => {
    setLoading(true)
    setNotice('')

    try {
      const response = await request(`/api/projects?page=${nextPage}&size=6`)
      const nextProjects = response.data?.projects ?? []
      setProjects((current) => append ? [...current, ...nextProjects] : nextProjects)
      setHasNext(Boolean(response.data?.hasNext))
      setPage(nextPage)
    } catch (error) {
      setNotice(error.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadProjects()
  }, [loadProjects])

  const updateForm = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  const updateContribution = (index, field, value) => {
    setForm((current) => ({
      ...current,
      contributions: current.contributions.map((contribution, contributionIndex) =>
        contributionIndex === index
          ? { ...contribution, [field]: value }
          : contribution,
      ),
    }))
  }

  const addContribution = () => {
    setForm((current) => ({
      ...current,
      contributions: [...current.contributions, { title: '', detail: '' }],
    }))
  }

  const removeContribution = (index) => {
    setForm((current) => ({
      ...current,
      contributions: current.contributions.filter((_, contributionIndex) => contributionIndex !== index),
    }))
  }

  const createProject = async (event) => {
    event.preventDefault()
    setSubmitting(true)
    setNotice('')

    const payload = {
      name: form.name,
      startDate: form.startDate,
      endDate: form.endDate || null,
      type: form.type,
      teamSize: form.type === 'TEAM' ? Number(form.teamSize) : null,
      description: form.description || null,
      techStacks: [],
      contributions: form.contributions
        .filter((contribution) => contribution.title.trim() !== '')
        .map((contribution, index) => ({
          title: contribution.title,
          detail: contribution.detail || null,
          displayOrder: index + 1,
        })),
    }

    try {
      const response = await request('/api/projects', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })
      setForm(createEmptyForm())
      setNotice(response.message ?? '프로젝트를 생성했습니다.')
      await loadProjects()
    } catch (error) {
      setNotice(error.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main>
      <header className="page-header">
        <div>
          <p className="eyebrow">BUILD LOG</p>
          <h1>프로젝트 기록</h1>
          <p>프로젝트를 생성하고 주요 작업을 한눈에 확인하세요.</p>
        </div>
        <span className="project-count">{projects.length}개 표시 중</span>
      </header>

      {notice && <div className="notice" role="status">{notice}</div>}

      <section className="layout">
        <form className="panel create-form" onSubmit={createProject}>
          <div className="section-heading">
            <span>NEW</span>
            <h2>프로젝트 생성</h2>
          </div>

          <label>
            프로젝트명
            <input name="name" value={form.name} onChange={updateForm} required maxLength="100" />
          </label>

          <div className="field-row">
            <label>
              시작일
              <input type="date" name="startDate" value={form.startDate} onChange={updateForm} required />
            </label>
            <label>
              종료일
              <input type="date" name="endDate" value={form.endDate} min={form.startDate} onChange={updateForm} />
            </label>
          </div>

          <div className="field-row">
            <label>
              유형
              <select name="type" value={form.type} onChange={updateForm}>
                <option value="PERSONAL">개인</option>
                <option value="TEAM">팀</option>
              </select>
            </label>
            {form.type === 'TEAM' && (
              <label>
                팀 인원
                <input type="number" name="teamSize" value={form.teamSize} min="2" onChange={updateForm} required />
              </label>
            )}
          </div>

          <label>
            설명
            <textarea name="description" value={form.description} onChange={updateForm} rows="3" maxLength="1000" />
          </label>

          <div className="subsection">
            <div className="subsection-title">
              <h3>주요 작업 <small>선택</small></h3>
              <button type="button" className="text-button" onClick={addContribution}>+ 작업 추가</button>
            </div>
            {form.contributions.map((contribution, index) => (
              <div className="contribution-editor" key={index}>
                <div className="editor-heading">
                  <strong>작업 {index + 1}</strong>
                  {form.contributions.length > 1 && (
                    <button type="button" className="remove-button" onClick={() => removeContribution(index)}>삭제</button>
                  )}
                </div>
                <label>
                  작업명
                  <input
                    value={contribution.title}
                    onChange={(event) => updateContribution(index, 'title', event.target.value)}
                    maxLength="200"
                  />
                </label>
                <label>
                  상세 내용
                  <textarea
                    value={contribution.detail}
                    onChange={(event) => updateContribution(index, 'detail', event.target.value)}
                    rows="3"
                  />
                </label>
              </div>
            ))}
          </div>

          <button className="primary-button" disabled={submitting}>
            {submitting ? '저장 중…' : '프로젝트 저장'}
          </button>
        </form>

        <section className="project-section">
          <div className="section-heading">
            <span>ARCHIVE</span>
            <h2>프로젝트 목록</h2>
          </div>

          {!loading && projects.length === 0 && (
            <div className="empty-state">아직 기록된 프로젝트가 없습니다.</div>
          )}

          <div className="project-list">
            {projects.map((project) => (
              <article className="project-card" key={project.id}>
                <div className="card-topline">
                  <span className={`type-badge ${project.type.toLowerCase()}`}>
                    {project.type === 'TEAM' ? 'TEAM' : 'PERSONAL'}
                  </span>
                  <time>{project.startDate} — {project.endDate ?? '진행 중'}</time>
                </div>
                <h3>{project.name}</h3>
                {project.description && <p>{project.description}</p>}
                {project.techStacks?.length > 0 && (
                  <div className="tags">
                    {project.techStacks.map((stack) => <span key={stack}>{stack}</span>)}
                  </div>
                )}
                {project.contributions?.length > 0 && (
                  <div className="contributions">
                    {project.contributions.map((item) => (
                      <div key={item.id}>
                        <strong>{item.title}</strong>
                        {item.detail && <p>{item.detail}</p>}
                      </div>
                    ))}
                  </div>
                )}
              </article>
            ))}
          </div>

          {loading && <div className="loading">불러오는 중…</div>}
          {hasNext && !loading && (
            <button className="more-button" onClick={() => loadProjects(page + 1, true)}>
              더보기
            </button>
          )}
        </section>
      </section>
    </main>
  )
}

export default App
