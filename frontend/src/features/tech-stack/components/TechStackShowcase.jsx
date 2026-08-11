import { useEffect, useMemo, useState } from 'react'
import { getTechStacks } from '../api/techStackApi'

const categories = [
  ['LANGUAGE', 'Languages'],
  ['FRAMEWORK_LIBRARY', 'Frameworks / Libraries'],
  ['DATABASE', 'Databases'],
  ['TOOL_IDE', 'Tools / IDEs'],
  ['UNCATEGORIZED', 'Uncategorized'],
]

export default function TechStackShowcase() {
  const [techStacks, setTechStacks] = useState([])

  useEffect(() => {
    getTechStacks().then(response => setTechStacks(response.data ?? [])).catch(() => setTechStacks([]))
  }, [])

  const groups = useMemo(() => categories.map(([key, label]) => ({
    key,
    label,
    items: techStacks.filter(tech => (tech.category ?? 'UNCATEGORIZED') === key),
  })).filter(group => group.items.length > 0), [techStacks])

  return <section className="content-section tech-stack-section" id="tech-stack">
    <span className="section-number">// TECH STACK</span>
    <div className="section-title"><h2>기술 스택</h2><small>{String(techStacks.length).padStart(2, '0')} TECHNOLOGIES</small></div>
    <div className="stack-showcase">
      {groups.map(group => <article key={group.key}><h3>{group.label}</h3><div>{group.items.map(tech => <span key={tech.id}>{tech.name}</span>)}</div></article>)}
      {groups.length === 0 && <div className="empty">등록된 기술 스택이 없습니다.</div>}
    </div>
  </section>
}
