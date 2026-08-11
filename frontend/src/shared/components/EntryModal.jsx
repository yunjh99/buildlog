import { useEffect } from 'react'
import { createPortal } from 'react-dom'

export default function EntryModal({ eyebrow, title, titleId, onClose, children }) {
  useEffect(() => {
    const closeOnEscape = event => { if (event.key === 'Escape') onClose() }
    document.body.classList.add('modal-open')
    window.addEventListener('keydown', closeOnEscape)
    return () => {
      document.body.classList.remove('modal-open')
      window.removeEventListener('keydown', closeOnEscape)
    }
  }, [onClose])

  return createPortal(
    <div className="edit-modal" role="dialog" aria-modal="true" aria-labelledby={titleId}
      onMouseDown={event => { if (event.target === event.currentTarget) onClose() }}>
      <section className="edit-modal-panel">
        <div className="edit-modal-header">
          <div><span className="section-number">// {eyebrow}</span><h2 id={titleId}>{title}</h2></div>
          <button type="button" className="edit-modal-close" onClick={onClose} aria-label="창 닫기">×</button>
        </div>
        {children}
      </section>
    </div>, document.body,
  )
}
