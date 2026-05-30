import request from '../../utils/request'

export const getUserPageAPI = (params) => {
  const queryParams = {
    page: params.current || params.page || 1,
    pageSize: params.size || params.pageSize || 10,
    username: params.name || params.username
  }
  return request.get('/admin/user/selectPage', { params: queryParams })
}

export const enableUserAPI = (id) => {
  return request.post(`/admin/user/enable/${id}`)
}

export const disableUserAPI = (id) => {
  return request.post(`/admin/user/disable/${id}`)
}

export const deleteUserAPI = (id) => {
  return request.delete(`/admin/user/${id}`)
}