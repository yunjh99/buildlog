import { useEffect, useMemo, useState } from 'react'
import { createTechStack, deleteTechStack, getTechStacks } from '../api/techStackApi'

const categories = [
  ['LANGUAGE', 'Languages'],
  ['FRAMEWORK_LIBRARY', 'Frameworks / Libraries'],
  ['DATABASE', 'Databases'],
  ['TOOL_IDE', 'Tools / IDEs'],
  ['UNCATEGORIZED', 'Uncategorized'],
]

export default function TechStackSelector({ value, onChange }) {
  const [query, setQuery] = useState('')
  const [category, setCategory] = useState('LANGUAGE')
  const [options, setOptions] = useState([])
  const [loading, setLoading] = useState(true)
  const [creating, setCreating] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getTechStacks()
      .then(response => setOptions(response.data?.techStacks ?? response.data ?? []))
      .catch(fetchError => setError(fetchError.message))
      .finally(() => setLoading(false))
  }, [])

  const selectable = useMemo(() => {
    const keyword = query.trim().toLowerCase()
    return options.filter(tech => tech.name.toLowerCase().includes(keyword))
  }, [options, query])

  const groupedOptions = useMemo(() => categories
    .map(([key, label]) => ({ key, label, items: selectable.filter(tech => (tech.category ?? 'UNCATEGORIZED') === key) }))
    .filter(group => group.items.length > 0), [selectable])

  const toggle = id => onChange(value.includes(id) ? value.filter(item => item !== id) : [...value, id])

  const addTechStack = async () => {
    const name = query.trim()
    if (!name || creating) return

    const existing = options.find(tech => tech.name.toLowerCase() === name.toLowerCase())
    if (existing) {
      if (!value.includes(existing.id)) onChange([...value, existing.id])
      setQuery('')
      return
    }

    setCreating(true)
    setError('')

    try {
      await createTechStack({ name, category })
      const response = await getTechStacks()
      const updatedOptions = response.data?.techStacks ?? response.data ?? []
      const created = updatedOptions.find(tech => tech.name.toLowerCase() === name.toLowerCase())

      setOptions(updatedOptions)
      if (created && !value.includes(created.id)) onChange([...value, created.id])
      setQuery('')
    } catch (createError) {
      setError(createError.message)
    } finally {
      setCreating(false)
    }
  }

  const handleSearchKeyDown = event => {
    if (event.key !== 'Enter') return
    event.preventDefault()
    addTechStack()
  }

  const removeTechStack = async tech => {
    if (!window.confirm(`'${tech.name}' 기술 스택을 삭제할까요?`)) return

    setDeletingId(tech.id)
    setError('')

    try {
      await deleteTechStack(tech.id)
      setOptions(current => current.filter(item => item.id !== tech.id))
      if (value.includes(tech.id)) onChange(value.filter(id => id !== tech.id))
    } catch (deleteError) {
      setError(deleteError.message)
    } finally {
      setDeletingId(null)
    }
  }

  return <div className="wide tech-picker">
    <span>기술 스택 /</span>
    <div className="tech-search">
      <select value={category} onChange={event => setCategory(event.target.value)} aria-label="기술 스택 카테고리">
        {categories.slice(0, 4).map(([value, label]) => <option value={value} key={value}>{label}</option>)}
      </select>
      <input value={query} onChange={event => setQuery(event.target.value)} onKeyDown={handleSearchKeyDown} maxLength="50" placeholder="기술 검색 또는 새 기술 입력" aria-label="기술 스택 검색 및 추가" />
      <button type="button" onClick={addTechStack} disabled={!query.trim() || creating}>{creating ? '추가 중...' : '+ 추가하기'}</button>
    </div>
    <div className="tech-options">
      {groupedOptions.map(group => <div className="tech-option-group" key={group.key}><strong>{group.label}</strong><div>{group.items.map(tech => <span className="tech-option" key={tech.id}>
          <button type="button" className={value.includes(tech.id) ? 'selected' : ''} onClick={() => toggle(tech.id)} aria-pressed={value.includes(tech.id)}>{tech.name}</button>
          <button type="button" className="tech-delete" onClick={() => removeTechStack(tech)} disabled={deletingId === tech.id} aria-label={`${tech.name} 삭제`}>×</button>
        </span>)}</div></div>)}
    </div>
    {loading && <small>기술 스택을 불러오는 중...</small>}
    {error && <small className="tech-error">{error}</small>}
    {!loading && !error && selectable.length === 0 && <small>등록된 기술 스택이 없습니다.</small>}
  </div>
}
