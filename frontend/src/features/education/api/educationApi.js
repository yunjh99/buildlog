import { apiClient } from '../../../shared/api/apiClient'
export const getEducations=()=>apiClient('/api/educations')
export const createEducation=p=>apiClient('/api/educations',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(p)})
export const updateEducation=(id,p)=>apiClient(`/api/educations/${id}`,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(p)})
export const deleteEducation=id=>apiClient(`/api/educations/${id}`,{method:'DELETE'})
