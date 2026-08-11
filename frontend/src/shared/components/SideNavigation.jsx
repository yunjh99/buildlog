import { useEffect, useState } from 'react'
import './SideNavigation.css'

const items = [
  ['introduce', 'INTRODUCE'],
  ['tech-stack', 'SKILL'],
  ['career', 'EXPERIENCE'],
  ['projects', 'PROJECT'],
  ['education', 'EDUCATION'],
  ['certifications', 'CERTIFICATIONS'],
]

export default function SideNavigation() {
  const [active, setActive] = useState('introduce')

  useEffect(() => {
    let frameId
    const updateActive = () => {
      const guideLine = window.innerHeight * .4
      const sections = items
        .map(([id]) => document.getElementById(id))
        .filter(Boolean)
      const current = sections.reduce((selected, section) =>
        section.getBoundingClientRect().top <= guideLine ? section : selected
      , sections[0])
      if (current) setActive(current.id)
    }
    const onScroll = () => {
      window.cancelAnimationFrame(frameId)
      frameId = window.requestAnimationFrame(updateActive)
    }
    updateActive()
    window.addEventListener('scroll', onScroll, { passive: true })
    window.addEventListener('resize', onScroll)
    return () => {
      window.cancelAnimationFrame(frameId)
      window.removeEventListener('scroll', onScroll)
      window.removeEventListener('resize', onScroll)
    }
  }, [])

  return <nav className="side-navigation" aria-label="페이지 섹션">
    {items.map(([id, label]) => <a key={id} href={`#${id}`} className={active === id ? 'active' : ''}>{label}</a>)}
  </nav>
}
