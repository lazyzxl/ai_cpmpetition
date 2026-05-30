import request from '../utils/request'

export const getCompetitionByIdAPI = (id) => {
  return request.get(`/competition/${id}`)
}

export const careCompetitionAPI = (id) => {
  return request.post(`/competition/${id}`)
}

export const uncareCompetitionAPI = (id) => {
  return request.delete(`/competition/${id}`)
}