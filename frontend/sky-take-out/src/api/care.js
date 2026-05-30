import request from '../utils/request'

export const getCareListAPI = (params) => {
  return request.get('/user/care', { 
    params: {
      page: params.current,
      pageSize: params.size
    } 
  })
}

export const deleteCareAPI = (ids) => {
  return request.delete('/user/care/delete', { params: { ids: ids.join(',') } })
}

export const checkCareAPI = (competitionId) => {
  return request.get('/user/care/check', { params: { competitionId } })
}