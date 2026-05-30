import request from '../utils/request'

export const getHotCompetitionsAPI = (limit = 10) => {
  return request.get('/home/hot-competitions', { params: { limit } })
}

export const getRecommendedCompetitionsAPI = (limit = 10) => {
  return request.get('/home/recommended-competitions', { params: { limit } })
}

export const recordViewAPI = (competitionId) => {
  return request.post('/home/record-view', null, { params: { competitionId } })
}

export const getAllCompetitionsAPI = () => {
  return request.get('/home/all-competitions')
}

export const getHomeStatisticsAPI = () => {
  return request.get('/home/statistics')
}