import { apiClient } from '../../../shared/api/apiClient'
export const getCertifications=()=>apiClient('/api/certifications')
export const createCertification=p=>apiClient('/api/certifications',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(p)})
export const updateCertification=(id,p)=>apiClient(`/api/certifications/${id}`,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(p)})
export const deleteCertification=id=>apiClient(`/api/certifications/${id}`,{method:'DELETE'})
