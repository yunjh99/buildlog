export function formatMonth(date) {
  return date ? date.slice(0, 7).replace('-', '.') : ''
}
