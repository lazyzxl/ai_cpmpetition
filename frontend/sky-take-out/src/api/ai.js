import request from '../utils/request'

export const getAISummaryAPI = (data) => {
  return request.post('/ai/summary', data)
}

export const chatWithAiAPI = (data) => {
  return request.post('/ai/chat', data)
}