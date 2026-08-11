import { useCallback, useEffect, useMemo, useState } from 'react'
import './Portfolio.css'
import './WorkText.css'
import CareerSection from './features/career/components/CareerSection'
import { deleteProject, getProjects } from './features/project/api/projectApi'
import ProjectManager from './features/project/components/ProjectManager'
import ProfileEditor from './features/profile/components/ProfileEditor'
import { getProfile } from './features/profile/api/profileApi'
import TechStackShowcase from './features/tech-stack/components/TechStackShowcase'
import { formatMonth } from './shared/utils/date'
import SideNavigation from './shared/components/SideNavigation'
import EducationSection from './features/education/components/EducationSection'
import CertificationSection from './features/certification/components/CertificationSection'

const defaultProfile = {
  heroLine1: '좋은 서비스는 고객이 편하게 사용할 수 있어야 하고,',
  heroLine2: '좋은 코드는 다른 사람이 쉽게 읽고 이어갈 수 있어야 한다고 생각합니다.',
  email: '', githubUrl: '', blogUrl: '', aboutTitle: '결과뿐 아니라', aboutEmphasis: '과정을 남깁니다.',
  aboutParagraph1: 'B2B 웹 서비스 운영·유지보수를 담당하며 고객 문의를 바탕으로 오류를 재현하고, 데이터와 기존 소스 코드를 추적해 문제의 원인을 해결해 왔습니다. 업체별 상품 정보 처리 오류를 수정하고, 대량 상품 조회 시 발생하던 화면 지연을 렌더링 방식 개선으로 완화했습니다.',
  aboutParagraph2: '운영 환경에서는 PostgreSQL과 JavaScript·PHP 기반의 기존 기능을 다루며 데이터 흐름과 비즈니스 로직을 이해했습니다. 프로젝트에서는 Java와 Spring Boot를 중심으로 REST API를 설계하고, JPA를 활용해 도메인과 연관관계를 모델링하며 안정적이고 유지보수하기 좋은 백엔드를 구현해 왔습니다.',
}

