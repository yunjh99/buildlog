export function formatMonth(date) {
  return date ? date.slice(0, 7).replace('-', '.') : ''
}

export function formatPeriod(startDate, endDate, ongoingLabel = '진행 중') {
  const start = formatMonth(startDate)
  if (!endDate) return `${start}\u00a0~\u00a0${ongoingLabel}`

  const end = formatMonth(endDate)
  return start === end ? start : `${start}\u00a0~\u00a0${end}`
}
