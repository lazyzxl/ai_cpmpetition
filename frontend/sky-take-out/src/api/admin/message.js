import request from '../../utils/request'

export const getMessagePageAPI = (params) => {
  return request.get('/admin/message/selectPage', { params })
}

export const deleteMessageAPI = (id) => {
  return request.delete(`/admin/message/${id}`)
}