export default function PortfolioApp({ isAdmin = false }) {
  const [projects, setProjects] = useState([])
  const [page, setPage] = useState(0)
  const [hasNext, setHasNext] = useState(false)
  const [loading, setLoading] = useState(true)
  const [notice, setNotice] = useState('')
  const [activeTag, setActiveTag] = useState('ALL')
  const [editingProject, setEditingProject] = useState(null)
  const [creatingProject, setCreatingProject] = useState(false)
  const [profile, setProfile] = useState(defaultProfile)

  const load = useCallback(async (nextPage = 0, append = false) => {
    setLoading(true)
    setNotice('')
    try {
      const response = await getProjects(nextPage, 4)
      const items = response.data?.projects ?? []
      setProjects(current => append ? [...current, ...items] : items)
      setHasNext(Boolean(response.data?.hasNext))
      setPage(nextPage)
    } catch (error) {
      setNotice(error.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])
  useEffect(() => { getProfile().then(response => setProfile(response.data ?? defaultProfile)).catch(() => {}) }, [])

  const tags = useMemo(() => [...new Set(projects.flatMap(project =>
    project.techStacks?.map(tech => typeof tech === 'string' ? tech : tech.name) ?? []
  ))], [projects])
  const visible = activeTag === 'ALL' ? projects : projects.filter(project =>
    project.techStacks?.some(tech => (typeof tech === 'string' ? tech : tech.name) === activeTag)
  )

  const removeProject = async project => {
    if (!window.confirm(`'${project.name}' 프로젝트를 삭제할까요?`)) return
    try {
      await deleteProject(project.id)
      if (editingProject?.id === project.id) setEditingProject(null)
      setNotice('프로젝트를 삭제했습니다.')
      await load()
    } catch (error) {
      setNotice(error.message)
    }
  }

  return <div className={`portfolio-shell${isAdmin ? ' admin-mode' : ''}`}>
    <header className="topbar">
      <a className="brand" href="#top">&gt; build.log</a>
      <nav><a href="#about">소개</a><a href="#career">이력</a><a href="#education">교육</a><a href="#projects">프로젝트</a><a href="#tech-stack">기술</a></nav>
    </header>
    <SideNavigation />
    <main id="top">
      <section className="hero grid-bg" id="introduce">
        <div className="hero-glow"/><div className="hero-content"><span className="section-number">// BACKEND DEVELOPER</span><h1><em>안녕하세요,</em><br/><span className="hero-name">개발자 윤정환입니다.</span></h1><p className="terminal"><b>$</b> backend developer<span>_</span></p><p className="hero-copy">{profile.heroLine1}<br/>{profile.heroLine2}</p><div className="contact-links" aria-label="연락처">{profile.email ? <a href={`mailto:${profile.email}`}>EMAIL ↗</a> : <span>EMAIL ↗</span>}{profile.githubUrl ? <a href={profile.githubUrl} target="_blank" rel="noreferrer">GITHUB ↗</a> : <span>GITHUB ↗</span>}{profile.blogUrl ? <a href={profile.blogUrl} target="_blank" rel="noreferrer">BLOG ↗</a> : <span>BLOG ↗</span>}</div><div className="hero-actions"><a className="button ghost" href="#about">소개</a><a className="button ghost" href="#career">이력</a><a className="button primary" href="#projects">프로젝트</a></div></div>
        <a className="scroll-indicator" href="#about" aria-label="소개 영역으로 이동"><span>SCROLL</span><b>↓</b></a>
        <small className="vertical-note">BUILD LOG · 2026</small>
      </section>

      <section className="content-section" id="about">
        <span className="section-number">// ABOUT</span><div className="section-grid"><h2>{profile.aboutTitle}<br/><em>{profile.aboutEmphasis}</em></h2><div><p className="about-content">{[profile.aboutParagraph1, profile.aboutParagraph2].filter(Boolean).join('\n\n')}</p><div className="stats"><span><b>{projects.length}</b>projects</span><span><b>{tags.length}</b>technologies</span><span><b>EXPERIENCE</b>B2B SERVICE</span></div></div></div>
      </section>

      <TechStackShowcase />

      <section className="content-section" id="projects">
        <span className="section-number">// PROJECTS</span><div className="section-title"><h2>프로젝트</h2><div className="section-title-actions"><small>{String(projects.length).padStart(2, '0')} ENTRIES</small>{isAdmin && <button className="add-entry" onClick={() => setCreatingProject(true)}>+ 프로젝트 추가</button>}</div></div>
        <div className="filters"><button className={activeTag === 'ALL' ? 'active' : ''} onClick={() => setActiveTag('ALL')}>ALL ({projects.length})</button>{tags.map(tag => <button key={tag} className={activeTag === tag ? 'active' : ''} onClick={() => setActiveTag(tag)}>{tag}</button>)}</div>
        {notice && <div className="notice" role="status">{notice}</div>}
        <div className="project-list">{visible.map((project, index) => <article className="project-card" key={project.id}>
          <span className="project-index">{String(index + 1).padStart(2, '0')}</span>
          <div><div className="project-heading"><h3>{project.name}</h3><div className="project-card-tools"><small>{project.type === 'TEAM' ? `팀 프로젝트${project.teamSize ? ` · ${project.teamSize}명` : ''}` : '개인 프로젝트'}</small>{isAdmin && <div className="project-actions"><button type="button" onClick={() => setEditingProject(project)}>수정</button><button type="button" onClick={() => removeProject(project)}>삭제</button></div>}</div></div>
          <p>{project.description || '프로젝트에 대한 설명이 아직 등록되지 않았습니다.'}</p>
          {project.contributions?.length > 0 && <div className="work-list">{project.contributions.map(item => <div key={item.id ?? item.title}><strong>{item.title}</strong>{item.detail && <span>{item.detail}</span>}</div>)}</div>}
          <div className="project-footer"><div className="tags">{project.techStacks?.map(tech => { const name = typeof tech === 'string' ? tech : tech.name; return <button key={typeof tech === 'string' ? tech : tech.id} onClick={() => setActiveTag(name)}>{name}</button> })}</div><time>{formatMonth(project.startDate)} ~ {project.endDate ? formatMonth(project.endDate) : '진행 중'}</time></div></div>
        </article>)}{!loading && visible.length === 0 && <div className="empty">표시할 프로젝트가 없습니다.</div>}</div>
        {loading && <div className="empty">프로젝트를 불러오는 중...</div>}{hasNext && !loading && <button className="more" onClick={() => load(page + 1, true)}>더보기 ↓</button>}
      </section>

      {editingProject && <ProjectManager project={editingProject} onChanged={() => load()} onClose={() => setEditingProject(null)} />}
      {creatingProject && <ProjectManager project={null} onChanged={() => load()} onClose={() => setCreatingProject(false)} />}

      <CareerSection isAdmin={isAdmin}/>
      <EducationSection isAdmin={isAdmin}/>
      <CertificationSection isAdmin={isAdmin}/>
      {isAdmin && <ProfileEditor profile={profile} onSaved={setProfile}/>} 
    </main>
    <footer><span>&gt; build.log</span><span>DESIGNED FROM FIGMA · BUILT WITH REACT</span></footer>
  </div>
}
